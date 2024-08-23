package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.commands.interfaces.OverWorldCommandStack;

public class IslandRequestTeleportCommand extends OverWorldCommandStack {
    public IslandRequestTeleportCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
            .then(Commands.literal("request_tp")
                .requires(source -> source.getEntity() instanceof ServerPlayer)
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity(), EntityArgument.getPlayer(context, "player")))
                )
            )
        );
    }

    public int execute(CommandSourceStack command, ServerPlayer executor, ServerPlayer target) {
        if(super.execute(command, executor) == 0) return Command.SINGLE_SUCCESS;



        return Command.SINGLE_SUCCESS;
    }
}
