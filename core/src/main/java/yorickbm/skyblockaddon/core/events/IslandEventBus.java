package yorickbm.skyblockaddon.core.events;

/**
 * Lightweight injectable event bus. The core fires events through this; each
 * version module (Forge 1.18.2, etc.) registers its own bridge implementation
 * that re-fires events on the platform's native bus (MinecraftForge.EVENT_BUS).
 *
 * Default implementation is a no-op so core code never throws when no bridge
 * is registered.
 */
public final class IslandEventBus {

    @FunctionalInterface
    public interface Bus {
        /**
         * Fire an event. Implementations should propagate to the platform bus
         * and sync the cancelled flag back before returning.
         *
         * @return the same event instance, possibly with cancelled/fields updated
         */
        <T extends IslandEvent> T fire(T event);
    }

    private static Bus bus = new Bus() {
        @Override public <T extends IslandEvent> T fire(T event) { return event; }
    };

    /** Called once at startup by the version module to register the platform bridge. */
    public static void register(final Bus implementation) {
        bus = implementation;
    }

    /** Fire an event through the registered bus. Returns the event for fluent cancelled checks. */
    public static <T extends IslandEvent> T fire(final T event) {
        return bus.fire(event);
    }

    private IslandEventBus() {}
}
