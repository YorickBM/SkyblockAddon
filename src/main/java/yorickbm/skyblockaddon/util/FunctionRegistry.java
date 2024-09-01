package yorickbm.skyblockaddon.util;

import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.util.exceptions.FunctionNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class FunctionRegistry {
    private static final Map<UUID, Function<ServerPlayer, Boolean>> functionMap = new HashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    // Method to register a function with a unique hash
    public static void registerFunction(UUID hash, Function<ServerPlayer, Boolean> function, int duration) {
        functionMap.put(hash, function);

        // Schedule the removal of the hash after the specified duration
        scheduler.schedule(() -> {
            functionMap.remove(hash);
        }, duration, TimeUnit.MINUTES);
    }

    // Method to execute a function based on the hash
    public static void executeFunction(UUID hash, ServerPlayer executor) {
        Function<ServerPlayer, Boolean> function = functionMap.get(hash);
        if (function != null) {
            if(function.apply(executor)) functionMap.remove(hash);
        } else {
            throw new FunctionNotFoundException(hash);
        }
    }

    public static String getCommand(UUID uuid) {
        return "/island registry %s".formatted(uuid.toString());
    }
}
