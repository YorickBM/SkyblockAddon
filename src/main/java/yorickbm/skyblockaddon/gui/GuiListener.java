package yorickbm.skyblockaddon.gui;

import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.util.BiConsumer;

import java.util.HashMap;
import java.util.Map;

public class GuiListener {

    private final Map<Integer, BiConsumer<Player, Integer>> listeners = new HashMap<>();

    /**
     * Adds a listener for a specific event.
     *
     * @param eventId The ID of the event.
     * @param callback The callback to be executed when the event is triggered.
     */
    public void addListener(int eventId, BiConsumer<Player, Integer> callback) {
        listeners.put(eventId, callback);
    }

    /**
     * Triggers an event, executing the callback associated with the event ID.
     *
     * @param eventId The ID of the event to trigger.
     */
    public void trigger(int eventId, Player player, int clickType) {
        BiConsumer<Player, Integer> callback = listeners.get(eventId);
        if (callback == null) return;
        callback.accept(player, clickType);
    }

}
