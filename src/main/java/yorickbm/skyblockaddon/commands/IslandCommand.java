package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class IslandCommand {
    public IslandCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Register the command for OPs
        dispatcher.register(Commands.literal("island")
                .requires(source -> source.getEntity() instanceof ServerPlayer
                && source.hasPermission(3))
                .executes(context -> executeOP(context.getSource()))
        );

        // Register the command for non-OPs
        dispatcher.register(Commands.literal("island")
                .requires(source -> source.getEntity() instanceof ServerPlayer
                && !source.hasPermission(3))
                .executes(context -> executeNonOP(context.getSource()))
        );
    }

    private int executeNonOP(CommandSourceStack command) {
        return Command.SINGLE_SUCCESS;
    }

    private int executeOP(CommandSourceStack command) {
        return Command.SINGLE_SUCCESS;
    }
}
