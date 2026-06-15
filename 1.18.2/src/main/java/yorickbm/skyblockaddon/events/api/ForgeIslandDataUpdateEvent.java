package yorickbm.skyblockaddon.events.api;

import net.minecraftforge.eventbus.api.Event;
import yorickbm.skyblockaddon.core.events.IslandDataUpdateEvent;
import yorickbm.skyblockaddon.core.islands.Island;

/**
 * Forge-visible wrapper around {@link IslandDataUpdateEvent}.
 * Fired on {@code MinecraftForge.EVENT_BUS} after an island data field has changed.
 * Not cancelable — use this for cache invalidation, GUI refreshes, etc.
 */
public class ForgeIslandDataUpdateEvent extends Event {

    private final IslandDataUpdateEvent core;

    public ForgeIslandDataUpdateEvent(final IslandDataUpdateEvent core) {
        this.core = core;
    }

    public Island getIsland()                      { return core.getIsland(); }
    public IslandDataUpdateEvent.Field getField()  { return core.getField(); }
    public Object getOldValue()                    { return core.getOldValue(); }
    public Object getNewValue()                    { return core.getNewValue(); }
}
