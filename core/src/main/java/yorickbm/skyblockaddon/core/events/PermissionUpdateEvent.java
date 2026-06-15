package yorickbm.skyblockaddon.core.events;

import yorickbm.skyblockaddon.core.islands.Island;

import java.util.UUID;

/**
 * Fired before a permission is toggled for an island group.
 * Cancelable — if cancelled the toggle is not applied.
 * {@code enabled} is mutable so listeners can override the target value.
 */
public class PermissionUpdateEvent extends IslandEvent {

    private final UUID groupId;
    private final String permissionId;
    private boolean enabled;
    /** UUID of the player who triggered the change, or null for API/console calls. */
    private final UUID playerUUID;

    public PermissionUpdateEvent(final Island island, final UUID groupId,
                                 final String permissionId, final boolean enabled,
                                 final UUID playerUUID) {
        super(island);
        this.groupId = groupId;
        this.permissionId = permissionId;
        this.enabled = enabled;
        this.playerUUID = playerUUID;
    }

    public UUID getGroupId()    { return groupId; }
    public UUID getPlayerUUID() { return playerUUID; }
    public String getPermissionId() { return permissionId; }

    public boolean isEnabled() { return enabled; }
    /** Listeners may change the value that will actually be stored. */
    public void setEnabled(final boolean enabled) { this.enabled = enabled; }
}
