package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.gui.GUIManager;
import yorickbm.skyblockaddon.islands.Island;

import java.util.UUID;

public class IslandCommand {
    public IslandCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Register the command for OPs
        dispatcher.register(Commands.literal("island")
                .requires(source -> source.getEntity() instanceof ServerPlayer)
                .executes(context -> executeNonOP(context.getSource(), (ServerPlayer) context.getSource().getEntity()))
        );

        // Register the command for non-OPs
        dispatcher.register(Commands.literal("island")
                .then(Commands.literal("admin")
                        .requires(source -> source.getEntity() instanceof ServerPlayer && source.hasPermission(3))
                        .then(Commands.literal("menu")
                                .executes(context -> executeOP(context.getSource(), (ServerPlayer) context.getSource().getEntity(), null))
                                .then(Commands.argument("id", UuidArgument.uuid())
                                        .executes(context -> executeOP(context.getSource(), (ServerPlayer) context.getSource().getEntity(), UuidArgument.getUuid(context, "id")))
                                )
                        )
                )
        );
    }

    private int executeNonOP(CommandSourceStack command, ServerPlayer executor) {
        if (executor.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.not.in.overworld")));
            return Command.SINGLE_SUCCESS;
        }

        command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByEntityUUID(executor);
            if (island == null) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.has.no.island")));
                return;
            }
            GUIManager.getInstance().openMenu("overview", executor, island);
        });
        return Command.SINGLE_SUCCESS;
    }

    private int executeOP(CommandSourceStack command, ServerPlayer executor, UUID islandId) {
        if (executor.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.not.in.overworld")));
            return Command.SINGLE_SUCCESS;
        }

        command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = null;

            if (islandId == null) island = cap.getIslandPlayerIsStandingOn(executor);
            else island = cap.getIslandByUUID(islandId);

            if (island == null) {
                if (islandId == null)
                    command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.no.island")));
                else
                    command.sendFailure(new TextComponent(String.format(SkyBlockAddonLanguage.getLocalizedString("commands.admin.island.not.found"), islandId.toString())));
                return;
            }
            GUIManager.getInstance().openMenu("overview", executor, island);
        });
        return Command.SINGLE_SUCCESS;
    }
}
