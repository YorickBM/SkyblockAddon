package yorickbm.skyblockaddon.gui.permission;

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
import oshi.util.tuples.Pair;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.gui.ServerOnlyHandler;
import yorickbm.skyblockaddon.gui.island.SettingsOverviewHandler;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.islands.PermissionGroup;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.List;

public class PermissionGroupOverviewHandler extends ServerOnlyHandler<IslandData> {
    protected PermissionGroupOverviewHandler(int syncId, Inventory playerInventory, IslandData data) {
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
                return new PermissionGroupOverviewHandler(syncId, inv, data);
            }
        };
        player.openMenu(fac);
    }

    @Override
    protected boolean isRightSlot(int slot) {
        return slot == 26 || (slot >= 10 && slot <= 17 && slot%9 != 0 && slot%9 != 8);
    }

    @Override
    protected void fillInventoryWith(Player player) {
        List<PermissionGroup> groups = this.data.getPermissionGroups();
        int groupIndex = 0;

        for(int slot = 0; slot < this.inventory.getContainerSize(); slot++) {
            ItemStack item;

            if( slot == 26) {
                item = new ItemStack(Items.ARROW);
                item.setHoverName(ServerHelper.formattedText("Back", ChatFormatting.RED, ChatFormatting.BOLD));
            } else if(slot >= 10 && slot <= 17 && slot%9 != 0 && slot%9 != 8) {
                if(groupIndex < groups.size()) {
                    item = groups.get(groupIndex).getItemStack();
                    item.getOrCreateTagElement(SkyblockAddon.MOD_ID).putString("group", groups.get(groupIndex).getName());

                    //TODO: Custom description options.
                    if(groups.get(groupIndex).getName().equals("Default")) {
                        ServerHelper.addLore(item,
                                ServerHelper.formattedText("\u00BB Click to alter permissions that are used by default for players!", ChatFormatting.GRAY),
                                ServerHelper.formattedText(" ", ChatFormatting.GRAY),
                                ServerHelper.formattedText("\u2666 These permissions are for anyone NOT put within a different group.", ChatFormatting.GRAY)
                        );
                    } else {
                        ServerHelper.addLore(item, ServerHelper.formattedText("\u00BB Click to alter permissions for group, or add players.", ChatFormatting.GRAY));
                    }

                    groupIndex += 1;
                } else {
                    item = new ItemStack(Items.AIR);
                }
            } else  {
                item = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
                item.setHoverName(new TextComponent(""));
            }
            setItem(slot, 0, item);
        }
    }

    @Override
    protected boolean handleSlotClicked(ServerPlayer player, int index, Slot slot, int clickType) {
        List<PermissionGroup> groups = this.data.getPermissionGroups();
        if( index == 26) {
            player.closeContainer();
            player.getServer().execute(() -> SettingsOverviewHandler.openMenu(player, this.data));
            ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
            return true;
        } else if(index >= 10 && index <= 17 && index%9 != 0 && index%9 != 8) {
            if(!slot.getItem().getOrCreateTagElement(SkyblockAddon.MOD_ID).contains("group")) return false;

            String groupName = slot.getItem().getOrCreateTagElement(SkyblockAddon.MOD_ID).getString("group");
            groups.stream().filter(g -> g.getName().equals(groupName)).findFirst().ifPresent(
                group -> {
                    player.closeContainer();
                    player.getServer().execute(() -> PermissionsOverviewHandler.openMenu(player, new Pair<>(data, group)));
                    ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
                }
            );
        }
        return false;
    }
}
