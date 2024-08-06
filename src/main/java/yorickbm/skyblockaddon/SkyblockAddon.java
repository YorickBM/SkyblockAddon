package yorickbm.skyblockaddon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
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
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.configs.SkyblockAddonConfig;
import yorickbm.skyblockaddon.configs.SkyblockAddonLanguageConfig;
import yorickbm.skyblockaddon.util.ThreadManager;
import yorickbm.skyblockaddon.util.UsernameCache;
import yorickbm.skyblockaddon.util.exceptions.TerralithFoundException;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SkyblockAddon.MOD_ID)
public class SkyblockAddon {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "skyblockaddon";
    public static final String VERSION = "7.0";

    public static final float UI_SOUND_VOL = 0.5f;
    public static final float EFFECT_SOUND_VOL = 0.2f;
    public static final int ISLAND_BUFFER = 200;
    public static final int ISLAND_SIZE = 400;

    private static MinecraftServer serverInstance;
    public static MinecraftServer getServerInstance() { return serverInstance; }

    public SkyblockAddon() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::processIMC);

        //Register username cache
        UsernameCache.initCache(120);

        //Register configs
        FileUtils.getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve(MOD_ID), MOD_ID);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SkyblockAddonLanguageConfig.SPEC, MOD_ID + "/language.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SkyblockAddonConfig.SPEC, MOD_ID + "/config.toml");

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
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
        if(ModList.get().isLoaded("terralith")) {
            LOGGER.error("Beware, skyblockaddon mod is loaded together with Terralith!");
            throw new TerralithFoundException();
        }
    }

    /**
     * Runs upon server starting event
     * Responsible for loading resources
     */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

        serverInstance = event.getServer();

        //Custom island.nbt
        try {
            Resource rs = event.getServer().getResourceManager().getResource(new ResourceLocation(SkyblockAddon.MOD_ID, "structures/island.nbt"));
            CompoundTag nbt = NbtIo.readCompressed(rs.getInputStream());
            File islandFile = new File(FMLPaths.CONFIGDIR.get().resolve(MOD_ID) + "/island.nbt");
            if(!islandFile.exists()) {
                if(islandFile.createNewFile()) { // Make sure we could create the new file
                    NbtIo.writeCompressed(nbt, islandFile);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Check mod version
        Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(MOD_ID);
        if(modContainer.isPresent()) {
            VersionChecker.CheckResult result = VersionChecker.getResult(modContainer.get().getModInfo());
            LOGGER.info("Vaulthunters Skyblock addon v" + VERSION + " (" + result.status().name() + ") has loaded!");
        }
    }

    /**
     * Runs upon Shutdown Event of server.
     * Responsible for terminating all current running threads.
     */
    @SubscribeEvent
    public void onServerShutDown(ServerStoppedEvent event) {
        try {
            ThreadManager.terminateAllThreads();
        } catch(NoClassDefFoundError ex) {
            //Seems to be thrown
        }
    }
}
