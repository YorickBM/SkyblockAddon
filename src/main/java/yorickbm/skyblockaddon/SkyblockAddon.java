package yorickbm.skyblockaddon;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.configs.SkyblockAddonConfig;
import yorickbm.skyblockaddon.events.ModEvents;
import yorickbm.skyblockaddon.gui.GUIManager;
import yorickbm.skyblockaddon.util.ResourceManager;
import yorickbm.skyblockaddon.util.ThreadManager;
import yorickbm.skyblockaddon.util.UsernameCache;
import yorickbm.skyblockaddon.util.exceptions.TerralithFoundException;

import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SkyblockAddon.MOD_ID)
public class SkyblockAddon {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final UUID MOD_UUID = UUID.fromString("36916271-8ff4-483c-9379-bde032a01270");
    public static final String MOD_ID = "skyblockaddon";
    public static final String VERSION = "7.0";

    public static final float UI_SOUND_VOL = 0.5f;
    public static final float UI_SUCCESS_VOL = 3f;
    public static final float EFFECT_SOUND_VOL = 0.2f;
    public static final int ISLAND_BUFFER = 200;
    public static final int ISLAND_SIZE = 400;

    public SkyblockAddon() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::processIMC);
        bus.addListener(this::onCommonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ModEvents());
    }

    /**
     * Inter Mod Communications.
     * Checks against Terralith
     */
    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m -> m.messageSupplier().get()).
                collect(Collectors.toList()));

        // Determine if Terralith is found
        if (ModList.get().isLoaded("terralith")) {
            LOGGER.error("Beware, skyblockaddon mod is loaded together with Terralith!");
            throw new TerralithFoundException();
        }
    }

    /**
     * Run Common config setup.
     */
    public void onCommonSetup(FMLCommonSetupEvent event) {
        ResourceManager.commonSetup();

        //Register username cache
        UsernameCache.initCache(120);

        //Register configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SkyblockAddonConfig.SPEC, MOD_ID + "/config.toml");

        //Register guis
        GUIManager.getInstance().loadAllGUIS(); //Load guis from file
    }

    /**
     * Runs upon server starting event.
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Check mod version
        Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(MOD_ID);
        if (modContainer.isPresent()) {
            VersionChecker.CheckResult result = VersionChecker.getResult(modContainer.get().getModInfo());
            LOGGER.info("Vaulthunters Skyblock addon v{} ({}) has loaded!", VERSION, result.status().name());
        }
    }

    /**
     * Runs upon Shutdown Event of server.
     */
    @SubscribeEvent
    public void onServerShutDown(ServerStoppedEvent event) {
        try {
            ThreadManager.terminateAllThreads();
        } catch (NoClassDefFoundError ex) {
            //Seems to be thrown
        }
    }
}
