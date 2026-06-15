package yorickbm.skyblockaddon.events.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;
import yorickbm.skyblockaddon.core.events.IslandDeletedEvent;
import yorickbm.skyblockaddon.core.islands.Island;

import java.util.UUID;

/** Forge-visible wrapper around {@link IslandDeletedEvent}. */
public class ForgeIslandDeletedEvent extends Event {

    private final IslandDeletedEvent core;
    private final ServerPlayer owner;

    public ForgeIslandDeletedEvent(final IslandDeletedEvent core, final ServerPlayer owner) {
        this.core = core;
        this.owner = owner;
    }

    public Island getIsland()    { return core.getIsland(); }
    public UUID getOwnerUUID()   { return core.getOwnerUUID(); }
    /** May be null if the owner is offline at the time of firing. */
    public ServerPlayer getOwner() { return owner; }
}
