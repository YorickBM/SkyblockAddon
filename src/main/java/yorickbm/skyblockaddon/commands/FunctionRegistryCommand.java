package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.util.FunctionRegistry;
import yorickbm.skyblockaddon.util.exceptions.FunctionNotFoundException;

import java.util.UUID;

public class FunctionRegistryCommand {

    public FunctionRegistryCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
            .then(Commands.literal("registry")
                .then(Commands.argument("uuid", UuidArgument.uuid())
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
                        .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity(), UuidArgument.getUuid(context, "uuid")))
                )
            )
        );
    }

    public int execute(CommandSourceStack command, ServerPlayer executor, UUID uuid) {
        try {
            FunctionRegistry.executeFunction(uuid, executor);
        } catch (FunctionNotFoundException e) {
            command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.expired")));
        }

        return Command.SINGLE_SUCCESS;
    }

}
