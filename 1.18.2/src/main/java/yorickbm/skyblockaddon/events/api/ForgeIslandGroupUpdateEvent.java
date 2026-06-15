package yorickbm.skyblockaddon.events.api;

import net.minecraftforge.eventbus.api.Event;
import yorickbm.skyblockaddon.core.events.IslandGroupUpdateEvent;
import yorickbm.skyblockaddon.core.islands.Island;

import java.util.UUID;

/** Forge-visible wrapper around {@link IslandGroupUpdateEvent}. */
public class ForgeIslandGroupUpdateEvent extends Event {

    private final IslandGroupUpdateEvent core;

    public ForgeIslandGroupUpdateEvent(final IslandGroupUpdateEvent core) {
        this.core = core;
    }

    public Island getIsland()                          { return core.getIsland(); }
    public UUID getGroupId()                           { return core.getGroupId(); }
    public String getGroupName()                       { return core.getGroupName(); }
    public IslandGroupUpdateEvent.Action getAction()   { return core.getAction(); }
}
