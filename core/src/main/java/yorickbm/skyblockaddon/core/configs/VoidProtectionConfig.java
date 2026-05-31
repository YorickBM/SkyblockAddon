package yorickbm.skyblockaddon.core.configs;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class VoidProtectionConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final VoidProtectionConfig INSTANCE = new VoidProtectionConfig();

    public static VoidProtectionConfig getInstance() { return INSTANCE; }

    private List<String> entities = new ArrayList<>();

    private static class VoidProtectionJson {
        List<String> entities;
    }

    public void load(final Path path) {
        try {
            final String content = Files.readString(path);
            final VoidProtectionJson data = GSON.fromJson(content, VoidProtectionJson.class);
            if (data == null || data.entities == null) {
                LOGGER.warn("VoidProtectionConfig at {} is empty or malformed, using defaults", path);
                return;
            }
            entities = data.entities;
            LOGGER.info("Loaded void protection config: {} entity pattern(s)", entities.size());
        } catch (final IOException e) {
            LOGGER.error("Failed to load VoidProtectionConfig from {}: {}", path, e.getMessage());
        }
    }

    public boolean shouldProtect(final String entityTypeId) {
        for (final String pattern : entities) {
            if (Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(entityTypeId).matches()) {
                return true;
            }
        }
        return false;
    }
}
