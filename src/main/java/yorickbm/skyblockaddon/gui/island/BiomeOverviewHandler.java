package yorickbm.skyblockaddon.gui.island;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.configs.SkyblockAddonLanguageConfig;
import yorickbm.skyblockaddon.gui.ServerOnlyHandler;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.List;

public class BiomeOverviewHandler extends ServerOnlyHandler<IslandData> {

    private final List<Biome> biomes;
    private final int pages;
    private int page = 0;

    protected BiomeOverviewHandler(int syncId, Inventory playerInventory, IslandData data) {
        super(syncId, playerInventory, 5, data);

        this.biomes = ForgeRegistries.BIOMES.getValues().stream().filter(p -> p.getRegistryName().toString().startsWith("minecraft:")).toList();
        this.pages = (int) Math.ceil((biomes.size()-1)/21.0);

        drawBiomes();
    }

    public static void openMenu(Player player, IslandData data) {
        MenuProvider fac = new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return new TextComponent(data.getOwner(player.getServer()).getName() + "'s island");
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int syncId, @NotNull Inventory inv, @NotNull Player player) {
                return new BiomeOverviewHandler(syncId, inv, data);
            }
        };
        player.openMenu(fac);
    }

    @Override
    protected boolean isRightSlot(int slot) {
        return slot == 44 || slot == 39 || slot == 41 || (slot >= 10 && slot <= 34 && slot%9 != 0 && slot%9 != 8);
    }

    @Override
    protected void fillInventoryWith(Player player) {
        for(int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack item = null;

            if (i == 44) {
                item = new ItemStack(Items.ARROW);
                item.setHoverName(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.default.back"), ChatFormatting.RED, ChatFormatting.BOLD));
            } else if(i >= 10 && i <= 34 && i%9 != 0 && i%9 != 8) {
                //BIOME So keep empty
            } else {
                item = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
                item.setHoverName(new TextComponent(""));
            }

            if(item != null) setItem(i, 0, item);
        }
    }

    private void drawBiomes() {
        int biomeIndex = page * 21;

        for(int i = 10; i <= 34; i++) {
            if(i%9 == 0 || i%9 == 8)  continue;

            ItemStack item;
            if (biomeIndex < biomes.size()) {
                Biome biome = biomes.get(biomeIndex);

                item = new ItemStack(Items.PAPER);
                item.setHoverName(ServerHelper.formattedText(
                        biome.getRegistryName().toString()
                                .replace("minecraft:", "")
                                .replace("_", " ")
                ));
                item.getOrCreateTagElement(SkyblockAddon.MOD_ID).putString("biome", biome.getRegistryName().toString()); //Put biome in item NBT for click event
                biomeIndex += 1;
            } else {
                item = new ItemStack(Items.AIR);
            }

            setItem(i, 0, item);
        }

        ItemStack prev = new ItemStack(Items.RED_BANNER);
        prev.setHoverName(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.default.previous"), ChatFormatting.RED, ChatFormatting.BOLD));
        prev.getOrCreateTagElement(SkyblockAddon.MOD_ID).putInt("page", page-1);
        if(page > 0) setItem(39, 0, prev);
        else setItem(39, 0, new ItemStack(Items.GRAY_STAINED_GLASS_PANE).setHoverName(new TextComponent("")));

        ItemStack next = new ItemStack(Items.GREEN_BANNER);
        next.setHoverName(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.default.next"), ChatFormatting.GREEN, ChatFormatting.BOLD));
        next.getOrCreateTagElement(SkyblockAddon.MOD_ID).putInt("page", page+1);
        if(page < (pages-1)) setItem(41, 0, next);
        else setItem(41, 0, new ItemStack(Items.GRAY_STAINED_GLASS_PANE).setHoverName(new TextComponent("")));

        ItemStack info = new ItemStack(Items.BOOK);
        info.setHoverName(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.default.page") + " " + (this.page + 1) + "/" + this.pages));
        setItem(40, 0, info);
    }

    @Override
    protected boolean handleSlotClicked(ServerPlayer player, int index, Slot slot, int clickType) {
        switch (index) {
            case 44 -> {
                player.closeContainer();
                player.getServer().execute(() -> SettingsOverviewHandler.openMenu(player, this.data));
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
                return true;
            }
            case 39, 41 -> {
                page = slot.getItem().getTagElement(SkyblockAddon.MOD_ID).getInt("page");
                drawBiomes();
                ServerHelper.playSongToPlayer(player, SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);
                return true;
            }
            default -> {
                player.closeContainer();
                ServerHelper.playSongToPlayer(player, SoundEvents.AMETHYST_BLOCK_CHIME, 3f, 1f);
                String biomeRegisterName = slot.getItem().getTagElement(SkyblockAddon.MOD_ID).getString("biome");
                Holder<Biome> biomeHolder = player.getLevel()
                        .registryAccess()
                        .registryOrThrow(Registry.BIOME_REGISTRY)
                        .getOrCreateHolder(ResourceKey.create(ForgeRegistries.BIOMES.getRegistryKey(), new ResourceLocation(biomeRegisterName)));
                data.setBiome(player.getLevel(), biomeHolder, biomeRegisterName.replace("minecraft:", "").replace("_", " "));
                player.sendMessage(
                        ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("biome.message").formatted(biomeRegisterName), ChatFormatting.GREEN),
                        player.getUUID());
            }
        }

        return false;
    }
}
