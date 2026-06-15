package yorickbm.skyblockaddon.core.events;

import yorickbm.skyblockaddon.core.islands.Island;

import java.util.UUID;

/**
 * Fired when a member is added to or removed from an island.
 * Not cancelable — it is a notification that the change already happened.
 */
public class IslandMemberUpdateEvent extends IslandEvent {

    public enum Action { ADDED, REMOVED, GROUP_CHANGED }

    private final UUID memberUUID;
    private final Action action;

    public IslandMemberUpdateEvent(final Island island, final UUID memberUUID, final Action action) {
        super(island);
        this.memberUUID = memberUUID;
        this.action = action;
    }

    public UUID getMemberUUID() { return memberUUID; }
    public Action getAction()   { return action; }
}
