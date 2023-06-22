package yorickbm.skyblockaddon.gui.island;

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
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.PlayerIsland;
import yorickbm.skyblockaddon.capabilities.Providers.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.Providers.PlayerIslandProvider;
import yorickbm.skyblockaddon.gui.ServerOnlyHandler;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.util.LanguageFile;
import yorickbm.skyblockaddon.util.ServerHelper;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.List;
import java.util.UUID;

public class MemberOverviewHandler extends ServerOnlyHandler<IslandData> {

    protected MemberOverviewHandler(int syncId, Inventory playerInventory, IslandData data) {
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
                SkyblockAddon.islandUIIds.add(syncId);
                return new MemberOverviewHandler(syncId, inv, data);
            }
        };
        player.openMenu(fac);
    }

    @Override
    protected boolean isRightSlot(int slot) {
        return slot == 35 || slot == 31 || (slot > 10 && slot <= 25 && slot%9 != 0 && slot%9 != 8) ;
    }

    @Override
    protected void fillInventoryWith(Player player) {
        int memberIndex = 0;
        List<UUID> members = this.data.getMembers();

        for(int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack item = null;

            if (i == 35) {
                item = new ItemStack(Items.ARROW);
                item.setHoverName(ServerHelper.formattedText("Back", ChatFormatting.RED, ChatFormatting.BOLD));
            } else if (i == 31 && this.data.isAdmin(player.getUUID())) {
                item = new ItemStack(Items.OAK_BOAT);
                item.setHoverName(ServerHelper.formattedText("Invite", ChatFormatting.GREEN, ChatFormatting.BOLD));
                ServerHelper.addLore(item, ServerHelper.formattedText("\u00BB Invite online player to join this island.", ChatFormatting.GRAY));
            } else if(i == 10) {
                String playerName = this.data.hasOwner() ? this.data.getOwner(player.getServer()).getName() : "Unknown";

                item = new ItemStack(Items.PLAYER_HEAD);
                item.setHoverName(
                        ServerHelper.formattedText(
                                playerName, ChatFormatting.GOLD, ChatFormatting.BOLD
                        )
                );
                ServerHelper.addLore(item, ServerHelper.formattedText("\u00BB Islands current owner.", ChatFormatting.GRAY));

                CompoundTag tag = item.getOrCreateTag();
                if(this.data.hasOwner()) tag.putString("SkullOwner", playerName);
                item.setTag(tag);


            } else if(i > 10 && i <= 25 && i%9 != 0 && i%9 != 8) {
                if(memberIndex < members.size()) {
                    String playerName = "Unknown";
                    try { playerName = UsernameCache.getBlocking(members.get(memberIndex)); } catch( Exception ex) {}

                    item = new ItemStack(Items.PLAYER_HEAD);
                    item.setHoverName(ServerHelper.formattedText(playerName, ChatFormatting.BLUE, ChatFormatting.BOLD));

                    CompoundTag tag = item.getOrCreateTag();
                    if(this.data.hasOwner()) tag.putString("SkullOwner", playerName);
                    item.setTag(tag);

                    if(!playerName.equals("Unknown") && this.data.isAdmin(player.getUUID())) {
                        UUID member = members.get(memberIndex);
                        item.getOrCreateTagElement("skyblockaddon").putString("member", member.toString()); //Put member in item NBT for click event

                        if(this.data.isAdmin(member)) {
                            ServerHelper.addLore(item,
                                ServerHelper.formattedText("\u00BB Island admin.", ChatFormatting.GRAY),
                                    ServerHelper.formattedText("", ChatFormatting.GRAY),
                                    ServerHelper.formattedText("\u2666 Right-click to demote to member", ChatFormatting.GRAY)
                            );
                            item.setHoverName(ServerHelper.formattedText(playerName, ChatFormatting.RED, ChatFormatting.BOLD));

                        } else {
                            ServerHelper.addLore(item,
                                ServerHelper.formattedText("\u00BB Island member.", ChatFormatting.GRAY),
                                ServerHelper.formattedText("", ChatFormatting.GRAY),
                                ServerHelper.formattedText("\u2666 Right-click to kick player from island", ChatFormatting.GRAY),
                                ServerHelper.formattedText("\u2666 Left-click to promote to admin", ChatFormatting.GRAY)
                            );
                        }
                    } else if(!playerName.equals("Unknown")) {
                        UUID member = player.getServer().getPlayerList().getPlayerByName(playerName).getUUID();
                        if(this.data.isAdmin(member)) {
                            ServerHelper.addLore(item,
                                    ServerHelper.formattedText("\u00BB Island admin.", ChatFormatting.GRAY)
                            );
                            item.setHoverName(ServerHelper.formattedText(playerName, ChatFormatting.RED, ChatFormatting.BOLD));
                        } else {
                            ServerHelper.addLore(item,
                                    ServerHelper.formattedText("\u00BB Island member.", ChatFormatting.GRAY)
                            );
                        }
                    }

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
            case 35:
                player.closeContainer();
                player.getServer().execute(() -> IslandOverviewHandler.openMenu(player, this.data));
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
                return true;
            case 31:
                player.closeContainer();
                player.getServer().execute(() -> InviteOverviewHandler.openMenu(player, this.data));
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
                return true;
            default:
                if(!this.data.isAdmin(player.getUUID())) return false;
                UUID member = UUID.fromString(slot.getItem().getTagElement("skyblockaddon").getString("member"));

                if(!this.data.isAdmin(member))
                    switch(clickType) {
                        case 1:
                            Player member_player = player.getServer().getPlayerList().getPlayer(member);
                            player.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(island -> {
                                this.data.removeIslandMember(member);
                                island.setIsland("");

                                if (member_player != null) {
                                    member_player.teleportTo(player.getLevel().getSharedSpawnPos().getX(), player.getLevel().getSharedSpawnPos().getY(), player.getLevel().getSharedSpawnPos().getZ());
                                    member_player.sendMessage(ServerHelper.formattedText(LanguageFile.getForKey("island.member.kick")), member);
                                }
                            });
                            break;

                        case 0:
                            this.data.makeAdmin(member);
                            break;
                    }
                else
                    switch (clickType) {
                        case 1:
                            this.data.removeAdmin(member);
                            break;
                        case 0:
                            break;
                    }

                ServerHelper.playSongToPlayer(player, SoundEvents.AMETHYST_BLOCK_CHIME, 3f, 1f);
                this.fillInventoryWith(player);
                return true;
        }
    }
}
