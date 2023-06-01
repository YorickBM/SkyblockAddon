package yorickbm.skyblockaddon.gui.permission;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
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
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.islands.PermissionGroup;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.islands.permissions.Permission;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.List;

public class PermissionsOverviewHandler extends ServerOnlyHandler<Pair<IslandData, PermissionGroup>> {

    protected PermissionsOverviewHandler(int syncId, Inventory playerInventory, Pair<IslandData, PermissionGroup> data) {
        super(syncId, playerInventory, 4, data);
    }

    public static void openMenu(Player player, Pair<IslandData, PermissionGroup> data) {
        MenuProvider fac = new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return new TextComponent(data.getB().getName() + " permissions");
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                SkyblockAddon.islandUIIds.add(syncId);
                return new PermissionsOverviewHandler(syncId, inv, data);
            }
        };
        player.openMenu(fac);
    }

    @Override
    protected boolean isRightSlot(int slot) {
        return slot == 35 || slot == 31 || slot == 27 || (slot >= 10 && slot <= 25 && slot%9 != 0 && slot%9 != 8);
    }

    @Override
    protected void fillInventoryWith(Player player) {
        int permissionIndex = 0;
        List<Permission> permissions = this.data.getB().getPermissions().stream().toList();

        for(int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack item = null;

            if (i == 35) {
                item = new ItemStack(Items.ARROW);
                item.setHoverName(ServerHelper.formattedText("Back", ChatFormatting.RED, ChatFormatting.BOLD));
            } else if(i == 27 && this.data.getB().canBeRemoved()) {
                item = new ItemStack(Items.PLAYER_HEAD);
                item.setHoverName(ServerHelper.formattedText("Members", ChatFormatting.BLUE, ChatFormatting.BOLD));
            } else if(i >= 10 && i <= 25 && i%9 != 0 && i%9 != 8) {
                if(permissionIndex < permissions.size()) {
                    item = permissions.get(permissionIndex).getItemStack();
                    item.getOrCreateTagElement("skyblockaddon").putString("permission", permissions.get(permissionIndex).getClass().getSimpleName());
                    permissionIndex += 1;
                } else {
                    item = new ItemStack(Items.AIR);
                }
            } else {
                item = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
                item.setHoverName(new TextComponent(""));
            }

            if(item != null && item instanceof ItemStack) setItem(i, 0, item);
        }
    }

    @Override
    protected boolean handleSlotClicked(ServerPlayer player, int index, Slot slot, int clickType) {
        switch(index) {
            case 35:
                player.closeContainer();
                player.getServer().execute(() -> PermissionGroupOverviewHandler.openMenu(player, this.data.getA()));
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
                return true;
            case 27:
                if(this.data.getB().canBeRemoved()) {
                    player.closeContainer();
                    player.getServer().execute(() -> PermissionGroupMemberOverviewHandler.openMenu(player, this.data));
                    ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
                }
                return true;
            default:
                if(index >= 10 && index <= 25 && index%9 != 0 && index%9 != 8) {
                    CompoundTag data = slot.getItem().getOrCreateTagElement("skyblockaddon");
                    if(!data.contains("permission")) return false;

                    Permission permission = this.data.getB().getPermission(Permissions.valueOf(data.getString("permission")));
                    permission.setState(!permission.getState());

                    //Got to update island list?

                    fillInventoryWith(player); //Update items
                    ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
                    return true;
                }
                return false;
        }
    }
}
