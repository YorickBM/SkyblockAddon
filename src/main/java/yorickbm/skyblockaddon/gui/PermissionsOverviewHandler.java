package yorickbm.skyblockaddon.gui;

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
import yorickbm.skyblockaddon.Main;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.islands.Permission;
import yorickbm.skyblockaddon.islands.PermissionState;
import yorickbm.skyblockaddon.util.LanguageFile;
import yorickbm.skyblockaddon.util.ServerHelper;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.List;
import java.util.UUID;

public class PermissionsOverviewHandler extends ServerOnlyHandler<IslandData> {
    protected PermissionsOverviewHandler(int syncId, Inventory playerInventory, IslandData data) {
        super(syncId, playerInventory, 4, data);
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
                Main.islandUIIds.add(syncId);
                return new MemberOverviewHandler(syncId, inv, data);
            }
        };
        player.openMenu(fac);
    }

    @Override
    protected boolean isRightSlot(int slot) {
        return slot == 35 || slot == 31 || (slot >= 10 && slot <= 25 && slot%9 != 0 && slot%9 != 8);
    }

    @Override
    protected void fillInventoryWith(Player player) {
        int permissionIndex = 0;
        Permission[] permissions = Permission.values();

        for(int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack item = null;

            if (i == 35) {
                item = new ItemStack(Items.ARROW);
                item.setHoverName(ServerHelper.formattedText("Back", ChatFormatting.RED, ChatFormatting.BOLD));
            } else if(i >= 10 && i <= 25 && i%9 != 0 && i%9 != 8) {
                if(permissionIndex < permissions.length) {
                    item = new ItemStack(permissions[permissionIndex].getDisplayItem());
                    item.setHoverName(
                        ServerHelper.formattedText(
                            LanguageFile.getForKey("guis.permissions."+permissions[permissionIndex].name()+".title"),
                            ChatFormatting.BLUE, ChatFormatting.BOLD
                        )
                    );
                    ServerHelper.addLore(item,
                            ServerHelper.formattedText("Information", ChatFormatting.YELLOW, ChatFormatting.UNDERLINE),
                            ServerHelper.combineComponents(
                                ServerHelper.formattedText("Required group: ", ChatFormatting.GRAY),
                                ServerHelper.formattedText(data.getPermission(permissions[permissionIndex]).Camelcase(), ChatFormatting.WHITE)
                            ),
                            ServerHelper.formattedText("", ChatFormatting.GRAY),
                            ServerHelper.formattedText("Description", ChatFormatting.YELLOW, ChatFormatting.UNDERLINE),
                            ServerHelper.formattedText(LanguageFile.getForKey("guis.permissions."+permissions[permissionIndex].name()+".desc"), ChatFormatting.GRAY),
                            ServerHelper.formattedText("", ChatFormatting.GRAY),
                            ServerHelper.formattedText("\u00BB Click to change permission group", ChatFormatting.GRAY)
                    );

                    item.getOrCreateTagElement("skyblockaddon").putString("permission", permissions[permissionIndex].name());
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
                player.getServer().execute(() -> SettingsOverviewHandler.openMenu(player, this.data));
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, Main.UI_SOUND_VOL, 1f);
                return true;
            default:
                if(index > 10 && index <= 25 && index%9 != 0 && index%9 != 8) {
                    CompoundTag data = slot.getItem().getOrCreateTagElement("skyblockaddon");
                    if(!data.contains("permission")) return false;
                    Permission permission = Permission.valueOf(data.getString("permission"));

                    switch(this.data.getPermission(permission)) {
                        case EVERYONE: this.data.setPermission(permission, PermissionState.OWNERS);
                        case OWNERS: this.data.setPermission(permission, PermissionState.MEMBERS);
                        case MEMBERS: this.data.setPermission(permission, PermissionState.EVERYONE);
                    }

                    fillInventoryWith(player); //Update items
                    ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, Main.UI_SOUND_VOL, 1f);
                    return true;
                }
                return false;
        }
    }
}
