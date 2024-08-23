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
import net.minecraft.world.level.Level;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.util.FunctionRegistry;

import java.util.UUID;

public class IslandTeleportCommand {
    public IslandTeleportCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
                .then(Commands.literal("tp")
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
                        .executes(context -> executePersonal(context.getSource(), (ServerPlayer) context.getSource().getEntity()))
                        .then(Commands.argument("player", EntityArgument.players())
                                .executes(context -> executeRequest(context.getSource(), (ServerPlayer) context.getSource().getEntity(), EntityArgument.getPlayer(context, "player")))
                        )
                )
        );
    }

    private int executePersonal(CommandSourceStack command, ServerPlayer executor) {
        if (executor.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.not.in.overworld")));
            return Command.SINGLE_SUCCESS;
        }

        command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByEntityUUID(executor.getUUID());
            if (island == null) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.has.no.island")));
                return;
            }

            command.sendSuccess(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.teleporting.own")).withStyle(ChatFormatting.GREEN), false);
            island.teleportTo(executor);
        });
        return Command.SINGLE_SUCCESS;
    }

    private int executeRequest(CommandSourceStack command, ServerPlayer executor, ServerPlayer requested) {
        if (executor.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.not.in.overworld")));
            return Command.SINGLE_SUCCESS;
        }

        command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByEntityUUID(requested.getUUID());
            if (island == null) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.has.no.island")));
                return;
            }

            if(executor.hasPermissions(3)) { //If player is OP skip request
                command.sendSuccess(new TextComponent(
                        SkyBlockAddonLanguage.getLocalizedString("commands.teleporting.other")
                                .formatted(requested.getDisplayName().getString()))
                        .withStyle(ChatFormatting.GREEN), false);
                island.teleportTo(executor);
                return;
            }

            UUID functionKey = UUID.randomUUID();
            FunctionRegistry.registerFunction(functionKey, (e) -> {
                command.sendSuccess(new TextComponent(
                        SkyBlockAddonLanguage.getLocalizedString("commands.teleporting.other")
                                .formatted(requested.getDisplayName().getString()))
                        .withStyle(ChatFormatting.GREEN), false);
                requested.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.teleporting.accepted").formatted(executor.getDisplayName().getString())), requested.getUUID());
                island.teleportTo(executor);
            }, 5); //Register function to teleport under hash 'functionKey' for 5 minutes.
            requested.sendMessage(new TextComponent(
                    SkyBlockAddonLanguage.getLocalizedString("commands.teleporting.request")
                            .formatted(executor.getDisplayName().getString()))
                    .withStyle(ChatFormatting.GREEN)
                    .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island registry %s".formatted(functionKey.toString()))))
                    , requested.getUUID());

        });
        return Command.SINGLE_SUCCESS;
    }
}
