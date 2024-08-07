package yorickbm.skyblockaddon.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import yorickbm.skyblockaddon.gui.json.GuiAction;
import yorickbm.skyblockaddon.gui.json.GuiHolder;
import yorickbm.skyblockaddon.gui.util.GuiActionable;
import yorickbm.skyblockaddon.gui.util.GuiContext;
import yorickbm.skyblockaddon.gui.util.SkyblockAddonMenuProvider;
import yorickbm.skyblockaddon.gui.util.TargetHolder;

public class ServerGui extends AbstractContainerMenu {
    private static final Logger LOGGER = LogManager.getLogger();

    protected final SimpleContainer inventory;
    protected final GuiContext sourceContext;
    protected final Player sourceEntity;
    protected final GuiHolder guiContent;

    protected final GuiListener event_bus;

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
                if(this.context != null) return this.context.parseTextComponent(holder.getTitle()); //Parse through context if available
                return holder.getTitle();
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

        processItems();
        processFillers();
    }

    private void processItems() {
        this.guiContent.getItems().forEach(guiItem -> {
            ItemStack item = guiItem.getItem().getItemStack();
            setItem(guiItem.getSlot(), 0, item);

            this.attachAction(guiItem, guiItem.getSlot());
        });
    }
    private void processFillers() {
        this.guiContent.getFillers().forEach(filler -> {

            switch (filler.getPattern()) {
                case EMPTY -> {
                    for (int slot = 0; slot < this.inventory.getContainerSize(); slot++) {
                        if(!getSlot(slot).hasItem()) {
                            setItem(slot, 0, filler.getItem().getItemStack());

                            this.attachAction(filler, slot);
                        }
                    }
                }
                case INSIDE -> {
                    for (int slot = 0; slot < this.inventory.getContainerSize(); slot++) {

                        if((slot < 10 || slot > this.inventory.getContainerSize() - 10)  || (slot%9 == 0 || slot%9 == 8)) continue;

                        if(!getSlot(slot).hasItem()) {
                            setItem(slot, 0, filler.getItem().getItemStack());
                            this.attachAction(filler, slot);
                        }
                    }
                }
                case EDGES -> {
                    for (int slot = 0; slot < this.inventory.getContainerSize(); slot++) {

                        if(slot >= 10 && slot <= this.inventory.getContainerSize() - 10  && slot%9 != 0 && slot%9 != 8) continue;

                        if(!getSlot(slot).hasItem()) {
                            setItem(slot, 0, filler.getItem().getItemStack());
                            this.attachAction(filler, slot);
                        }
                    }
                }

            }
        });
    }

    private void attachAction(GuiActionable item, int slotIndex) {
        if(item.getAction().notNone()) { //Only add event listener if action is not none.
            this.event_bus.addListener(slotIndex, (index, slot, clickType) -> {
                GuiAction action = item.getAction();
                switch(clickType) {
                    case 2 -> action.onRightClick(item.getItem().getItemStack(), new TargetHolder(this.sourceEntity, this.sourceEntity.getUUID()), null, this.sourceContext, null);
                    case 1 -> action.onLeftClick(item.getItem().getItemStack(), new TargetHolder(this.sourceEntity, this.sourceEntity.getUUID()), null, this.sourceContext, null);
                }
            });
        }
    }

    @Override
    public void clicked(int i, int j, @NotNull ClickType actionType, @NotNull Player playerEntity) {
        if (i < 0)
            return;

        Slot slot = this.slots.get(i);
        if(slot.hasItem()) {
            this.event_bus.trigger(slot.getSlotIndex(), i, slot, j);
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
