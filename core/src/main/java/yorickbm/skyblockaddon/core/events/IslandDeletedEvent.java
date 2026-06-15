package yorickbm.skyblockaddon.core.events;

import yorickbm.skyblockaddon.core.islands.Island;

import java.util.UUID;

/** Fired after an island has been removed from the registry and caches. Not cancelable. */
public class IslandDeletedEvent extends IslandEvent {

    private final UUID ownerUUID;

    public IslandDeletedEvent(final Island island, final UUID ownerUUID) {
        super(island);
        this.ownerUUID = ownerUUID;
    }

    public UUID getOwnerUUID() { return ownerUUID; }
}
