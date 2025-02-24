package yorickbm.skyblockaddon;

import net.minecraft.world.level.Level;
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
import yorickbm.guilibrary.GUILibraryRegistry;
import yorickbm.guilibrary.events.DefaultEventHandler;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.configs.SkyblockAddonConfig;
import yorickbm.skyblockaddon.events.Gui.GuiEvents;
import yorickbm.skyblockaddon.events.Gui.IslandGuiEvents;
import yorickbm.skyblockaddon.events.Gui.RegistryGuiEvents;
import yorickbm.skyblockaddon.events.ModEvents;
import yorickbm.skyblockaddon.events.ParticleEvents;
import yorickbm.skyblockaddon.events.PermissionEvents;
import yorickbm.skyblockaddon.events.PlayerEvents;
import yorickbm.skyblockaddon.islands.data.IslandData;
import yorickbm.skyblockaddon.permissions.PermissionManager;
import yorickbm.skyblockaddon.util.ResourceManager;
import yorickbm.skyblockaddon.util.ThreadManager;
import yorickbm.skyblockaddon.util.UsernameCache;
import yorickbm.skyblockaddon.util.exceptions.TerralithFoundException;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SkyblockAddon.MOD_ID)
public class SkyblockAddon {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final UUID MOD_UUID = UUID.fromString("36916271-8ff4-483c-9379-bde032a01270");
    public static final UUID MOD_UUID2 = UUID.fromString("36916271-8ff4-483c-9379-bde032a01271");
    public static final String MOD_ID = "skyblockaddon";
    public static final String VERSION = "7.0";

    public static final float UI_SOUND_VOL = 0.5f;
    public static final float UI_SUCCESS_VOL = 3f;
    public static final float EFFECT_SOUND_VOL = 0.2f;
    public static final int ISLAND_BUFFER = 200;
    public static final int ISLAND_SIZE = 400;

    public SkyblockAddon() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::processIMC);
        bus.addListener(this::onCommonSetup);

        //Register configs
        FileUtils.getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve(MOD_ID), MOD_ID);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SkyblockAddonConfig.SPEC, MOD_ID + "/config.toml");

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ModEvents());
        MinecraftForge.EVENT_BUS.register(new PlayerEvents());
        MinecraftForge.EVENT_BUS.register(new PermissionEvents());

        MinecraftForge.EVENT_BUS.register(new DefaultEventHandler());
        MinecraftForge.EVENT_BUS.register(new GuiEvents());
        MinecraftForge.EVENT_BUS.register(new IslandGuiEvents());
        MinecraftForge.EVENT_BUS.register(new RegistryGuiEvents());

        if(SkyblockAddonConfig.getForKey("island.particles.border").equalsIgnoreCase("TRUE")) MinecraftForge.EVENT_BUS.register(new ParticleEvents());
    }

    /**
     * Inter Mod Communications.
     * Checks against Terralith
     */
    private void processIMC(InterModProcessEvent event) {
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
    public void onCommonSetup(final FMLCommonSetupEvent event) {
        //Register Resources
        ResourceManager.commonSetup();

        //Register username cache
        UsernameCache.initCache(500);

        //Register guis
        GUILibraryRegistry.registerFolder(SkyblockAddon.MOD_ID, FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/guis/"));

        //Register permissions
        PermissionManager.getInstance().loadPermissions();

        //Init Version Checker
        VersionChecker.startVersionCheck();
    }

    /**
     * Runs upon server starting event.
     */
    @SubscribeEvent
    public void onServerStarting(final ServerStartingEvent event) {

        //Loading public island owner names into username cache.
        Objects.requireNonNull(event.getServer().getLevel(Level.OVERWORLD)).getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap ->
                cap.getIslands().stream().filter(IslandData::isVisible).forEach(i -> {
                    i.updateName();
                })
        );

        // Check mod version
        final Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(MOD_ID);
        if (modContainer.isPresent()) {
            final VersionChecker.CheckResult result = VersionChecker.getResult(modContainer.get().getModInfo());
            LOGGER.info("Vaulthunters Skyblock addon v{} ({}) has loaded!", VERSION, result.status().name());
        }
    }

    /**
     * Runs upon Shutdown Event of server.
     */
    @SubscribeEvent
    public void onServerShutDown(final ServerStoppedEvent event) {
        try {
            ThreadManager.terminateAllThreads();
        } catch (final NoClassDefFoundError ex) {
            //Seems to be thrown
        }
    }
}
