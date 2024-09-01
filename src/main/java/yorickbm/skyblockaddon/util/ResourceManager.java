package yorickbm.skyblockaddon.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ResourceManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static CompoundTag IslandNBTData = null;

    private static void generateFile(String file, String asset) {
        try {
            File languageFile = new File(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID) + "/" + file);

            //Determine if file doesnt exists
            if (!languageFile.exists()) {
                if (languageFile.createNewFile()) {
                    try (InputStream in = SkyblockAddon.class.getResourceAsStream("/assets/" + SkyblockAddon.MOD_ID + "/" + asset)) {
                        if (in == null) {
                            LOGGER.error("Resource not found '/assets/{}/{}'!", SkyblockAddon.MOD_ID, asset);
                            return;
                        }

                        //Copy asset into file
                        Files.copy(in, languageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void generateIslandNBTFile() {
        try (InputStream in = SkyblockAddon.class.getResourceAsStream("/assets/" + SkyblockAddon.MOD_ID + "/structures/island.nbt")) {
            if (in == null) {
                LOGGER.error("Resource not found '/assets/{}/structures/island.nbt'!", SkyblockAddon.MOD_ID);
                return;
            }
            CompoundTag nbt = NbtIo.readCompressed(in);
            File islandFile = new File(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID) + "/island.nbt");
            if (!islandFile.exists()) {
                if (islandFile.createNewFile()) { // Make sure we could create the new file
                    NbtIo.writeCompressed(nbt, islandFile);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load custom .NBT file for island structure
     *
     * @param server
     * @return NBT data
     */
    public static CompoundTag getIslandNBT(MinecraftServer server) {
        if (IslandNBTData == null) {
            try {
                File islandFile = new File(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID) + "/island.nbt");
                IslandNBTData = NbtIo.readCompressed(islandFile);
            } catch (IOException e) {
                LOGGER.error("Could not load external island.nbt file, using mod's internal island.nbt file.");
                try {
                    Resource rs = server.getResourceManager().getResource(new ResourceLocation(SkyblockAddon.MOD_ID, "structures/island.nbt"));
                    IslandNBTData = NbtIo.readCompressed(rs.getInputStream());
                } catch (IOException ex) {
                    LOGGER.error("Could not load mod's internal island.nbt file!!!");
                }
            }
        }
        return IslandNBTData;
    }

    public static void commonSetup() {
        FileUtils.getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID), SkyblockAddon.MOD_ID);

        //Custom island.nbt
        ResourceManager.generateIslandNBTFile();

        //Custom language.json
        generateFile("language.json", "lang/en_us.json");
        SkyBlockAddonLanguage.loadLocalization(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/language.json"));

        //Generate registries
        if (!Files.exists(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/registries/"))) {
            FileUtils.getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/registries/"), SkyblockAddon.MOD_ID + "/registries/");

            generateFile("registries/BiomeRegistry.json", "registries/BiomeRegistry.json");
            generateFile("registries/PermissionRegistry.json", "registries/PermissionRegistry.json");
        }

        //Generate GUIS
        if (!Files.exists(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/guis/"))) {
            FileUtils.getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/guis/"), SkyblockAddon.MOD_ID + "/guis/");

            generateFile("guis/overview.json", "guis/overview.json");
            generateFile("guis/settings.json", "guis/settings.json");
            generateFile("guis/biomes.json", "guis/biomes.json");
            generateFile("guis/travel.json", "guis/travel.json");
            generateFile("guis/members.json", "guis/members.json");
            //TODO: Add other GUIS
        }
    }
}
