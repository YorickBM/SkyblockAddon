package yorickbm.skyblockaddon.core.util;

import yorickbm.skyblockaddon.core.util.exceptions.FunctionNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class FunctionRegistry<T> {
    private final Map<UUID, Function<T, Boolean>> functionMap = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void register(final UUID hash, final Function<T, Boolean> function, final int durationMinutes) {
        functionMap.put(hash, function);
        scheduler.schedule(() -> functionMap.remove(hash), durationMinutes, TimeUnit.MINUTES);
    }

    public void execute(final UUID hash, final T executor) {
        final Function<T, Boolean> function = functionMap.get(hash);
        if (function != null) {
            if (function.apply(executor)) functionMap.remove(hash);
        } else {
            throw new FunctionNotFoundException(hash);
        }
    }

    public static String getCommand(final UUID uuid) {
        return "/island registry %s".formatted(uuid.toString());
    }
}
