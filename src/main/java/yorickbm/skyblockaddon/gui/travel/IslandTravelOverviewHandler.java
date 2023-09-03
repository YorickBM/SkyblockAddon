package yorickbm.skyblockaddon.gui.travel;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.PlayerIsland;
import yorickbm.skyblockaddon.capabilities.providers.IslandGeneratorProvider;
import yorickbm.skyblockaddon.gui.ServerOnlyHandler;
import yorickbm.skyblockaddon.util.ServerHelper;

public class IslandTravelOverviewHandler extends ServerOnlyHandler<PlayerIsland> {
    protected IslandTravelOverviewHandler(int syncId, Inventory playerInventory, PlayerIsland data) {
        super(syncId, playerInventory, 3, data);
    }

    public static void openMenu(Player player, PlayerIsland data) {
        MenuProvider fac = new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return new TextComponent("Island Traveller");
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int syncId, @NotNull Inventory inv, @NotNull Player player) {
                return new IslandTravelOverviewHandler(syncId, inv, data);
            }
        };
        player.openMenu(fac);
    }

    @Override
    protected boolean isRightSlot(int slot) {
        return slot == 11 || slot == 15;
    }

    @Override
    protected void fillInventoryWith(Player player) {
        for(int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack item;

            if(i == 11) {
                if(!data.hasOne()) {
                    item = new ItemStack(Items.OAK_BOAT);
                    item.setHoverName(ServerHelper.formattedText("Join new Island", ChatFormatting.BOLD, ChatFormatting.BLUE));
                    ServerHelper.addLore(item, ServerHelper.formattedText("\u00BB View all islands you are able to join without an invite.", ChatFormatting.GRAY));
                } else {
                    item = new ItemStack(Items.ENDER_EYE);
                    item.setHoverName(ServerHelper.formattedText("Teleport", ChatFormatting.BOLD, ChatFormatting.BLUE));
                    ServerHelper.addLore(item, ServerHelper.formattedText("\u00BB Teleport to your islands spawn location.", ChatFormatting.GRAY));
                }
            } else if(i == 15) {
                item = new ItemStack(Items.SHEEP_SPAWN_EGG);
                item.setHoverName(ServerHelper.formattedText("Visit Island", ChatFormatting.BOLD, ChatFormatting.BLUE));
                ServerHelper.addLore(item, ServerHelper.formattedText("\u00BB View all islands you are able to visit without a request.", ChatFormatting.GRAY));
            } else {
                item = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
                item.setHoverName(new TextComponent(""));
            }

            setItem(i, 0, item);
        }
    }

    @Override
    protected boolean handleSlotClicked(ServerPlayer player, int index, Slot slot, int clickType) {
        if(index == 11) {
            if(!data.hasOne()) {
                player.closeContainer();
                player.getServer().execute(() -> JoinIslandOverviewHandler.openMenu(player, this.data));
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
            } else {
                player.closeContainer();
                player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(g -> g.getIslandById(data.getIslandId()).teleport(player));
            }
        } else if(index == 15) {
            player.closeContainer();
            player.getServer().execute(() -> TeleportIslandOverviewHandler.openMenu(player, this.data));
            ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
        }
        return true;
    }
}
