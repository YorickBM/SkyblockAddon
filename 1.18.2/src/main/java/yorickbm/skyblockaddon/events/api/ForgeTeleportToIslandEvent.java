package yorickbm.skyblockaddon.events.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import yorickbm.skyblockaddon.core.events.TeleportToIslandEvent;
import yorickbm.skyblockaddon.core.islands.Island;

/**
 * Forge-visible wrapper around {@link TeleportToIslandEvent}.
 * Fired on {@code MinecraftForge.EVENT_BUS} before a player is teleported to an island.
 * Cancel to suppress the teleport.
 */
@Cancelable
public class ForgeTeleportToIslandEvent extends Event {

    private final TeleportToIslandEvent core;
    private final ServerPlayer player;

    public ForgeTeleportToIslandEvent(final TeleportToIslandEvent core, final ServerPlayer player) {
        this.core = core;
        this.player = player;
    }

    public Island getIsland()       { return core.getIsland(); }
    public ServerPlayer getPlayer() { return player; }
}
