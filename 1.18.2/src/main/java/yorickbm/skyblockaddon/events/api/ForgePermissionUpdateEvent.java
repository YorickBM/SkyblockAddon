package yorickbm.skyblockaddon.events.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import yorickbm.skyblockaddon.core.events.PermissionUpdateEvent;
import yorickbm.skyblockaddon.core.islands.Island;

import java.util.UUID;

/**
 * Forge-visible wrapper around {@link PermissionUpdateEvent}.
 * Fired on {@code MinecraftForge.EVENT_BUS} before a permission toggle is applied.
 * Cancel to suppress the toggle; mutate {@link #setEnabled} to override the value.
 */
@Cancelable
public class ForgePermissionUpdateEvent extends Event {

    private final PermissionUpdateEvent core;
    private final ServerPlayer player;

    public ForgePermissionUpdateEvent(final PermissionUpdateEvent core, final ServerPlayer player) {
        this.core = core;
        this.player = player;
    }

    public Island getIsland()       { return core.getIsland(); }
    public UUID getGroupId()        { return core.getGroupId(); }
    public String getPermissionId() { return core.getPermissionId(); }
    public boolean isEnabled()      { return core.isEnabled(); }
    public void setEnabled(final boolean enabled) { core.setEnabled(enabled); }

    /** The player who triggered the toggle (may be null for console/API calls). */
    public ServerPlayer getPlayer() { return player; }
}
