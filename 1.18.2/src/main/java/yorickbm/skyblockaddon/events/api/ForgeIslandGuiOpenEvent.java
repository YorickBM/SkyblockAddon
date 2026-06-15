package yorickbm.skyblockaddon.events.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import yorickbm.skyblockaddon.core.events.IslandGuiOpenEvent;
import yorickbm.skyblockaddon.core.islands.Island;

/**
 * Forge-visible wrapper around {@link IslandGuiOpenEvent}.
 * Fired on {@code MinecraftForge.EVENT_BUS} before the built-in GUI is opened.
 * Cancel to suppress the default GUILibrary screen and show your own instead.
 */
@Cancelable
public class ForgeIslandGuiOpenEvent extends Event {

    private final IslandGuiOpenEvent core;
    private final ServerPlayer player;

    public ForgeIslandGuiOpenEvent(final IslandGuiOpenEvent core, final ServerPlayer player) {
        this.core = core;
        this.player = player;
    }

    public Island getIsland()                      { return core.getIsland(); }
    public IslandGuiOpenEvent.GuiType getGuiType() { return core.getGuiType(); }
    public ServerPlayer getPlayer()                { return player; }
}
