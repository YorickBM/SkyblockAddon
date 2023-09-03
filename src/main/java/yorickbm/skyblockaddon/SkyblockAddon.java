package yorickbm.skyblockaddon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.capabilities.Providers.IslandGeneratorProvider;
import yorickbm.skyblockaddon.events.BlockEvents;
import yorickbm.skyblockaddon.events.ModEvents;
import yorickbm.skyblockaddon.events.PlayerEvents;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.util.LanguageFile;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SkyblockAddon.MOD_ID)
public class SkyblockAddon {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "skyblockaddon";
    public static final String VERSION = "5.1";

    public static final float UI_SOUND_VOL = 0.5f;
    public static final float EFFECT_SOUND_VOL = 0.2f;

    public SkyblockAddon() {
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

        //Register configs
        //ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SkyblockAddonLanguageConfig.SPEC, "skyblockaddon-language.toml");
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
        VersionChecker.CheckResult result = VersionChecker.getResult(ModList.get().getModContainerById(MOD_ID).get().getModInfo());
        LOGGER.info("Vaulthunters Skyblock addon v"+VERSION+" ("+result.status().name()+") has loaded!");

        if(ModList.get().isLoaded("terralith")) {
            LOGGER.error("Beware, skyblockaddon mod is loaded together with Terralith!");
        }
    }

    public static IslandData CheckOnIsland(Entity player) {
        AtomicReference<IslandData> island = new AtomicReference<>(null);

        player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(islandGenerator -> {

            String islandIdOn = islandGenerator.getIslandIdByLocation(new Vec3i(player.getX(), 121, player.getZ()));
            if(islandIdOn == null || islandIdOn.equals("")) return; //Not on an island so we do not affect permission

            IslandData data = islandGenerator.getIslandById(islandIdOn);
            island.set(data);
        });

        return island.get(); //Not any island
    }

    public static IslandData PlayerPartOfIslandByPos(BlockPos pos, ServerLevel level) {
        AtomicReference<IslandData> island = new AtomicReference<>(null);

        level.getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(ig -> {
            String islandIdCoords = ig.getIslandIdByLocation(pos);
            if(islandIdCoords == null || islandIdCoords.equals("")) return; //Not on an island at coords so we ignore

            IslandData data = ig.getIslandById(islandIdCoords);
            island.set(data);
        });

        return island.get();
    }
}
