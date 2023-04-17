package yorickbm.skyblockaddon.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import yorickbm.skyblockaddon.capabilities.PlayerIslandProvider;
import yorickbm.skyblockaddon.commands.LeaveIslandCommand;
import yorickbm.skyblockaddon.util.IslandData;
import yorickbm.skyblockaddon.util.ServerHelper;

public class SettingsOverviewHandler extends ServerOnlyHandler<IslandData> {
    protected SettingsOverviewHandler(int syncId, Inventory playerInventory, IslandData data) {
        super(syncId, playerInventory, 3, data);
    }

    public static void openMenu(Player player, IslandData data) {
        MenuProvider fac = new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return new TextComponent(data.getOwner(player.getServer()).getName() + "'s island");
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                return new SettingsOverviewHandler(syncId, inv, data);
            }
        };
        player.openMenu(fac);
    }

    @Override
    protected boolean isRightSlot(int slot) {
        return slot == 10 || slot == 13 || slot == 16 || slot == 26;
    }

    @Override
    protected void fillInventoryWith(Player player) {
        for(int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack item = null;
            switch(i) {
                case 10:
                    item = new ItemStack(Items.OAK_SAPLING);
                    item.setHoverName(ServerHelper.formattedText("Change Biome", ChatFormatting.BOLD, ChatFormatting.BLUE));
                    ServerHelper.addLore(item,
                            ServerHelper.formattedText(" "),
                            ServerHelper.formattedText("\\u{2726} Change Biome of your island.", ChatFormatting.GRAY),
                            ServerHelper.formattedText("\\u{2726} Current: UNKNOWN.", ChatFormatting.GRAY));
                    break;

                case 12:
                    item = new ItemStack(Items.IRON_BARS);
                    item.setHoverName(ServerHelper.formattedText("Permissions", ChatFormatting.BOLD, ChatFormatting.BLUE));
                    ServerHelper.addLore(item,
                            ServerHelper.formattedText(" "),
                            ServerHelper.formattedText("\\u{2726} Alter island permissions.", ChatFormatting.GRAY),
                            ServerHelper.formattedText(" "),
                            ServerHelper.formattedText("Global Permissions:", ChatFormatting.YELLOW, ChatFormatting.UNDERLINE),
                            ServerHelper.formattedText("\\u{2726} Teleport: REQUEST", ChatFormatting.GRAY),
                            ServerHelper.formattedText("\\u{2726} Invite: MEMBERS", ChatFormatting.GRAY),
                            ServerHelper.formattedText(" "),
                            ServerHelper.formattedText("Interaction Permissions:", ChatFormatting.YELLOW, ChatFormatting.UNDERLINE),
                            ServerHelper.formattedText("\\u{2726} Place: MEMBERS", ChatFormatting.GRAY),
                            ServerHelper.formattedText("\\u{2726} BREAK: MEMBERS", ChatFormatting.GRAY),
                            ServerHelper.formattedText("\\u{2726} USE BLOCK: MEMBERS", ChatFormatting.GRAY),
                            ServerHelper.formattedText("\\u{2726} USE ITEM: EVERYONE", ChatFormatting.GRAY)
                    );
                    break;
                case 14:
                    item = new ItemStack(Items.RED_BED);
                    item.setHoverName(ServerHelper.formattedText("Change Spawn", ChatFormatting.BOLD, ChatFormatting.BLUE));
                    ServerHelper.addLore(item,
                            ServerHelper.formattedText(" "),
                            ServerHelper.formattedText("\\u{2726} Set spawn of your island.", ChatFormatting.GRAY),
                            ServerHelper.formattedText("\\u{2726} Current: X, Y, Z", ChatFormatting.GRAY));
                    break;
                case 16:
                    item = new ItemStack(Items.BARRIER);
                    item.setHoverName(ServerHelper.formattedText("Leave", ChatFormatting.BOLD, ChatFormatting.RED));
                    ServerHelper.addLore(item,
                            ServerHelper.formattedText(" "),
                            ServerHelper.formattedText("\\u{2726} Leave this island and teleport to spawn.", ChatFormatting.GRAY));
                    break;

                case 26:
                    item = new ItemStack(Items.ARROW);
                    item.setHoverName(ServerHelper.formattedText("Back", ChatFormatting.RED));
                    break;

                default:
                    item = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
                    item.setHoverName(new TextComponent(""));
                    break;
            }
            setItem(i, 0, item);
        }
    }

    @Override
    protected boolean handleSlotClicked(ServerPlayer player, int index, Slot slot, int clickType) {

        if (index == 0) {
            player.closeContainer();
            ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, 1, 1f);
            return true;
        }

        switch(index) {
            case 10:
                player.closeContainer();
                player.getServer().execute(() -> System.out.println("OPEN BIOME MENU"));
                return true;
            case 12:
                player.closeContainer();
                player.getServer().execute(() -> System.out.println("OPEN ALTER SETTINGS MENU"));
                return true;
            case 14:
                player.closeContainer();
                player.getServer().execute(() -> System.out.println("SET ISLAND NEW SPAWN"));
                return true;
            case 16:
                player.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(pdata -> {
                    player.closeContainer();
                    LeaveIslandCommand.leaveIsland(this.data, pdata, player);
                });
                return true;

            case 26:
                player.closeContainer();
                player.getServer().execute(() -> IslandOverviewHandler.openMenu(player, this.data));
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, 1, 1f);
                return true;
        }

        return true;
    }
}
