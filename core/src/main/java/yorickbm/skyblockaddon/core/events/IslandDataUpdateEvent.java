package yorickbm.skyblockaddon.core.events;

import yorickbm.skyblockaddon.core.islands.Island;

/**
 * Fired after an island data field has been changed.
 * Not cancelable — it is a notification that the change already happened.
 */
public class IslandDataUpdateEvent extends IslandEvent {

    public enum Field {
        BIOME, SPAWNPOINT, VISIBILITY, NAME, SKULL
    }

    private final Field field;
    private final Object oldValue;
    private final Object newValue;

    public IslandDataUpdateEvent(final Island island, final Field field,
                                 final Object oldValue, final Object newValue) {
        super(island);
        this.field = field;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Field getField() { return field; }
    public Object getOldValue() { return oldValue; }
    public Object getNewValue() { return newValue; }
}
