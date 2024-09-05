package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.commands.interfaces.OverWorldCommandStack;
import yorickbm.skyblockaddon.gui.GUIManager;

public class IslandTravelCommand extends OverWorldCommandStack {
    public IslandTravelCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
                .then(Commands.literal("travel")
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
                        .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity()))
                )
        );
    }

    @Override
    public int execute(CommandSourceStack command, ServerPlayer executor) {
        if(super.execute(command, executor) == 0) return Command.SINGLE_SUCCESS;

        GUIManager.getInstance().openMenu("travel", executor, null, new CompoundTag());
        return Command.SINGLE_SUCCESS;
    }
}
