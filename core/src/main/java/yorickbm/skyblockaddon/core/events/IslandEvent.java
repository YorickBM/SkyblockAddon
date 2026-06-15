package yorickbm.skyblockaddon.core.events;

import yorickbm.skyblockaddon.core.islands.Island;

public abstract class IslandEvent {

    private final Island island;
    private boolean cancelled = false;

    protected IslandEvent(final Island island) {
        this.island = island;
    }

    public Island getIsland() { return island; }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(final boolean cancelled) { this.cancelled = cancelled; }
}
