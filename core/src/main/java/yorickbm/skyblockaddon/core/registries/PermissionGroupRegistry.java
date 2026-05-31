package yorickbm.skyblockaddon.core.registries;

import com.google.gson.Gson;
import yorickbm.skyblockaddon.core.JSON.PermissionGroupJson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

public class PermissionGroupRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final PermissionGroupRegistry INSTANCE = new PermissionGroupRegistry();

    public static PermissionGroupRegistry getInstance() { return INSTANCE; }

    // group name → (context → patterns)
    private final Map<String, Map<String, List<String>>> groups = new HashMap<>();

    public void clear() { groups.clear(); }

    public int loadFromDirectory(final Path dir, final Predicate<String> isModLoaded) {
        if (!dir.toFile().isDirectory()) return 0;

        int loaded = 0;
        final List<Path> files = new ArrayList<>();
        try {
            Files.list(dir).filter(p -> p.toString().endsWith(".json")).forEach(files::add);
        } catch (final IOException e) {
            LOGGER.error("Failed to list group files in {}: {}", dir, e.getMessage());
            return -1;
        }

        final Gson gson = new Gson();
        for (final Path file : files) {
            try {
                final String json = Files.readString(file);
                final PermissionGroupJson data = gson.fromJson(json, PermissionGroupJson.class);
                if (data == null || data.groups == null) continue;

                // Skip if required mod is not loaded
                if (data.mod != null && !data.mod.isEmpty() && !isModLoaded.test(data.mod)) {
                    LOGGER.info("Skipping group file {} (mod '{}' not loaded)", file.getFileName(), data.mod);
                    continue;
                }

                // Merge groups into registry
                for (final Map.Entry<String, Map<String, List<String>>> entry : data.groups.entrySet()) {
                    mergeGroup(entry.getKey().toLowerCase(), entry.getValue());
                    loaded++;
                }
                final String modLabel = (data.mod != null && !data.mod.isEmpty()) ? data.mod : "always";
                LOGGER.info("Loaded {} group(s) from {} [mod: {}]", data.groups.size(), file.getFileName(), modLabel);
            } catch (final Exception e) {
                LOGGER.error("Failed to load group file {}: {}", file.getFileName(), e.getMessage());
            }
        }
        return loaded;
    }

    private void mergeGroup(final String name, final Map<String, List<String>> contextPatterns) {
        final Map<String, List<String>> existing = groups.computeIfAbsent(name, k -> new HashMap<>());
        for (final Map.Entry<String, List<String>> entry : contextPatterns.entrySet()) {
            existing.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                    .addAll(entry.getValue());
        }
    }

    /**
     * Expand a list of patterns, resolving any #group_name references.
     * Handles negated group refs like !#group_name.
     */
    public List<String> expandPatterns(final String context, final List<String> patterns) {
        if (patterns == null) return List.of();
        final List<String> result = new ArrayList<>();
        for (final String pattern : patterns) {
            if (pattern.startsWith("!#")) {
                final String groupName = pattern.substring(2).toLowerCase();
                getGroupPatterns(context, groupName).stream()
                        .map(p -> "!" + p)
                        .forEach(result::add);
            } else if (pattern.startsWith("#")) {
                final String groupName = pattern.substring(1).toLowerCase();
                result.addAll(getGroupPatterns(context, groupName));
            } else {
                result.add(pattern);
            }
        }
        return result;
    }

    private List<String> getGroupPatterns(final String context, final String groupName) {
        final Map<String, List<String>> group = groups.get(groupName);
        if (group == null) return List.of();
        final List<String> contextPatterns = group.getOrDefault(context, List.of());
        // Recursively expand nested group references
        return expandPatterns(context, contextPatterns);
    }

    public boolean hasGroup(final String name) {
        return groups.containsKey(name.toLowerCase());
    }
}
