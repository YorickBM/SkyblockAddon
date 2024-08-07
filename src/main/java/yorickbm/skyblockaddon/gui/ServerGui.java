package yorickbm.skyblockaddon.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import yorickbm.skyblockaddon.gui.json.GuiHolder;

public class ServerGui extends AbstractContainerMenu {
    protected final SimpleContainer inventory;
    protected final Object context;

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public static SkyblockAddonMenuProvider getProvider(GuiHolder holder) {
        return new SkyblockAddonMenuProvider() {
            Object context = null;
            @Override
            public void setContext(Object context) {
                this.context = context;
            }

            @Override
            public @NotNull Component getDisplayName() {
                return holder.getTitle();
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
                return new ServerGui(i, inventory, holder, this.context);
            }
        };
    }

    protected ServerGui(int syncId, Inventory playerInventory, GuiHolder holder, Object context) {
        super(fromRows(holder.getRows()), syncId);
        this.inventory = new SimpleContainer(holder.getRows() * 9);
        this.context = context;

        int i = (holder.getRows() - 4) * 18;
        int n, m;

        //Register our inventory slots
        for (n = 0; n < holder.getRows(); ++n) {
            for (m = 0; m < 9; ++m) {
                this.addSlot(new Slot(this.inventory, m + n * 9, 8 + m * 18, 18 + n * 18){
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
                addSlot(new Slot(playerInventory,  m + n * 9 + 9, 8 + m * 18, 103 + n * 18 + i) {
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
}
