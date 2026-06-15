package yorickbm.skyblockaddon.core.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.core.SkyblockAddonCore;
import yorickbm.skyblockaddon.core.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.core.configs.VoidProtectionConfig;
import yorickbm.skyblockaddon.core.registries.CategoryRegistry;
import yorickbm.skyblockaddon.core.util.exceptions.ResourceNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


public class ResourceManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String[] OLD_PERMISSION_FILES = {
        "general", "storage", "transport", "redstone", "interactables", "admin"
    };

    private static final String[] NEW_PERMISSION_FILES = {
        "minecraft",
        "ae2", "merequester", "ae2things",
        "refinedstorage", "refinedstorageaddons", "rsrequestify", "extrastorage",
        "simplestoragenetworks",
        "storagedrawers",
        "colossalchests", "sophisticatedbackpacks",
        "create", "moreburners", "createaddition", "railways",
        "mekanism", "mekanismgenerators",
        "fluxnetworks", "thermal", "powah", "irongenerators", "rftoolsbase",
        "xnet", "pipez", "modularrouters",
        "waystones", "elevators", "buildinggadgets", "mininggadgets", "cookingforblockheads",
        "botania", "botanypots",
        "easy_villagers", "easy_piglins",
        "the_vault", "vaultintegrations"
    };


    private static final String[] GROUP_FILES = {
        "minecraft", "quark", "create", "ae2", "waystones", "refinedstorage",
        "storagedrawers", "colossalchests", "easy_villagers", "blockcarpentry",
        "sophisticatedbackpacks", "supplementaries", "the_vault",
        "mekanism", "fluxnetworks", "thermal", "xnet", "botania", "simplestoragenetworks",
        "ironfurnaces", "framedcompactdrawers"
    };

    /**
     * Generate resource file from projects resources folder
     * @param file Path for file destination
     * @param asset File name
     * @throws ResourceNotFoundException If asset is not within projects resources
     */
    private static void generateFile(final Path FMLPath, final String file, final String asset) throws ResourceNotFoundException {
        final File resourceFile = new File(FMLPath.resolve(SkyblockAddonCore.MOD_ID) + "/" +  file);

        try {
            //Determine if file doesnt exists
            if (!resourceFile.exists()) {
                if (resourceFile.createNewFile()) {
                    try (final InputStream in = SkyblockAddonCore.class.getResourceAsStream("/assets/" + SkyblockAddonCore.MOD_ID + "/" + asset)) {
                        if (in == null) {
                            throw new ResourceNotFoundException(asset);
                        }

                        //Copy asset into file
                        Files.copy(in, resourceFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } catch (final IOException e) {
            throw new RuntimeException(resourceFile.getPath() + "\n" + e);
        }
    }

    public static void commonSetup(final Path FMLPath, final RegistrySelector selector) throws ResourceNotFoundException {
        commonSetup(FMLPath, selector, null);
    }

    public static void commonSetup(Path FMLPath, RegistrySelector selector, java.util.function.Predicate<String> isModLoaded) throws ResourceNotFoundException {
        //ResourceManager.getOrCreateDirectory(FMLPath.resolve(SkyblockAddonCore.MOD_ID), SkyblockAddonCore.MOD_ID);

        //Custom island.nbt
        generateFile(FMLPath, "island.nbt", "structures/island.nbt");

        //Custom language.json
        generateFile(FMLPath, "language.json", "lang/en_us.json");
        SkyBlockAddonLanguage.loadLocalization(FMLPath.resolve(SkyblockAddonCore.MOD_ID + "/language.json"));

        //Generate registries
        if (!Files.exists(FMLPath.resolve(SkyblockAddonCore.MOD_ID + "/registries/"))) {
            ResourceManager.getOrCreateDirectory(FMLPath.resolve(SkyblockAddonCore.MOD_ID + "/"), "registries");
        }
        if(!Files.exists(FMLPath.resolve(SkyblockAddonCore.MOD_ID + "/registries/BiomeRegistry.json"))) {
            generateFile(FMLPath, "registries/BiomeRegistry.json", "registries/BiomeRegistry.json");
        }

        //Generate permission + group files
        getOrCreateDirectory(FMLPath.resolve(SkyblockAddonCore.MOD_ID + "/registries/"), "permissions");
        getOrCreateDirectory(FMLPath.resolve(SkyblockAddonCore.MOD_ID + "/registries/"), "groups");

        migrateOldPermissionFiles(FMLPath);

        for (final String name : NEW_PERMISSION_FILES) {
            generateFile(FMLPath, "registries/permissions/" + name + ".json", "registries/permissions/" + name + ".json");
        }

        for (final String name : GROUP_FILES) {
            generateFile(FMLPath, "registries/groups/" + name + ".json", "registries/groups/" + name + ".json");
        }


        //Generate void protection config
        getOrCreateDirectory(FMLPath.resolve(SkyblockAddonCore.MOD_ID + "/"), "configs");
        generateFile(FMLPath, "configs/void_protection.json", "configs/void_protection.json");
        VoidProtectionConfig.getInstance().load(FMLPath.resolve(SkyblockAddonCore.MOD_ID + "/configs/void_protection.json"));

        //Generate category registry (extract default, then load)
        generateFile(FMLPath, "registries/categories.json", "registries/categories.json");
        CategoryRegistry.getInstance().load(FMLPath.resolve(SkyblockAddonCore.MOD_ID + "/registries/categories.json"));

        // Extract GUI files (each individually — only creates if missing, so server edits survive)
        getOrCreateDirectory(FMLPath.resolve(SkyblockAddonCore.MOD_ID + "/"), "guis");
        generateFile(FMLPath, "guis/overview.json", "guis/overview.json");
        generateFile(FMLPath, "guis/settings.json", "guis/settings.json");
        generateFile(FMLPath, "guis/biomes.json", "guis/biomes.json");
        generateFile(FMLPath, "guis/travel.json", "guis/travel.json");
        generateFile(FMLPath, "guis/members.json", "guis/members.json");
        generateFile(FMLPath, "guis/groups.json", "guis/groups.json");
        generateFile(FMLPath, "guis/set_group.json", "guis/set_group.json");
        generateFile(FMLPath, "guis/set_permission.json", "guis/set_permission.json");
        generateFile(FMLPath, "guis/members_group.json", "guis/members_group.json");

        // Generate permissions.json from CategoryRegistry — always overwrite so it stays in
        // sync with the loaded mod set. permissions.json is NOT user-editable; edit categories.json instead.
        if (isModLoaded != null) {
            CategoryRegistry.getInstance().generatePermissionsGui(
                    FMLPath.resolve(SkyblockAddonCore.MOD_ID + "/guis/permissions.json"),
                    isModLoaded
            );
        } else {
            // Fallback: extract static default (shows all categories, no mod-gating)
            generateFile(FMLPath, "guis/permissions.json", "guis/permissions.json");
        }
    }

    /**
     * Removes old category-based permission files when migrating to per-mod structure.
     * Only runs if minecraft.json (new structure marker) does not yet exist.
     */
    private static void migrateOldPermissionFiles(final Path FMLPath) {
        final Path permDir = FMLPath.resolve(SkyblockAddonCore.MOD_ID + "/registries/permissions/");
        if (Files.exists(permDir.resolve("minecraft.json"))) return; // already migrated

        boolean anyDeleted = false;
        for (final String name : OLD_PERMISSION_FILES) {
            final File old = permDir.resolve(name + ".json").toFile();
            if (old.exists() && old.delete()) {
                anyDeleted = true;
                LOGGER.info("Removed legacy permission file: {}.json", name);
            }
        }
        if (anyDeleted) {
            LOGGER.info("Migrated permissions to per-mod structure.");
        }
    }

    /**
     * Ensures the child directory exists inside the parent directory.
     *
     * @param parent the parent Path
     * @param child  the child folder name
     * @throws RuntimeException if creation fails
     */
    public static void getOrCreateDirectory(Path parent, String child) {
        Path dir = parent.resolve(child);
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            //throw new RuntimeException("Failed to create directory: " + dir, e);
        }
    }
}
