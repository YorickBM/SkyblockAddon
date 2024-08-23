package yorickbm.skyblockaddon.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.SkyblockAddonWorldCapability;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.gui.interfaces.GuiContext;
import yorickbm.skyblockaddon.gui.interfaces.SkyblockAddonMenuProvider;
import yorickbm.skyblockaddon.gui.json.GuiAction;
import yorickbm.skyblockaddon.gui.json.GuiHolder;
import yorickbm.skyblockaddon.gui.util.FillerPattern;
import yorickbm.skyblockaddon.gui.util.GuiActionable;
import yorickbm.skyblockaddon.gui.util.TargetHolder;
import yorickbm.skyblockaddon.registries.BiomeRegistry;
import yorickbm.skyblockaddon.registries.IslandRegistry;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;

import java.util.Optional;

public class ServerGui extends AbstractContainerMenu {
    private static final Logger LOGGER = LogManager.getLogger();

    protected final SimpleContainer inventory;
    protected final GuiContext sourceContext;
    protected final Player sourceEntity;
    protected final GuiHolder guiContent;

    protected final GuiListener event_bus;

    private int page = 0;
    private int maxPage = 1;

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public static SkyblockAddonMenuProvider getProvider(GuiHolder holder) {
        return new SkyblockAddonMenuProvider() {
            GuiContext context = null;

            @Override
            public void setContext(GuiContext context) {
                this.context = context;
            }

            @Override
            public @NotNull Component getDisplayName() {
                try {
                    return holder.getTitle(context);
                } catch (Exception ex) {
                    return new TextComponent("Invalid GUI Title").withStyle(ChatFormatting.RED);
                }
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
                return new ServerGui(i, inventory, holder, this.context, player);
            }
        };
    }

    protected ServerGui(int syncId, Inventory playerInventory, GuiHolder content, GuiContext context, Player entity) {
        super(fromRows(content.getRows()), syncId);

        this.event_bus = new GuiListener();
        this.inventory = new SimpleContainer(content.getRows() * 9);

        this.sourceContext = context;
        this.sourceEntity = entity;
        this.guiContent = content;

        int i = (content.getRows() - 4) * 18;
        int n, m;

        //Register our inventory slots
        for (n = 0; n < content.getRows(); ++n) {
            for (m = 0; m < 9; ++m) {
                this.addSlot(new Slot(this.inventory, m + n * 9, 8 + m * 18, 18 + n * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return false;
                    }

                    @Override
                    public boolean mayPickup(@NotNull Player playerEntity) {
                        return false;
                    }
                });
            }
        }

        // Register players inventory (Non toolbar slots)
        for (n = 0; n < 3; ++n) {
            for (m = 0; m < 9; ++m) {
                addSlot(new Slot(playerInventory, m + n * 9 + 9, 8 + m * 18, 103 + n * 18 + i) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return false;
                    }

                    @Override
                    public boolean mayPickup(@NotNull Player playerEntity) {
                        return false;
                    }
                });
            }
        }

        // Register players toolbar slots
        for (n = 0; n < 9; ++n) {
            this.addSlot(new Slot(playerInventory, n, 8 + n * 18, 161 + i) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false;
                }

                @Override
                public boolean mayPickup(@NotNull Player playerEntity) {
                    return false;
                }
            });
        }

        this.draw();
    }

    /**
     * Store GUI data into NBT tag
     * @param tag - NBT Tag
     */
    private void setGuiNBT(CompoundTag tag) {
        tag.putString("pagenum", (this.page+1)+"");
        tag.putString("maxpage", this.maxPage+"");
    }

    /**
     * Trigger next page
     */
    public void nextPage() {
        this.page += 1;
        if(this.page >= this.maxPage) this.page = 0;
        this.draw();
    }

    /**
     * Trigger previous page
     */
    public void previousPage() {
        this.page -= 1;
        if(this.page < 0) this.page = maxPage - 1;

        this.draw();
    }

    /**
     * Redraw contents of GUI
     */
    public void draw() {
        inventory.clearContent();

        processPreFillers();
        processItems();
        processPostFillers();
    }

    /**
     * Close container for the source entity.
     * Source Entity - Who opened the GUI
     */
    public void close() {
        this.sourceEntity.closeContainer();
    }

    /**
     * Process Gui Holders Items
     */
    private void processItems() {
        this.guiContent.getItems().forEach(guiItem -> {
            CompoundTag tag = new CompoundTag();
            this.setGuiNBT(tag);

            ItemStack item = guiItem.getItem().getItemStack(this.sourceContext, guiItem.getItem().getTag(tag));
            setItem(guiItem.getSlot(), 0, item);

            this.attachAction(guiItem, guiItem.getSlot());
        });
    }

    /**
     * Process Gui Holders Fillers
     */
    private void processPreFillers() {
        this.guiContent.getFillers().stream().filter(f -> f.getPattern() == FillerPattern.INSIDE).forEach(filler -> {
            ItemStack slotItem = filler.getItem().getItemStack(this.sourceContext, filler.getItem().getTag(new CompoundTag()));
            SkyblockAddonRegistry registry = null;

            CompoundTag modTag = slotItem.getOrCreateTagElement(SkyblockAddon.MOD_ID);

            if(modTag.contains("registry")) {
                switch (modTag.getString("registry")) {
                    case "BiomeRegistry":
                        registry = new BiomeRegistry();
                        break;
                    case "IslandRegistry":
                        Optional<SkyblockAddonWorldCapability> cap = this.sourceEntity.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).resolve();
                        if(cap.isEmpty()) break; //Capability not found
                        registry = new IslandRegistry(cap.get());
                        break;
                }

                if(registry != null) {
                    int rows = (this.inventory.getContainerSize() + 1) / 9;
                    int slots = this.inventory.getContainerSize() - (18 + (2 * (rows - 2)));

                    registry.setIndex(slots * this.page);
                    this.maxPage = (int) Math.ceil((double) registry.getSize() / slots);
                }
            }

            for (int slot = 0; slot < this.inventory.getContainerSize(); slot++) {
                if((slot < 10 || slot > this.inventory.getContainerSize() - 10)  || (slot%9 == 0 || slot%9 == 8)) continue;
                if(!getSlot(slot).hasItem()) {
                    CompoundTag tag = new CompoundTag();

                    if(registry != null) {
                        boolean isValid = registry.getNextData(tag);
                        if(!isValid) break; //Reached end
                    }

                    ItemStack item = filler.getItem().getItemStack(this.sourceContext, filler.getItem().getTag(tag));
                    setItem(slot, 0, item);
                    this.attachAction(filler, slot);
                }
            }
        });
        this.guiContent.getFillers().stream().filter(f -> f.getPattern() == FillerPattern.EDGES).forEach(filler -> {
            ItemStack slotItem = filler.getItem().getItemStack(this.sourceContext, filler.getItem().getTag(new CompoundTag()));
            for (int slot = 0; slot < this.inventory.getContainerSize(); slot++) {
                if(slot >= 10 && slot <= this.inventory.getContainerSize() - 10  && slot%9 != 0 && slot%9 != 8) continue;
                if(!getSlot(slot).hasItem()) {
                    setItem(slot, 0, slotItem);
                    this.attachAction(filler, slot);
                }
            }
        });
    }

    /**
     * Process Gui Holders Fillers
     */
    private void processPostFillers() {
        this.guiContent.getFillers().stream().filter(f -> f.getPattern() == FillerPattern.EMPTY).forEach(filler -> {
            ItemStack slotItem = filler.getItem().getItemStack(this.sourceContext, filler.getItem().getTag(new CompoundTag()));
            for (int slot = 0; slot < this.inventory.getContainerSize(); slot++) {
                if(!getSlot(slot).hasItem()) {
                    setItem(slot, 0, slotItem);
                    this.attachAction(filler, slot);
                }
            }
        });
    }

    /**
     * Attach a Gui Action to certain slot for container
     *
     * @param item - Gui Action
     * @param slotIndex - Slot to attach it too
     */
    private void attachAction(GuiActionable item, int slotIndex) {
        if(item.getAction().notNone()) { //Only add event listener if action is not none.
            this.event_bus.addListener(slotIndex, (player, clickType) -> {
                GuiAction action = item.getAction();
                CompoundTag tag = new CompoundTag();
                this.setGuiNBT(tag);

                switch(clickType) {
                    case 0 -> action.onPrimaryClick(getSlot(slotIndex).getItem(),
                            new TargetHolder(this.sourceEntity, this.sourceEntity.getUUID()),
                            this.sourceContext,
                            this);
                    case 1 -> action.onSecondaryClick(getSlot(slotIndex).getItem(),
                            new TargetHolder(this.sourceEntity, this.sourceEntity.getUUID()),
                            this.sourceContext,
                            this);
                }
            });
        }
    }

    /**
     * Event triggers upon clicking an items within the GUI
     */
    @Override
    public void clicked(int i, int j, @NotNull ClickType actionType, @NotNull Player playerEntity) {
        if (i < 0)
            return;

        Slot slot = this.slots.get(i);
        if(slot.hasItem()) {
            this.event_bus.trigger(slot.getSlotIndex(), playerEntity, j);
        }

        this.broadcastChanges();
    }

    /**
     * Convert row to Menu Type
     *
     * @param rows - Rows wished in menu
     * @return MenuType for rows
     */
    private static MenuType<ChestMenu> fromRows(int rows) {
        return switch (rows) {
            case 2 -> MenuType.GENERIC_9x2;
            case 3 -> MenuType.GENERIC_9x3;
            case 4 -> MenuType.GENERIC_9x4;
            case 5 -> MenuType.GENERIC_9x5;
            case 6 -> MenuType.GENERIC_9x6;
            default -> MenuType.GENERIC_9x1;
        };
    }
}
