package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class IslandLeaveCommand {

    public IslandLeaveCommand(CommandDispatcher<CommandSourceStack> dispatcher) {

    }

    public int execute(CommandSourceStack command, ServerPlayer executor) {
        return Command.SINGLE_SUCCESS;
    }

}
