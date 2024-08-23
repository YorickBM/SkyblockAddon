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

    public static void generateLanguageFile() {
        try {
            File languageFile = new File(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID) + "/language.json");

            //Determine if file doesnt exists
            if (!languageFile.exists()) {
                if (languageFile.createNewFile()) {
                    try (InputStream in = SkyblockAddon.class.getResourceAsStream("/assets/" + SkyblockAddon.MOD_ID + "/lang/en_us.json")) {
                        if (in == null) {
                            LOGGER.error("Resource not found '/assets/{}/lang/en_us.json'!", SkyblockAddon.MOD_ID);
                            return;
                        }
                        Files.copy(in, languageFile.toPath(), StandardCopyOption.REPLACE_EXISTING); //Replace it
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void generateGUIFile(String name) {
        try {
            File guiFile = new File(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID) + "/guis/" + name + ".json");
            if (!guiFile.exists()) {
                if (guiFile.createNewFile()) {
                    try (InputStream in = SkyblockAddon.class.getResourceAsStream("/assets/" + SkyblockAddon.MOD_ID + "/guis/" + name + ".json")) {
                        if (in == null) {
                            LOGGER.error("Resource not found '/assets/{}/guis/{}.json'!", SkyblockAddon.MOD_ID, name);
                            return;
                        }
                        Files.copy(in, guiFile.toPath(), StandardCopyOption.REPLACE_EXISTING); //Replace it
                    }
                }
            }
        } catch (Exception e) {
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
        ResourceManager.generateLanguageFile();
        SkyBlockAddonLanguage.loadLocalization(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/language.json"));

        if (!Files.exists(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/guis/"))) {
            FileUtils.getOrCreateDirectory(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/guis/"), SkyblockAddon.MOD_ID + "/guis/");

            //Generate GUIS
            ResourceManager.generateGUIFile("overview");
            ResourceManager.generateGUIFile("settings");
            ResourceManager.generateGUIFile("biomes");
            ResourceManager.generateGUIFile("travel");
            //TODO: Add other GUIS
        }
    }
}
