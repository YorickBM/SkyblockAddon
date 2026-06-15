package yorickbm.skyblockaddon.events.api;

import net.minecraftforge.eventbus.api.Event;
import yorickbm.skyblockaddon.core.events.IslandMemberUpdateEvent;
import yorickbm.skyblockaddon.core.islands.Island;

import java.util.UUID;

/** Forge-visible wrapper around {@link IslandMemberUpdateEvent}. */
public class ForgeIslandMemberUpdateEvent extends Event {

    private final IslandMemberUpdateEvent core;

    public ForgeIslandMemberUpdateEvent(final IslandMemberUpdateEvent core) {
        this.core = core;
    }

    public Island getIsland()                          { return core.getIsland(); }
    public UUID getMemberUUID()                        { return core.getMemberUUID(); }
    public IslandMemberUpdateEvent.Action getAction()  { return core.getAction(); }
}
