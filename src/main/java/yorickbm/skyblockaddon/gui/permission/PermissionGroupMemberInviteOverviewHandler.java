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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.gui.ServerOnlyHandler;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.islands.PermissionGroup;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.List;
import java.util.UUID;

public class PermissionGroupMemberInviteOverviewHandler extends ServerOnlyHandler<Pair<IslandData, PermissionGroup>> {
    protected PermissionGroupMemberInviteOverviewHandler(int syncId, Inventory playerInventory, Pair<IslandData, PermissionGroup> data) {
        super(syncId, playerInventory, 4, data);
    }

    public static void openMenu(Player player, Pair<IslandData, PermissionGroup> data) {
        MenuProvider fac = new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return new TextComponent(data.getB().getName() + " invite player");
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int syncId, @NotNull Inventory inv, @NotNull Player player) {
                return new PermissionGroupMemberInviteOverviewHandler(syncId, inv, data);
            }
        };
        player.openMenu(fac);
    }

    @Override
    protected boolean isRightSlot(int slot) {
        return slot == 35 || (slot >= 10 && slot <= 25 && slot%9 != 0 && slot%9 != 8);
    }

    @Override
    protected void fillInventoryWith(Player player) {
        int memberIndex = 0;
        List<ServerPlayer> members = player.getServer().getPlayerList().getPlayers().stream().filter(p ->
                !this.data.getA().partOfAnyGroup(p.getUUID()) && !this.data.getA().isOwner(p.getUUID())
        ).toList();

        for(int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack item;

            if (i == 35) {
                item = new ItemStack(Items.ARROW);
                item.setHoverName(ServerHelper.formattedText("Back", ChatFormatting.RED, ChatFormatting.BOLD));
            } else if(i >= 10 && i <= 25 && i%9 != 0 && i%9 != 8) {
                if(memberIndex < members.size()) {
                    ServerPlayer sPlayer = members.get(memberIndex);

                    item = new ItemStack(Items.PLAYER_HEAD);
                    item.setHoverName(
                            ServerHelper.formattedText(
                                    sPlayer.getGameProfile().getName(), ChatFormatting.BLUE, ChatFormatting.BOLD
                            )
                    );
                    ServerHelper.addLore(item, ServerHelper.formattedText("\u00BB Click to add player to this group", ChatFormatting.GRAY));
                    item.getOrCreateTagElement("skyblockaddon").putUUID("player", sPlayer.getUUID());

                    CompoundTag tag = item.getOrCreateTag();
                    tag.putString("SkullOwner", sPlayer.getGameProfile().getName());
                    item.setTag(tag);

                    memberIndex += 1;
                } else {
                    item = new ItemStack(Items.AIR);
                }
            } else {
                item = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
                item.setHoverName(new TextComponent(""));
            }

            setItem(i, 0, item);
        }
    }

    @Override
    protected boolean handleSlotClicked(ServerPlayer player, int index, Slot slot, int clickType) {
        if (index == 35) {
            player.closeContainer();
            player.getServer().execute(() -> PermissionGroupMemberOverviewHandler.openMenu(player, this.data));
            ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
            return true;
        } else {
            if (index >= 10 && index <= 25 && index % 9 != 0 && index % 9 != 8) {
                if (slot.getItem().isEmpty() || slot.getItem().getItem().equals(Items.AIR))
                    return false; //Item is air or empty!

                player.closeContainer();
                ServerHelper.playSongToPlayer(player, SoundEvents.AMETHYST_BLOCK_CHIME, 3f, 1f);

                UUID uuid = slot.getItem().getTagElement("skyblockaddon").getUUID("player");
                this.data.getB().addMember(uuid);

                player.getServer().execute(() -> PermissionGroupMemberOverviewHandler.openMenu(player, this.data));
            }
        }

        return true;
    }
}
