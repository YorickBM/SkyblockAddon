package yorickbm.skyblockaddon.gui.island;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Vec3i;
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
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.configs.SkyblockAddonLanguageConfig;
import yorickbm.skyblockaddon.gui.ServerOnlyHandler;
import yorickbm.skyblockaddon.gui.permission.PermissionGroupOverviewHandler;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.util.ServerHelper;

public class SettingsOverviewHandler extends ServerOnlyHandler<IslandData> {
    protected SettingsOverviewHandler(int syncId, Inventory playerInventory, IslandData data) {
        super(syncId, playerInventory, 3, data);
    }

    public static void openMenu(Player player, IslandData data) {
        MenuProvider fac = new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return new TextComponent(data.getOwner(player.getServer()).getName() + "'s island");
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int syncId, @NotNull Inventory inv, @NotNull Player player) {
                return new SettingsOverviewHandler(syncId, inv, data);
            }
        };
        player.openMenu(fac);
    }

    @Override
    protected boolean isRightSlot(int slot) {
        return slot == 10 || slot == 12 || slot == 14 || slot == 16 || slot == 26;
    }

    @Override
    protected void fillInventoryWith(Player player) {
        for(int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack item;
            switch (i) {
                case 10 -> {
                    item = new ItemStack(Items.OAK_SAPLING);
                    item.setHoverName(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.biome.title"), ChatFormatting.BOLD, ChatFormatting.BLUE));
                    ServerHelper.addLore(item,
                            ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.biome.desc"), ChatFormatting.GRAY),
                            ServerHelper.formattedText(" "),
                            ServerHelper.combineComponents(
                                    ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.biome.current") + " ", ChatFormatting.GRAY),
                                    ServerHelper.formattedText(data.getBiome(), ChatFormatting.WHITE)
                            ));
                }
                case 12 -> {
                    item = new ItemStack(Items.IRON_BARS);
                    item.setHoverName(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.permissions.title"), ChatFormatting.BOLD, ChatFormatting.BLUE));
                    ServerHelper.addLore(item,
                            ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.permissions.desc"), ChatFormatting.GRAY)
                    );
                }
                case 14 -> {
                    item = new ItemStack(Items.RED_BED);
                    item.setHoverName(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.spawn.title"), ChatFormatting.BOLD, ChatFormatting.BLUE));
                    ServerHelper.addLore(item,
                            ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.spawn.desc"), ChatFormatting.GRAY),
                            ServerHelper.formattedText(" "),
                            ServerHelper.combineComponents(
                                    ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.spawn.current") + " ", ChatFormatting.GRAY),
                                    ServerHelper.formattedText(data.getSpawn().getX() + ", " + data.getSpawn().getY() + ", " + data.getSpawn().getZ(), ChatFormatting.WHITE)
                            ),
                            ServerHelper.combineComponents(
                                    ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.spawn.new") + " ", ChatFormatting.GRAY),
                                    ServerHelper.formattedText(Math.round(player.position().x) + ", " + Math.round(player.position().y) + ", " + Math.round(player.position().z), ChatFormatting.WHITE)
                            )
                    );
                }
                case 16 -> {
                    item = new ItemStack(Items.OAK_BOAT);
                    item.setHoverName(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.travels.title"), ChatFormatting.BOLD, ChatFormatting.BLUE));
                    ServerHelper.addLore(item,
                            ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.travels.desc"), ChatFormatting.GRAY),
                            ServerHelper.formattedText(" "),
                            ServerHelper.combineComponents(
                                    ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.travels.current") + " ", ChatFormatting.GRAY),
                                    ServerHelper.formattedText(this.data.getTravelability() ? SkyblockAddonLanguageConfig.getForKey("guis.travels.public") : SkyblockAddonLanguageConfig.getForKey("guis.travels.private"), ChatFormatting.WHITE)
                            ),
                            ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.travels.onclick"), ChatFormatting.GRAY)
                    );
                }
                case 26 -> {
                    item = new ItemStack(Items.ARROW);
                    item.setHoverName(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.default.back"), ChatFormatting.RED, ChatFormatting.BOLD));
                }
                default -> {
                    item = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
                    item.setHoverName(new TextComponent(""));
                }
            }
            setItem(i, 0, item);
        }
    }

    @Override
    protected boolean handleSlotClicked(ServerPlayer player, int index, Slot slot, int clickType) {

        if (index == 0) {
            player.closeContainer();
            ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
            return true;
        }

        switch (index) {
            case 10 -> {
                player.closeContainer();
                player.getServer().execute(() -> BiomeOverviewHandler.openMenu(player, data));
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
                return true;
            }
            case 12 -> {
                player.closeContainer();
                player.getServer().execute(() -> PermissionGroupOverviewHandler.openMenu(player, data));
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
                return true;
            }
            case 14 -> {
                player.closeContainer();
                if (!this.data.getIslandBoundingBox().isInside(player.blockPosition())) {
                    ServerHelper.playSongToPlayer(player, SoundEvents.AMETHYST_BLOCK_BREAK, 3f, 1f);
                    player.sendMessage(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.island.setspawn.notallowed"), ChatFormatting.RED), player.getUUID());
                    return false;
                }
                ServerHelper.playSongToPlayer(player, SoundEvents.AMETHYST_BLOCK_CHIME, 3f, 1f);
                data.setSpawn(new Vec3i(player.position().x, player.position().y, player.position().z));
                return true;
            }
            case 16 -> {
                this.data.setTravelability(!this.data.getTravelability());
                fillInventoryWith(player);
                ServerHelper.playSongToPlayer(player, SoundEvents.AMETHYST_BLOCK_BREAK, 3f, 1f);
                return true;
            }
            case 26 -> {
                player.closeContainer();
                player.getServer().execute(() -> IslandOverviewHandler.openMenu(player, this.data));
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
                return true;
            }
        }

        return true;
    }
}
