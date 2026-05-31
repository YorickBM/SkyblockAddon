package yorickbm.skyblockaddon.core.permissions;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.core.JSON.PermissionJson;
import yorickbm.skyblockaddon.core.registries.PermissionGroupRegistry;
import yorickbm.skyblockaddon.core.util.JSON.JSONEncoder;
import yorickbm.skyblockaddon.core.util.MatchResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PermissionManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private List<Permission> permissions = new ArrayList<>();

    private static final PermissionManager instance = new PermissionManager();
    public static PermissionManager getInstance() { return instance; }

    public PermissionManager() {
        this.permissions = new ArrayList<>();
    }

    // ── Loading ────────────────────────────────────────────────────────────

    /**
     * Load from a single file (old format, backward compat).
     * Replaces all previously loaded permissions.
     */
    public int loadPermissions(final Path path) {
        permissions.clear();
        if (!path.toFile().isFile()) return -1;
        try {
            final PermissionJson json = JSONEncoder.loadFromFile(path, PermissionJson.class);
            if (json.permissions != null) resolveAndAdd(json.permissions);
            sortByPriority();
            return permissions.size();
        } catch (final Exception ex) {
            LOGGER.error("Failed to load permissions from {}: {}", path, ex.getMessage());
            return -1;
        }
    }

    /**
     * Load from a directory of JSON files, each optionally gated by a mod.
     * Replaces all previously loaded permissions.
     */
    public int loadPermissions(final Path dir, final Predicate<String> isModLoaded) {
        permissions.clear();
        if (!dir.toFile().isDirectory()) return -1;

        final List<Path> files = new ArrayList<>();
        try {
            Files.list(dir).filter(p -> p.toString().endsWith(".json")).forEach(files::add);
        } catch (final IOException e) {
            LOGGER.error("Failed to scan permissions directory {}: {}", dir, e.getMessage());
            return -1;
        }

        final Gson gson = new Gson();
        for (final Path file : files) {
            try {
                final String content = Files.readString(file);
                final PermissionJson json = gson.fromJson(content, PermissionJson.class);
                if (json == null || json.permissions == null) continue;

                if (json.mod != null && !json.mod.isEmpty() && !isModLoaded.test(json.mod)) {
                    LOGGER.debug("Skipping permission file {} (mod '{}' not loaded)", file.getFileName(), json.mod);
                    continue;
                }

                resolveAndAdd(json.permissions);
                LOGGER.debug("Loaded {} permissions from {}", json.permissions.size(), file.getFileName());
            } catch (final Exception e) {
                LOGGER.error("Failed to load permission file {}: {}", file.getFileName(), e.getMessage());
            }
        }

        sortByPriority();
        return permissions.size();
    }

    private void resolveAndAdd(final List<Permission> loaded) {
        for (final Permission p : loaded) {
            if (p.getData() != null) {
                p.getData().resolve(PermissionGroupRegistry.getInstance());
            }
            permissions.add(p);
        }
    }

    private void sortByPriority() {
        permissions.sort(Comparator.comparingInt(Permission::getPriority).reversed());
    }

    // ── Queries ────────────────────────────────────────────────────────────

    public List<Permission> getPermissions() { return permissions; }

    public List<Permission> getPermissionsFor(final String category) {
        return permissions.stream()
                .filter(pm -> pm.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Permission> getPermissionsForTrigger(final String trigger) {
        return permissions.stream()
                .filter(pm -> pm.hasTrigger(trigger))
                .collect(Collectors.toList());
    }

    // ── Match logic ────────────────────────────────────────────────────────

    /**
     * Run permission filter logic.
     *
     * @return SKIP  – a negated rule matched (item/block explicitly excluded)
     *         BLOCK – a positive rule matched (or all-negation list with no match)
     *         SKIP  – no rule matched at all (onlyNegations = false)
     */
    public static MatchResult checkMatch(final List<String> rules, final String item) {
        boolean foundNonNegatedMatch = false;
        boolean onlyNegations = true;

        if (rules.isEmpty()) return MatchResult.BLOCK;

        for (final String rule : rules) {
            final boolean isNegation = rule.startsWith("!");
            final String patternString = isNegation ? rule.substring(1) : rule;
            final Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);

            if (pattern.matcher(item).matches()) {
                if (isNegation) return MatchResult.SKIP;
                else foundNonNegatedMatch = true;
            }

            if (!isNegation) onlyNegations = false;
        }

        return foundNonNegatedMatch ? MatchResult.BLOCK
                : (onlyNegations ? MatchResult.BLOCK : MatchResult.SKIP);
    }

    /**
     * Check whether any negated pattern in the list explicitly matches the value.
     * Used by InteractionValidator to detect explicit exclusions.
     */
    public static boolean hasExplicitNegation(final List<String> patterns, final String value) {
        for (final String p : patterns) {
            if (!p.startsWith("!")) continue;
            final String pat = p.substring(1);
            if (Pattern.compile(pat, Pattern.CASE_INSENSITIVE).matcher(value).matches()) return true;
        }
        return false;
    }
}
