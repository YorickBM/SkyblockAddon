package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.commands.interfaces.OverWorldCommandStack;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.util.FunctionRegistry;

import java.util.UUID;

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

        if(executor.getUUID().equals(target.getUUID())) {
            command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.request.self")));
            return Command.SINGLE_SUCCESS;
        }

        command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByEntityUUID(executor.getUUID());
            if (island == null) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.has.no.island")));
                return;
            }

            //Register teleport function into registry
            UUID functionKey = UUID.randomUUID();
            FunctionRegistry.registerFunction(functionKey, (e) -> {
                if(super.execute(command, e) == 0) return;

                e.sendMessage(new TextComponent(
                    SkyBlockAddonLanguage.getLocalizedString("commands.request.other")
                        .formatted(executor.getDisplayName().getString()))
                    .withStyle(ChatFormatting.GREEN), e.getUUID());
                executor.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.request.accepted").formatted(target.getDisplayName().getString())).withStyle(ChatFormatting.GREEN), executor.getUUID());
                island.teleportTo(e);
            }, 5); //Register function to teleport under hash 'functionKey' for 5 minutes.

            //Sending clickable message to accept request to requested
            command.sendSuccess(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.request.requested").formatted(target.getDisplayName().getString())).withStyle(ChatFormatting.GREEN), false);
            target.sendMessage(new TextComponent(
                    SkyBlockAddonLanguage.getLocalizedString("commands.request.request")
                        .formatted(executor.getDisplayName().getString()))
                    .withStyle(ChatFormatting.GREEN)
                    .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, FunctionRegistry.getCommand(functionKey))))
                , target.getUUID());

        });

        return Command.SINGLE_SUCCESS;
    }
}
