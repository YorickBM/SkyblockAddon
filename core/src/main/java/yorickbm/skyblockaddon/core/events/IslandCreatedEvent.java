package yorickbm.skyblockaddon.core.events;

import yorickbm.skyblockaddon.core.islands.Island;

import java.util.UUID;

/** Fired after a new island has been registered and cached. Not cancelable. */
public class IslandCreatedEvent extends IslandEvent {

    private final UUID ownerUUID;

    public IslandCreatedEvent(final Island island, final UUID ownerUUID) {
        super(island);
        this.ownerUUID = ownerUUID;
    }

    public UUID getOwnerUUID() { return ownerUUID; }
}
