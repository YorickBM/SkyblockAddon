package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.commands.interfaces.OverWorldCommandStack;

public class IslandInviteCommand extends OverWorldCommandStack {
    public IslandInviteCommand(CommandDispatcher<CommandSourceStack> dispatcher) {

    }

    @Override
    public int execute(CommandSourceStack command, ServerPlayer executor) {
        if(super.execute(command, executor) == 0) return Command.SINGLE_SUCCESS;

        return Command.SINGLE_SUCCESS;
    }
}
