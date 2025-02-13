package yorickbm.guilibrary.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import yorickbm.guilibrary.GUIFiller;
import yorickbm.guilibrary.GUIItem;
import yorickbm.guilibrary.GUIType;
import yorickbm.guilibrary.events.GuiDrawFillerEvent;
import yorickbm.guilibrary.events.GuiDrawItemEvent;
import yorickbm.guilibrary.util.FillerPattern;

import java.util.List;

public class ServerInterface extends AbstractContainerMenu {

    protected CompoundTag data;
    protected final SimpleContainer container;
    protected Player holder;
    protected GUIType type;

    private List<GUIItem> items;

    private int maxPage = 1;
    private int currPage = 1;

    public ServerInterface(int syncId, Inventory playerInventory, Player holder, GUIType type, CompoundTag data) {
        super(fromRows(type.getRows()), syncId);
        this.container = new SimpleContainer(type.getRows() * 9);
        this.holder = holder;

        this.type = type;
        this.items = type.getItems();
        this.data = data;

        int i = (type.getRows() - 4) * 18;
        int n, m;

        //Register our inventory slots
        for (n = 0; n < type.getRows(); ++n) {
            for (m = 0; m < 9; ++m) {
                this.addSlot(new Slot(this.container, m + n * 9, 8 + m * 18, 18 + n * 18) {
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

        this.update();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    /**
     * Returns the player entity that owns the creation of the GUI instance
     * @return - Player Entity
     */
    public Player getOwner() { return this.holder; }

    /**
     * Close container for the source entity.
     * Source Entity - Who opened the GUI
     */
    public void close() {
        this.holder.closeContainer();
    }

    /**
     * Get container responsible for instance.
     */
    public SimpleContainer getContainer() {
        return this.container;
    }

    /**
     * Return container NBT data.
     */
    public CompoundTag getData() {
        return this.data;
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
            this.items.stream().filter(item -> item.getSlot() == slot.getSlotIndex()).findFirst().ifPresent(gi -> {
                switch (j) {
                    case 0 -> MinecraftForge.EVENT_BUS.post(gi.getPrimaryClick(this, (ServerPlayer) playerEntity, slot));
                    case 1 -> MinecraftForge.EVENT_BUS.post(gi.getSecondaryClick(this, (ServerPlayer) playerEntity, slot));
                }
            });
            this.type.getFillers().stream().filter(item -> item.getSlot() == slot.getSlotIndex()).findFirst().ifPresent(gi -> {
                switch (j) {
                    case 0 -> MinecraftForge.EVENT_BUS.post(gi.getPrimaryClick(this, (ServerPlayer) playerEntity, slot));
                    case 1 -> MinecraftForge.EVENT_BUS.post(gi.getSecondaryClick(this, (ServerPlayer) playerEntity, slot));
                }
            });
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

    /**
     * Initiates a draw of the inventory
     */
    public void update() {
        this.container.clearContent(); //Clear Inventory

        //Add fillers
        for(GUIFiller item : type.getFillers().stream().filter(f -> f.getPattern() != FillerPattern.EMPTY).toList()) {
            if(item.hasEvent()) MinecraftForge.EVENT_BUS.post(item.getEvent(this, type.getRows() * 9));
            else MinecraftForge.EVENT_BUS.post(new GuiDrawFillerEvent(this, item, type.getRows() * 9));
        }

        //Add items
        for(GUIItem item : type.getItems()) {
            MinecraftForge.EVENT_BUS.post(new GuiDrawItemEvent(this, item));
        }

        //Add fillers
        for(GUIFiller item : type.getFillers().stream().filter(f -> f.getPattern() == FillerPattern.EMPTY).toList()) {
            if(item.hasEvent()) MinecraftForge.EVENT_BUS.post(item.getEvent(this, type.getRows() * 9));
            else MinecraftForge.EVENT_BUS.post(new GuiDrawFillerEvent(this, item, type.getRows() * 9));
        }
    }

    /**
     * Remove item from data set if its not rendered (prevents action triggers)
     * @param itemHolder
     */
    public void removeItem(GUIItem itemHolder) {
        this.items.remove(itemHolder);
    }

    public int getCurrentPage() {  return this.currPage; }

    public int getMaxPage() { return this.maxPage; }
    public void setMaxPage(int page) { this.maxPage = page; }

    public boolean nextPage() {
        if(this.currPage == this.maxPage) return false;
        else this.currPage += 1;
        return true;
    }
    public boolean prevPage() {
        if(this.currPage == 1) return false;
        else this.currPage -= 1;
        return true;
    }

}
