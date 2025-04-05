package yorickbm.skyblockaddon.commands.op;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.configs.SkyblockAddonConfig;

public class DebugCommand {
    public DebugCommand(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
                .then(Commands.literal("admin")
                        .requires(source -> source.getEntity() instanceof ServerPlayer && source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.literal("debug")
                                .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity()))
                        )
                )
        );
    }

    public int execute(final CommandSourceStack command, final ServerPlayer executor) {
        final String def = SkyblockAddonConfig.getForKey("permissions.debug");
        SkyblockAddonConfig.setValueForKey("permissions.debug", def.equalsIgnoreCase("TRUE") ? "FALSE" : "TRUE");

        command.sendSuccess(new TextComponent("Debug messages have been " + (def.equalsIgnoreCase("TRUE") ? "disabled" : "enabled") + " for permissions in console.").withStyle(ChatFormatting.GREEN), true);
        return Command.SINGLE_SUCCESS;
    }
}
