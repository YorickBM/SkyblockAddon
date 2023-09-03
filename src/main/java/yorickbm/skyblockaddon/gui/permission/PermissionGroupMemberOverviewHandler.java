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
import oshi.util.tuples.Pair;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.gui.ServerOnlyHandler;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.islands.PermissionGroup;
import yorickbm.skyblockaddon.util.ServerHelper;
import yorickbm.skyblockaddon.util.UsernameCache;

import javax.annotation.Nullable;
import java.util.UUID;

public class PermissionGroupMemberOverviewHandler extends ServerOnlyHandler<Pair<IslandData, PermissionGroup>> {
    private final int pages;
    private int page = 0;

    protected PermissionGroupMemberOverviewHandler(int syncId, Inventory playerInventory, Pair<IslandData, PermissionGroup> data) {
        super(syncId, playerInventory, 5, data);

        this.pages = (int) Math.ceil((data.getB().getMembers().size()-1)/21.0);

        drawMembers();
    }

    public static void openMenu(Player player, Pair<IslandData, PermissionGroup> data) {
        MenuProvider fac = new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return new TextComponent(data.getB().getName() + " member(s)");
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int syncId, @NotNull Inventory inv, @NotNull Player player) {
                return new PermissionGroupMemberOverviewHandler(syncId, inv, data);
            }
        };
        player.openMenu(fac);
    }

    @Override
    protected boolean isRightSlot(int slot) {
        return slot == 44 || slot == 39 || slot == 36 || slot == 41 || (slot >= 10 && slot <= 34 && slot%9 != 0 && slot%9 != 8);
    }

    @Override
    protected void fillInventoryWith(Player player) {
        for(int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack item = null;

            if (i == 44) {
                item = new ItemStack(Items.ARROW);
                item.setHoverName(ServerHelper.formattedText("Back", ChatFormatting.RED, ChatFormatting.BOLD));
            } else if(i == 36 && this.data.getB().canBeRemoved()) {
                item = new ItemStack(Items.OAK_BOAT);
                item.setHoverName(ServerHelper.formattedText("Add player", ChatFormatting.GREEN, ChatFormatting.BOLD));
            } else if(i >= 10 && i <= 34 && i%9 != 0 && i%9 != 8) {
                //BIOME So keep empty
            } else {
                item = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
                item.setHoverName(new TextComponent(""));
            }

            if(item != null) setItem(i, 0, item);
        }
    }

    private void drawMembers() {
        int memberIndex = page * 21;

        for(int i = 10; i <= 34; i++) {
            if(i%9 == 0 || i%9 == 8)  continue;

            ItemStack item;
            if (memberIndex < data.getB().getMembers().size()) {
                UUID member = data.getB().getMembers().get(memberIndex);
                String username = "Unknown";

                try {
                    username = UsernameCache.getBlocking(member);
                } catch(Exception ignored) {}

                item = new ItemStack(Items.PLAYER_HEAD);
                item.setHoverName(ServerHelper.formattedText(username, ChatFormatting.AQUA, ChatFormatting.BOLD));
                ServerHelper.addLore(item,
                    ServerHelper.formattedText("\u00BB Click to remove this user from the group.", ChatFormatting.GRAY)
                );
                CompoundTag tag = item.getOrCreateTag();
                tag.putString("SkullOwner", username);
                item.setTag(tag);

                item.getOrCreateTagElement("skyblockaddon").putString("member", member.toString()); //Put member in item NBT for click event
                memberIndex += 1;
            } else {
                item = new ItemStack(Items.AIR);
            }

            setItem(i, 0, item);
        }

        ItemStack prev = new ItemStack(Items.RED_BANNER);
        prev.setHoverName(ServerHelper.formattedText("Previous", ChatFormatting.RED, ChatFormatting.BOLD));
        prev.getOrCreateTagElement("skyblockaddon").putInt("page", page-1);
        if(page > 0) setItem(39, 0, prev);
        else setItem(39, 0, new ItemStack(Items.GRAY_STAINED_GLASS_PANE).setHoverName(new TextComponent("")));

        ItemStack next = new ItemStack(Items.GREEN_BANNER);
        next.setHoverName(ServerHelper.formattedText("Next", ChatFormatting.GREEN, ChatFormatting.BOLD));
        next.getOrCreateTagElement("skyblockaddon").putInt("page", page+1);
        if(page < (pages-1)) setItem(41, 0, next);
        else setItem(41, 0, new ItemStack(Items.GRAY_STAINED_GLASS_PANE).setHoverName(new TextComponent("")));

        ItemStack info = new ItemStack(Items.BOOK);
        info.setHoverName(ServerHelper.formattedText("Page " + (this.page + 1) + "/" + (this.pages+1)));
        setItem(40, 0, info);
    }

    @Override
    protected boolean handleSlotClicked(ServerPlayer player, int index, Slot slot, int clickType) {
        switch (index) {
            case 44 -> {
                player.closeContainer();
                player.getServer().execute(() -> PermissionsOverviewHandler.openMenu(player, this.data));
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
                return true;
            }
            case 36 -> {
                if (this.data.getB().canBeRemoved()) {
                    player.closeContainer();
                    player.getServer().execute(() -> PermissionGroupMemberInviteOverviewHandler.openMenu(player, this.data));
                    ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
                }
                return true;
            }
            case 39, 41 -> {
                page = slot.getItem().getTagElement("skyblockaddon").getInt("page");
                drawMembers();
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
                return true;
            }
            default -> {
                if (!slot.getItem().getTagElement("skyblockaddon").contains("member")) return false;
                UUID member = UUID.fromString(slot.getItem().getTagElement("skyblockaddon").getString("member"));
                ServerHelper.playSongToPlayer(player, SoundEvents.AMETHYST_BLOCK_CHIME, 3f, 1f);
                this.data.getB().removeMember(member);
                drawMembers();
            }
        }

        return false;
    }
}
