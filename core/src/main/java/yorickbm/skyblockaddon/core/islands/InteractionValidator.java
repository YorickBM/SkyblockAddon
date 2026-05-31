package yorickbm.skyblockaddon.core.islands;

import yorickbm.skyblockaddon.core.JSON.PermissionDataJson;
import yorickbm.skyblockaddon.core.permissions.Permission;
import yorickbm.skyblockaddon.core.permissions.PermissionManager;
import yorickbm.skyblockaddon.core.util.MatchResult;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class InteractionValidator {

    /**
     * Primary check: context-map approach with priority.
     *
     * Permissions are pre-sorted by priority (high → low) in PermissionManager.
     * The first permission that CLAIMS the action determines the result:
     *   - If the group has the permission → ALLOW (return false)
     *   - If not → BLOCK (return true)
     *
     * A permission "claims" an action when:
     *   - All provided contexts have no explicit negation matching the value, AND
     *   - At least one context filter matches (BLOCK result), OR all filters are empty (binary)
     *
     * A permission is SKIPPED when any context has an explicit negation matching the value.
     *
     * @param group         Island group the player belongs to
     * @param perms         Permissions for the trigger (sorted by priority)
     * @param contextValues Map of context → value, e.g. {"block": "minecraft:chest", "item": "minecraft:stick"}
     * @return true if blocked
     */
    public static boolean checkPermission(final IslandGroup group,
                                          final List<Permission> perms,
                                          final Map<String, String> contextValues) {
        if (group == null) return false;

        for (final Permission perm : perms) {
            if (group.canDo(perm.getId())) continue;

            boolean skipThisPerm = false;
            boolean anyMatch = false;
            boolean hasData = false;

            for (final Map.Entry<String, String> ctx : contextValues.entrySet()) {
                final List<String> patterns = perm.getData().getFiltersForContext(ctx.getKey());
                if (patterns == null) continue;    // context not set → not relevant
                hasData = true;

                if (patterns.isEmpty()) {
                    // Binary context (old format empty array = match everything)
                    anyMatch = true;
                    continue;
                }

                // Explicit negation → skip this permission for this action
                if (PermissionManager.hasExplicitNegation(patterns, ctx.getValue())) {
                    skipThisPerm = true;
                    break;
                }

                if (PermissionManager.checkMatch(patterns, ctx.getValue()) == MatchResult.BLOCK) {
                    anyMatch = true;
                }
            }

            if (skipThisPerm) continue;              // explicitly excluded → try lower priority

            if (!hasData) return true;               // binary permission, no filter data → BLOCK
            if (anyMatch) return true;               // permission claims this action → BLOCK
            // No claim → continue to lower priority
        }

        return false;
    }

    // ── Backward-compat wrappers ───────────────────────────────────────────

    /**
     * Block+item combined check (used by InteractionHandler for block events).
     */
    public static boolean checkPermissions(final IslandGroup group,
                                           final List<Permission> perms,
                                           final String itemName,
                                           final String blockName) {
        final Map<String, String> ctx = new java.util.LinkedHashMap<>();
        if (blockName != null && !blockName.isEmpty()) ctx.put("block", blockName);
        if (itemName  != null && !itemName.isEmpty())  ctx.put("item",  itemName);
        return checkPermission(group, perms, ctx);
    }

    /**
     * Single-context match check (used by PermissionEvents for item/entity events).
     */
    public static boolean checkMatchPermission(final IslandGroup group,
                                               final List<Permission> perms,
                                               final String matchValue,
                                               final Function<PermissionDataJson, List<String>> dataExtractor) {
        // Legacy bridge: determine which context the extractor maps to by trying known extractors
        if (group == null) return false;

        for (final Permission perm : perms) {
            if (group.canDo(perm.getId())) continue;

            final List<String> data = dataExtractor.apply(perm.getData());
            if (data.isEmpty()) return true; // binary

            if (PermissionManager.hasExplicitNegation(data, matchValue)) continue;
            if (PermissionManager.checkMatch(data, matchValue) == MatchResult.BLOCK) return true;
        }
        return false;
    }
}
