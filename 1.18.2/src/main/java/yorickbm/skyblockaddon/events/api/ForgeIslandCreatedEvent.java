package yorickbm.skyblockaddon.events.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;
import yorickbm.skyblockaddon.core.events.IslandCreatedEvent;
import yorickbm.skyblockaddon.core.islands.Island;

import java.util.UUID;

/** Forge-visible wrapper around {@link IslandCreatedEvent}. */
public class ForgeIslandCreatedEvent extends Event {

    private final IslandCreatedEvent core;
    private final ServerPlayer owner;

    public ForgeIslandCreatedEvent(final IslandCreatedEvent core, final ServerPlayer owner) {
        this.core = core;
        this.owner = owner;
    }

    public Island getIsland()    { return core.getIsland(); }
    public UUID getOwnerUUID()   { return core.getOwnerUUID(); }
    /** May be null if the owner is offline at the time of firing. */
    public ServerPlayer getOwner() { return owner; }
}
