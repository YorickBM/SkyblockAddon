package yorickbm.skyblockaddon.core.events;

import yorickbm.skyblockaddon.core.islands.Island;
import yorickbm.skyblockaddon.core.islands.IslandGroup;

import java.util.UUID;

/**
 * Fired when a permission group is created or removed on an island.
 * Not cancelable — it is a notification that the change already happened.
 */
public class IslandGroupUpdateEvent extends IslandEvent {

    public enum Action { CREATED, REMOVED }

    private final UUID groupId;
    private final String groupName;
    private final Action action;

    public IslandGroupUpdateEvent(final Island island, final UUID groupId,
                                  final String groupName, final Action action) {
        super(island);
        this.groupId = groupId;
        this.groupName = groupName;
        this.action = action;
    }

    public UUID getGroupId()    { return groupId; }
    public String getGroupName() { return groupName; }
    public Action getAction()   { return action; }
}
