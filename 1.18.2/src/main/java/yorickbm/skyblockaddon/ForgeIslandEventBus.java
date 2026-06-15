package yorickbm.skyblockaddon;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import yorickbm.skyblockaddon.core.events.*;
import yorickbm.skyblockaddon.events.api.*;

/**
 * Bridges the core {@link IslandEventBus} to {@code MinecraftForge.EVENT_BUS}.
 *
 * When core code fires an {@link IslandEvent}, this bus:
 *   1. Wraps it in the matching Forge event (which carries ServerPlayer etc.)
 *   2. Posts on MinecraftForge.EVENT_BUS so third-party mods can subscribe
 *   3. Syncs the cancelled flag (and any mutable fields) back to the core event
 *
 * Register once during FMLCommonSetupEvent via {@link IslandEventBus#register}.
 */
public class ForgeIslandEventBus implements IslandEventBus.Bus {

    private final MinecraftServer server;

    public ForgeIslandEventBus(final MinecraftServer server) {
        this.server = server;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IslandEvent> T fire(final T event) {
        final Event forgeEvent = wrap(event);
        if (forgeEvent == null) return event;

        MinecraftForge.EVENT_BUS.post(forgeEvent);

        if (forgeEvent.isCanceled()) event.setCancelled(true);

        return event;
    }

    private Event wrap(final IslandEvent event) {
        if (event instanceof final PermissionUpdateEvent e) {
            final ServerPlayer player = e.getPlayerUUID() != null
                    ? server.getPlayerList().getPlayer(e.getPlayerUUID()) : null;
            return new ForgePermissionUpdateEvent(e, player);
        }
        if (event instanceof final TeleportToIslandEvent e) {
            final ServerPlayer player = server.getPlayerList().getPlayer(e.getPlayerUUID());
            return player != null ? new ForgeTeleportToIslandEvent(e, player) : null;
        }
        if (event instanceof final IslandGuiOpenEvent e) {
            final ServerPlayer player = server.getPlayerList().getPlayer(e.getPlayerUUID());
            return player != null ? new ForgeIslandGuiOpenEvent(e, player) : null;
        }
        if (event instanceof final IslandDataUpdateEvent e) {
            return new ForgeIslandDataUpdateEvent(e);
        }
        if (event instanceof final IslandMemberUpdateEvent e) {
            return new ForgeIslandMemberUpdateEvent(e);
        }
        if (event instanceof final IslandGroupUpdateEvent e) {
            return new ForgeIslandGroupUpdateEvent(e);
        }
        if (event instanceof final IslandCreatedEvent e) {
            final ServerPlayer player = server.getPlayerList().getPlayer(e.getOwnerUUID());
            return new ForgeIslandCreatedEvent(e, player);
        }
        if (event instanceof final IslandDeletedEvent e) {
            final ServerPlayer player = server.getPlayerList().getPlayer(e.getOwnerUUID());
            return new ForgeIslandDeletedEvent(e, player);
        }
        return null;
    }
}
