package yorickbm.skyblockaddon.commands.op;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.gui.GUIManager;
import yorickbm.skyblockaddon.util.ResourceManager;

public class ConfigReloadCommand {
    public ConfigReloadCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
            .then(Commands.literal("admin")
                .requires(source -> source.getEntity() instanceof ServerPlayer && source.hasPermission(3))
                .then(Commands.literal("reload")
                        .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity()))
                )
            )
        );
    }

    public int execute(CommandSourceStack command, ServerPlayer executor) {
        ResourceManager.commonSetup();
        GUIManager.getInstance().loadAllGUIS(); //Load guis from file

        command.sendSuccess(new TextComponent("Configuration files have been reloaded!").withStyle(ChatFormatting.GREEN), true);

        return Command.SINGLE_SUCCESS;
    }
}
