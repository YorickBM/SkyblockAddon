package yorickbm.skyblockaddon;

import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.capabilities.Providers.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.Providers.PlayerIslandProvider;
import yorickbm.skyblockaddon.events.BlockEvents;
import yorickbm.skyblockaddon.events.ModEvents;
import yorickbm.skyblockaddon.events.PlayerEvents;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.util.LanguageFile;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Main.MOD_ID)
public class Main {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "skyblockaddon";
    public static final String VERSION = "4.0";

    public static final float UI_SOUND_VOL = 0.5f;
    public static final float EFFECT_SOUND_VOL = 0.2f;
    public static List<Item> Allowed_Clickable_Blocks = new ArrayList<>();
    public static List<Item> Allowed_Clickable_Items = new ArrayList<>();
    public static List<Item> Left_Clickable_Blocks = new ArrayList<>();
    public static List<EntityType> Allowed_Fake_Player = new ArrayList<>();

    public static List<Integer> islandUIIds = new ArrayList<>();

    public Main() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);
        bus.addListener(this::enqueueIMC);
        bus.addListener(this::processIMC);

        LanguageFile.init();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ModEvents());
        MinecraftForge.EVENT_BUS.register(new BlockEvents());
        MinecraftForge.EVENT_BUS.register(new PlayerEvents());

        //Register username cache
        UsernameCache.initCache(120);
    }

    private void setup(final FMLCommonSetupEvent event) {
        //PRE INIT
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m -> m.messageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        registerMods();
        LOGGER.info("Vaulthunters Skyblock addon v"+VERSION+" has loaded!");
    }

    public static IslandData CheckOnIsland(Player player) {
        if(player.getLevel().dimension() != Level.OVERWORLD || player.hasPermissions(3)) return null; //Non overworld events we ignore //
        AtomicReference<IslandData> island = new AtomicReference<>(null);

        player.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(playerIsland ->
            player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(islandGenerator -> {

            String islandIdOn = islandGenerator.getIslandIdByLocation(new Vec3i(player.getX(), 121, player.getZ()));
            System.out.println("User is on island: '" + islandIdOn + "'");
            if(islandIdOn == null || islandIdOn.equals("")) return; //Not on an island so we do not affect permission

            island.set(islandGenerator.getIslandById(islandIdOn));
        }));

        return island.get(); //Not any island
    }

    private void registerMods() {
        //Whitelist inventories
        Allowed_Clickable_Blocks.add(Items.ENDER_CHEST);
        if(ModList.get().isLoaded("sophisticatedbackpacks")) {
            ForgeRegistries.ITEMS.getHolder(new ResourceLocation("sophisticatedbackpacks:backpack"))
                    .ifPresentOrElse(h -> Allowed_Clickable_Items.add(h.value()), () -> LOGGER.warn("Could not find item sophisticatedbackpacks:backpack"));
            ForgeRegistries.ITEMS.getHolder(new ResourceLocation("sophisticatedbackpacks:iron_backpack"))
                    .ifPresentOrElse(h -> Allowed_Clickable_Items.add(h.value()), () -> LOGGER.warn("Could not find item sophisticatedbackpacks:iron_backpack"));
            ForgeRegistries.ITEMS.getHolder(new ResourceLocation("sophisticatedbackpacks:gold_backpack"))
                    .ifPresentOrElse(h -> Allowed_Clickable_Items.add(h.value()), () -> LOGGER.warn("Could not find item sophisticatedbackpacks:gold_backpack"));
            ForgeRegistries.ITEMS.getHolder(new ResourceLocation("sophisticatedbackpacks:diamond_backpack"))
                    .ifPresentOrElse(h -> Allowed_Clickable_Items.add(h.value()), () -> LOGGER.warn("Could not find item sophisticatedbackpacks:diamond_backpack"));
            ForgeRegistries.ITEMS.getHolder(new ResourceLocation("sophisticatedbackpacks:netherite_backpack"))
                    .ifPresentOrElse(h -> Allowed_Clickable_Items.add(h.value()), () -> LOGGER.warn("Could not find item sophisticatedbackpacks:netherite_backpack"));

            LOGGER.info("Vaulthunters Skyblock addon loaded 'sophisticated backpacks' items into allowed items.");
        }
        if(ModList.get().isLoaded("easy_villagers")) {
            LOGGER.info("Vaulthunters Skyblock addon loaded 'easy villagers' pickup villager protection for islands.");
        }
        if(ModList.get().isLoaded("storagedrawers")) {
            //Controller, slave & compacting drawe
            register("storagedrawers", Left_Clickable_Blocks,
                    "compacting_drawers_3",
                    "controller",
                    "controller_slave"
                );

            //Basic drawers for different wood types
            String[] draw_types = new String[]{"%s_full_drawers_1", "%s_full_drawers_2", "%s_full_drawers_4", "%s_half_drawers_1", "%s_half_drawers_2", "%s_half_drawers_4"};
            Arrays.stream(draw_types).forEach(s -> register("storagedrawers", Left_Clickable_Blocks, s.formatted("oak")));
            Arrays.stream(draw_types).forEach(s -> register("storagedrawers", Left_Clickable_Blocks, s.formatted("spruce")));
            Arrays.stream(draw_types).forEach(s -> register("storagedrawers", Left_Clickable_Blocks, s.formatted("birch")));
            Arrays.stream(draw_types).forEach(s -> register("storagedrawers", Left_Clickable_Blocks, s.formatted("jungle")));
            Arrays.stream(draw_types).forEach(s -> register("storagedrawers", Left_Clickable_Blocks, s.formatted("acacia")));
            Arrays.stream(draw_types).forEach(s -> register("storagedrawers", Left_Clickable_Blocks, s.formatted("dark_oak")));
            Arrays.stream(draw_types).forEach(s -> register("storagedrawers", Left_Clickable_Blocks, s.formatted("crimson")));
            Arrays.stream(draw_types).forEach(s -> register("storagedrawers", Left_Clickable_Blocks, s.formatted("warped")));

            LOGGER.info("Vaulthunters Skyblock addon loaded 'storage drawers' left-click protection for islands.");
        }
        if(ModList.get().isLoaded("framedcompactdrawers")) {
            //Different framed drawers
            register("framedcompactdrawers", Left_Clickable_Blocks,
                    "framed_compact_drawer",
                    "framed_drawer_controller",
                    "framed_slave",
                    "framed_full_one",
                    "framed_full_two",
                    "framed_full_four",
                    "framed_half_one",
                    "framed_half_two",
                    "framed_half_four"
                );

            LOGGER.info("Vaulthunters Skyblock addon loaded 'framed compact drawers' left-click protection for islands.");
        }

        if(ModList.get().isLoaded("create")) {
            ForgeRegistries.ENTITIES.getHolder(new ResourceLocation("create:deployer"))
                    .ifPresentOrElse(e -> Allowed_Fake_Player.add(e.value()), () -> LOGGER.warn("Could not find entity create:deployer"));
            LOGGER.info("Vaulthunters Skyblock addon loaded 'create' allow deployers for islands.");
        }
    }

    private void register(String modId, List<Item> list, String ...items) {
        for(String item : items) {
            ForgeRegistries.ITEMS.getHolder(new ResourceLocation(modId + ":" + item))
                    .ifPresentOrElse(h -> list.add(h.value()), () -> LOGGER.warn("Could not find item "+modId+":"+item));
        }
    }
}
