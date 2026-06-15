package yorickbm.skyblockaddon.core.islands;

import yorickbm.skyblockaddon.core.JSON.PermissionDataJson;
import yorickbm.skyblockaddon.core.permissions.Permission;
import yorickbm.skyblockaddon.core.permissions.PermissionManager;
import yorickbm.skyblockaddon.core.util.MatchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class InteractionValidator {

    /**
     * Collects permission check results for a single debug log line.
     * Passed  = claimed + canDo=true (allowed)
     * Blocked = claimed + canDo=false (blocked)
     * Ignored = matched trigger but filtered out by context / negation
     */
    public static class PermissionDebug {
        public final List<String> passed  = new ArrayList<>();
        public final List<String> blocked = new ArrayList<>();
        public final List<String> ignored = new ArrayList<>();

        public void log(final java.util.function.Consumer<String> logger, final String header) {
            logger.accept(header);
            logger.accept("  Passed permissions:             " + fmt(passed));
            logger.accept("  Blocked permissions:            " + fmt(blocked));
            logger.accept("  Ignored permissions on trigger: " + fmt(ignored));
        }

        private static String fmt(final List<String> list) {
            return list.isEmpty() ? "-" : String.join(", ", list);
        }
    }

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
        return checkPermission(group, perms, contextValues, null);
    }

    public static boolean checkPermission(final IslandGroup group,
                                          final List<Permission> perms,
                                          final Map<String, String> contextValues,
                                          final PermissionDebug debug) {
        if (group == null) return false;

        for (final Permission perm : perms) {
            boolean skipThisPerm = false;
            boolean anyMatch = false;
            boolean hasData = false;

            for (final Map.Entry<String, String> ctx : contextValues.entrySet()) {
                final List<String> patterns = perm.getData().getFiltersForContext(ctx.getKey());
                if (patterns == null) continue;
                hasData = true;

                if (patterns.isEmpty()) {
                    anyMatch = true;
                    continue;
                }

                if (PermissionManager.hasExplicitNegation(patterns, ctx.getValue())) {
                    skipThisPerm = true;
                    break;
                }

                if (PermissionManager.checkMatch(patterns, ctx.getValue()) == MatchResult.BLOCK) {
                    anyMatch = true;
                }
            }

            if (skipThisPerm) {
                if (debug != null) debug.ignored.add(perm.getId());
                continue;
            }

            // If the permission declares a context key that is absent from the provided
            // contextValues (e.g. interact_spawn requires "item" but player has empty hand),
            // treat it as hasData=true with no match — the permission requires that context
            // to be present, so it should not claim an action where it is absent.
            if (!hasData || !anyMatch) {
                for (final String declaredCtx : perm.getData().getContextKeys()) {
                    if (!contextValues.containsKey(declaredCtx)) {
                        hasData = true; // permission requires this context but it's absent
                    }
                }
            }

            final boolean claims = !hasData || anyMatch;
            if (!claims) {
                if (debug != null) debug.ignored.add(perm.getId());
                continue;
            }

            final boolean isBlocked = !group.canDo(perm.getId());
            if (debug != null) {
                if (isBlocked) debug.blocked.add(perm.getId());
                else           debug.passed.add(perm.getId());
            }
            return isBlocked;
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
        return checkMatchPermission(group, perms, matchValue, dataExtractor, null);
    }

    public static boolean checkMatchPermission(final IslandGroup group,
                                               final List<Permission> perms,
                                               final String matchValue,
                                               final Function<PermissionDataJson, List<String>> dataExtractor,
                                               final PermissionDebug debug) {
        if (group == null) return false;

        for (final Permission perm : perms) {
            final List<String> data = dataExtractor.apply(perm.getData());

            if (PermissionManager.hasExplicitNegation(data, matchValue)) {
                if (debug != null) debug.ignored.add(perm.getId());
                continue;
            }

            final boolean claims = data.isEmpty()
                    || PermissionManager.checkMatch(data, matchValue) == MatchResult.BLOCK;
            if (!claims) {
                if (debug != null) debug.ignored.add(perm.getId());
                continue;
            }

            final boolean isBlocked = !group.canDo(perm.getId());
            if (debug != null) {
                if (isBlocked) debug.blocked.add(perm.getId());
                else           debug.passed.add(perm.getId());
            }
            return isBlocked;
        }

        return false;
    }
}
