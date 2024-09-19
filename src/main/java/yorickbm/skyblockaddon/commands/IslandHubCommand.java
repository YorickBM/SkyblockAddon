package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.commands.interfaces.OverWorldCommandStack;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.gui.GUIManager;
import yorickbm.skyblockaddon.islands.Island;

public class IslandHubCommand extends OverWorldCommandStack {
    public IslandHubCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hub")
                .requires(source -> source.getEntity() instanceof ServerPlayer)
                .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity()))
        );
    }

    @Override
    public int execute(CommandSourceStack command, ServerPlayer executor) {
        if(super.execute(command, executor) == 0) return Command.SINGLE_SUCCESS;

        executor.teleportTo(command.getLevel().getSharedSpawnPos().getX(), command.getLevel().getSharedSpawnPos().getY(), command.getLevel().getSharedSpawnPos().getZ());

        return Command.SINGLE_SUCCESS;
    }
}
