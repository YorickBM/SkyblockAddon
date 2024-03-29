package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import yorickbm.skyblockaddon.configs.SkyblockAddonLanguageConfig;
import yorickbm.skyblockaddon.util.ServerHelper;

public class SpawnCommand {
    public SpawnCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("spawn").executes((command) -> execute(command.getSource())));
        dispatcher.register(Commands.literal("hub").executes((command) -> execute(command.getSource())));
    }

    private int execute(CommandSourceStack command) {
        if(!(command.getEntity() instanceof Player player)) { //Executed by non-player
            command.sendFailure(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.not.player")));
            return Command.SINGLE_SUCCESS;
        }

        if(player.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.not.overworld")));
            return Command.SINGLE_SUCCESS;
        }

        command.sendSuccess(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("commands.spawn.teleport"), ChatFormatting.GREEN), false);
        player.teleportTo(command.getLevel().getSharedSpawnPos().getX(),command.getLevel().getSharedSpawnPos().getY(),command.getLevel().getSharedSpawnPos().getZ());
        return Command.SINGLE_SUCCESS;
    }
}
