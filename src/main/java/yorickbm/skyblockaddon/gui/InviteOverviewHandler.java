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
import yorickbm.skyblockaddon.capabilities.PlayerIslandProvider;
import yorickbm.skyblockaddon.util.IslandData;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class InviteOverviewHandler extends ServerOnlyHandler<IslandData> {
    protected InviteOverviewHandler(int syncId, Inventory playerInventory, IslandData data) {
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
                return new InviteOverviewHandler(syncId, inv, data);
            }
        };
        player.openMenu(fac);
    }

    @Override
    protected boolean isRightSlot(int slot) {
        return slot == 35;
    }

    @Override
    protected void fillInventoryWith(Player player) {
        int memberIndex = 0;
        List<ServerPlayer> members = player.getServer().getPlayerList().getPlayers().stream().filter( p -> {
            AtomicBoolean hasOne = new AtomicBoolean(false);
            p.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> hasOne.set(i.hasOne()));
            return !hasOne.get();
        }).collect(Collectors.toList());

        for(int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack item = null;

            if (i == 35) {
                item = new ItemStack(Items.ARROW);
                item.setHoverName(ServerHelper.formattedText("Back", ChatFormatting.RED, ChatFormatting.BOLD));
            } else if(i >= 10 && i <= 25 && i%9 != 0 && i%9 != 8) {
                if(memberIndex < members.size()) {
                    ServerPlayer sPlayer = members.get(memberIndex);

                    CompoundTag tag = new CompoundTag();
                    tag.putString("SkullOwner", sPlayer.getGameProfile().getName());

                    item = new ItemStack(Items.PLAYER_HEAD, 1, tag);
                    item.setHoverName(
                            ServerHelper.formattedText(
                                    sPlayer.getGameProfile().getName(), ChatFormatting.BLUE, ChatFormatting.BOLD
                            )
                    );
                    ServerHelper.addLore(item, ServerHelper.formattedText("\u00BB Regular island member.", ChatFormatting.GRAY));

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
            case 44:
                player.closeContainer();
                player.getServer().execute(() -> IslandOverviewHandler.openMenu(player, this.data));
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, 1, 1f);
                return true;
            case 31:
                player.closeContainer();
                player.getServer().execute(() -> IslandOverviewHandler.openMenu(player, this.data));
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, 1, 1f);
                return true;
        }

        return false;
    }
}
