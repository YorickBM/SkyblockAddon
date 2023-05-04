package yorickbm.skyblockaddon.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import yorickbm.skyblockaddon.SkyblockAddon;

//TODO Prevent Extraction quick handle
//TODO Prevent inventory sort
public abstract class ServerOnlyHandler<T> extends AbstractContainerMenu {

    protected SimpleContainer inventory;
    protected T data;

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    protected ServerOnlyHandler(int syncId, Inventory playerInventory, int rows, T data) {
        super(fromRows(rows), syncId);

        this.data = data;
        this.inventory = new SimpleContainer(rows * 9);

        int i = (rows - 4) * 18;
        int n, m;

        //Register our inventory slots
        for (n = 0; n < rows; ++n) {
            for (m = 0; m < 9; ++m) {
                this.addSlot(new Slot(this.inventory, m + n * 9, 8 + m * 18, 18 + n * 18){
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public boolean mayPickup(Player playerEntity) {
                        return false;
                    }
                });
            }
        }

        // Register players inventory (Non toolbar slots)
        for (n = 0; n < 3; ++n) {
            for (m = 0; m < 9; ++m) {
                addSlot(new Slot(playerInventory,  m + n * 9 + 9, 8 + m * 18, 103 + n * 18 + i) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public boolean mayPickup(Player playerEntity) {
                        return false;
                    }
                });
            }
        }

        // Register players toolbar slots
        for (n = 0; n < 9; ++n) {
            this.addSlot(new Slot(playerInventory, n, 8 + n * 18, 161 + i) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }

                @Override
                public boolean mayPickup(Player playerEntity) {
                    return false;
                }
            });
        }


        fillInventoryWith(playerInventory.player);
    }

    /**
     * Override default setting to register every item with mod id within its NBT data.
     * This is done, so on close of menu players inventory can be cleared of items taken from GUI from this mod
     * @param p_182407_ Inventory slot index
     * @param p_182408_ Item State
     * @param p_182409_ ItemStack
     */
    @Override
    public void setItem(int p_182407_, int p_182408_, ItemStack p_182409_) {
        p_182409_.getOrCreateTagElement(SkyblockAddon.MOD_ID); //Put our mod ID as tag
        super.setItem(p_182407_, p_182408_, p_182409_);
    }

    /**
     * Convert row to Menu Type
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
     * Action to be taken when item is clicked within inventory
     * @param i Index
     * @param j ???
     * @param actionType Type of action taken
     * @param playerEntity PlayerEntity whom clicked
     */
    @Override
    public void clicked(int i, int j, ClickType actionType, Player playerEntity) {
        if (i < 0)
            return;

        Slot slot = this.slots.get(i);
        if (this.isRightSlot(i)) {
            this.handleSlotClicked((ServerPlayer) playerEntity, i, slot, j);
        }

        this.broadcastChanges();
    }

    /**
     * Action taken when quick move is used on stack
     * @param player
     * @param index
     * @return
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0)
            return ItemStack.EMPTY;

        Slot slot = this.slots.get(index);
        if (this.isRightSlot(index))
            this.handleSlotClicked((ServerPlayer) player, index, slot, 0);

        return slot.getItem().copy();
    }

    /**
     * Determine whether a slot is needed to be handled
     * @param slot - Slot to determine for
     * @return Boolean whether it needs to be handled
     */
    protected abstract boolean isRightSlot(int slot);

    /**
     * This function is executed upon Inventory Open
     * Will fill it with initial items
     * @param player - Player whom opens inventory
     */
    protected abstract void fillInventoryWith(Player player);

    /**
     * Function gets executed when an item slot is clicked
     * @param player - Whom clicked item
     * @param index - Index of item
     * @param slot - Slot which is clicked
     * @param clickType - 0 for left click, 1 for right click
     * @return
     */
    protected abstract boolean handleSlotClicked(ServerPlayer player, int index, Slot slot, int clickType);
}
