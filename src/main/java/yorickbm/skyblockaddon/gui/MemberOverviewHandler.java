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
import yorickbm.skyblockaddon.util.IslandData;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.List;
import java.util.UUID;

public class MemberOverviewHandler extends ServerOnlyHandler<IslandData> {

    protected MemberOverviewHandler(int syncId, Inventory playerInventory, IslandData data) {
        super(syncId, playerInventory, 6, data);
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
                return new MemberOverviewHandler(syncId, inv, data);
            }
        };
        player.openMenu(fac);
    }

    @Override
    protected boolean isRightSlot(int slot) {
        return slot == 53 || (slot < 45 && slot > 8 && slot % 9 != 0);
    }

    @Override
    protected void fillInventoryWith(Player player) {
        int memberIndex = 0;
        List<UUID> members = this.data.getMembers();

        for(int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack item = null;

            if (i == 53) {
                item = new ItemStack(Items.ARROW);
                item.setHoverName(ServerHelper.formattedText("Back", ChatFormatting.RED));
            } else if(i == 10) {
                String playerName = this.data.hasOwner() ? this.data.getOwner(player.getServer()).getName() : "Unknown";

                item = new ItemStack(Items.PLAYER_HEAD);
                item.setHoverName(
                        ServerHelper.formattedText(
                                playerName, ChatFormatting.GOLD, ChatFormatting.BOLD
                        )
                );
                ServerHelper.addLore(item, ServerHelper.formattedText(" "), ServerHelper.formattedText("\\u{2726} Islands current owner.", ChatFormatting.GRAY));

                CompoundTag tag = item.getOrCreateTag();
                if(this.data.hasOwner()) tag.putString("SkullOwner", playerName);
                item.setTag(tag);


            } else if(i >= 10 && i <= 43 && i%9 != 0 && i%9 != 8) {
                if(memberIndex < members.size()) {
                    CompoundTag tag = new CompoundTag();
                    String playerName = "Unknown";

                    if(this.data.hasOwner()) {
                        playerName = player.getServer().getPlayerList().getPlayer(members.get(memberIndex)).getGameProfile().getName();
                        tag.putString("SkullOwner", playerName);
                    }

                    item = new ItemStack(Items.PLAYER_HEAD, 1, tag);
                    item.setHoverName(
                            ServerHelper.formattedText(
                                    playerName, ChatFormatting.BLUE, ChatFormatting.BOLD
                            )
                    );
                    ServerHelper.addLore(item, ServerHelper.formattedText(" "), ServerHelper.formattedText("\\u{2726} Regular island member.", ChatFormatting.GRAY));

                    memberIndex += 1;
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
            case 53:
                player.closeContainer();
                player.getServer().execute(() -> IslandOverviewHandler.openMenu(player, this.data));
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, 1, 1f);
                return true;
        }

        return false;
    }
}
