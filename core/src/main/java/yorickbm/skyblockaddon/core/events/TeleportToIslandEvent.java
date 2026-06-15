package yorickbm.skyblockaddon.core.events;

import yorickbm.skyblockaddon.core.islands.Island;

import java.util.UUID;

/**
 * Fired before a player is teleported to an island spawn.
 * Cancelable — if cancelled the teleport does not happen.
 */
public class TeleportToIslandEvent extends IslandEvent {

    private final UUID playerUUID;

    public TeleportToIslandEvent(final Island island, final UUID playerUUID) {
        super(island);
        this.playerUUID = playerUUID;
    }

    public UUID getPlayerUUID() { return playerUUID; }
}
