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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarFile;
import java.util.stream.Stream;


public class ResourceManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String[] OLD_PERMISSION_FILES = {
        "general", "storage", "transport", "redstone", "interactables", "admin"
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

        extractResourceDirectory(FMLPath, "registries/permissions");
        extractResourceDirectory(FMLPath, "registries/groups");


        //Generate void protection config
        generateFile(FMLPath, "void_protection.json", "void_protection.json");
        VoidProtectionConfig.getInstance().load(FMLPath.resolve(SkyblockAddonCore.MOD_ID + "/void_protection.json"));

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
     * Dynamically discovers and extracts all JSON files from a resource directory inside the JAR,
     * so no hardcoded file list is needed — whatever is packaged gets deployed.
     *
     * Strategy:
     *  1. Try Paths.get(uri) — works for file: (dev) and union: (Forge production, which registers
     *     its own NIO filesystem provider at startup).
     *  2. Fall back to JarFile scanning for standard jar: URIs (non-Forge / test environments).
     */
    private static void extractResourceDirectory(final Path FMLPath, final String resourceSubDir) {
        final String resourcePath = "/assets/" + SkyblockAddonCore.MOD_ID + "/" + resourceSubDir + "/";
        final URL dirUrl = SkyblockAddonCore.class.getResource(resourcePath);
        if (dirUrl == null) {
            LOGGER.warn("Resource directory not found: {}", resourcePath);
            return;
        }
        LOGGER.debug("Scanning resource directory '{}' via {} URL", resourceSubDir, dirUrl.getProtocol());

        try {
            final URI dirUri = dirUrl.toURI();

            // Primary: NIO path — covers file: (dev) and union: (Forge production)
            try {
                final Path dirPath = Paths.get(dirUri);
                try (final Stream<Path> listing = Files.list(dirPath)) {
                    listing.filter(p -> p.getFileName().toString().endsWith(".json"))
                           .forEach(p -> extractSingleFile(FMLPath, resourceSubDir, p.getFileName().toString()));
                }
                return;
            } catch (final FileSystemNotFoundException ignored) {
                // Not a registered NIO filesystem — fall through to JarFile fallback
            }

            // Fallback: standard jar: protocol (non-Forge environments)
            if ("jar".equals(dirUrl.getProtocol())) {
                final String jarFilePath = URLDecoder.decode(
                        dirUrl.getPath().substring(5, dirUrl.getPath().indexOf("!")),
                        StandardCharsets.UTF_8);
                final String prefix = "assets/" + SkyblockAddonCore.MOD_ID + "/" + resourceSubDir + "/";
                try (final JarFile jar = new JarFile(jarFilePath)) {
                    jar.stream()
                       .filter(e -> !e.isDirectory() && e.getName().startsWith(prefix) && e.getName().endsWith(".json"))
                       .forEach(e -> extractSingleFile(FMLPath, resourceSubDir, e.getName().substring(prefix.length())));
                }
                return;
            }

            LOGGER.warn("Unhandled resource URL protocol '{}' for directory '{}'; no files extracted.",
                    dirUrl.getProtocol(), resourceSubDir);

        } catch (final IOException | URISyntaxException e) {
            LOGGER.error("Failed to scan resource directory '{}': {}", resourceSubDir, e.getMessage());
        }
    }

    private static void extractSingleFile(final Path FMLPath, final String resourceSubDir, final String fileName) {
        try {
            generateFile(FMLPath, resourceSubDir + "/" + fileName, resourceSubDir + "/" + fileName);
        } catch (final ResourceNotFoundException ex) {
            LOGGER.warn("Skipping resource {}: {}", fileName, ex.getMessage());
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
