package yorickbm.skyblockaddon.configs;

import com.google.gson.Gson;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.loading.FMLPaths;
import yorickbm.skyblockaddon.SkyblockAddon;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SkyBlockAddonLanguage {
    private static final Gson GSON = new Gson();
    private static final Map<String, String> LANGUAGE_MAP = new HashMap<>();

    public static void loadLocalization(MinecraftServer server) {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID).resolve("/language.json");
        try (BufferedReader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
            Map<String, String> map = GSON.fromJson(reader, HashMap.class);
            LANGUAGE_MAP.putAll(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLocalizedString(String key) {
        return LANGUAGE_MAP.getOrDefault(key, key);
    }
}
