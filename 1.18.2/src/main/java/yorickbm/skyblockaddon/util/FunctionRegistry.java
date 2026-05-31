package yorickbm.skyblockaddon.util;

import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.core.util.FunctionRegistry;

import java.util.UUID;
import java.util.function.Function;

public class FunctionRegistry {
    private static final yorickbm.skyblockaddon.core.util.FunctionRegistry<ServerPlayer> REGISTRY =
            new yorickbm.skyblockaddon.core.util.FunctionRegistry<>();

    public static void registerFunction(final UUID hash, final Function<ServerPlayer, Boolean> function, final int duration) {
        REGISTRY.register(hash, function, duration);
    }

    public static void executeFunction(final UUID hash, final ServerPlayer executor) {
        REGISTRY.execute(hash, executor);
    }

    public static String getCommand(final UUID uuid) {
        return yorickbm.skyblockaddon.core.util.FunctionRegistry.getCommand(uuid);
    }
}
