package yorickbm.skyblockaddon.core.islands;

import yorickbm.skyblockaddon.core.JSON.PermissionDataJson;
import yorickbm.skyblockaddon.core.permissions.Permission;
import yorickbm.skyblockaddon.core.permissions.PermissionManager;
import yorickbm.skyblockaddon.core.util.MatchResult;

import java.util.List;
import java.util.function.Function;

public class InteractionValidator {

    /**
     * Check whether a player interaction should be blocked based on item and block names.
     * Used by InteractionHandler for right/left-click block and item events.
     *
     * @param group     - The island group the player belongs to
     * @param perms     - Permissions relevant to this trigger
     * @param itemName  - Registry name of the item in hand, empty string if none
     * @param blockName - Registry name of the clicked block, empty string if air
     * @return true if the interaction should be blocked
     */
    public static boolean checkPermissions(final IslandGroup group, final List<Permission> perms,
                                           final String itemName, final String blockName) {
        if (perms.isEmpty()) return false;

        boolean runFail = false;
        for (final Permission perm : perms) {
            if (group.canDo(perm.getId())) continue;
            if (runFail) break;

            final List<String> itemsData = perm.getData().getItemsData();
            final List<String> blocksData = perm.getData().getBlocksData();

            if (itemsData.isEmpty() && blocksData.isEmpty()) {
                runFail = true;
                break;
            }

            boolean itemAllowed = true;
            boolean blockAllowed = true;

            if (!itemName.isEmpty() && !itemsData.isEmpty()) {
                if (PermissionManager.checkMatch(itemsData, itemName) == MatchResult.BLOCK) itemAllowed = false;
            }

            if (!blockName.isEmpty() && !blocksData.isEmpty()) {
                if (PermissionManager.checkMatch(blocksData, blockName) == MatchResult.BLOCK) blockAllowed = false;
            }

            runFail = !blockAllowed || !itemAllowed;
        }

        return runFail;
    }

    /**
     * Check a match-based permission against a single value extracted from permission data.
     * Used for triggers like onPickup (items), onAttack (entities), onPlayerChangedDimension, etc.
     *
     * @param group         - The island group the player belongs to
     * @param perms         - Permissions relevant to this trigger
     * @param matchValue    - The registry name or identifier to match against
     * @param dataExtractor - Function to extract the relevant list from PermissionDataJson
     * @return true if the action should be blocked
     */
    public static boolean checkMatchPermission(final IslandGroup group, final List<Permission> perms,
                                               final String matchValue,
                                               final Function<PermissionDataJson, List<String>> dataExtractor) {
        if (perms.isEmpty()) return false;
        if (group == null) return false;

        boolean runFail = false;
        for (final Permission perm : perms) {
            if (group.canDo(perm.getId())) continue;
            if (runFail) break;

            final List<String> data = dataExtractor.apply(perm.getData());
            if (data.isEmpty()) {
                runFail = true;
            } else {
                if (PermissionManager.checkMatch(data, matchValue) == MatchResult.BLOCK) runFail = true;
            }
        }
        return runFail;
    }
}
