package yorickbm.skyblockaddon.commands.op;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.UUID;

public class AdminGetIdCommand {
    public AdminGetIdCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
            .then(Commands.literal("admin")
                .requires(source -> source.getEntity() instanceof ServerPlayer && source.hasPermission(3))
                .then(Commands.literal("getId")
                    .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity(), (ServerPlayer) null))
                    .then(Commands.argument("player", EntityArgument.players())
                        .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity(), EntityArgument.getPlayer(context, "player")))
                    )
                    .then(Commands.argument("uuid", UuidArgument.uuid())
                            .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity(), UuidArgument.getUuid(context, "uuid")))
                    )
                )
            )
        );
    }


    public int execute(CommandSourceStack command, ServerPlayer executor, ServerPlayer target) {

        if(target == null) { //Require over-world if admin is not running a player check
            if (executor.level.dimension() != Level.OVERWORLD) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.not.in.overworld")));
                return Command.SINGLE_SUCCESS;
            }

            command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
                Island island = cap.getIslandPlayerIsStandingOn(executor);
                if (island == null) {
                    command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.no.island")));
                    return;
                }

                command.sendSuccess(
                        new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.island.found.id").formatted(island.getId().toString()))
                                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, island.getId().toString())))
                        , false);
            });

            return Command.SINGLE_SUCCESS;
        }

        execute(command, executor, target.getUUID());

        return Command.SINGLE_SUCCESS;
    }

    public int execute(CommandSourceStack command, ServerPlayer executor, UUID target) {

        command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByEntityUUID(target);
            if (island == null) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.island.not.found.player").formatted(UsernameCache.getBlocking(target))));
                return;
            }

            command.sendSuccess(
                    new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.island.found.id").formatted(island.getId().toString()))
                            .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, island.getId().toString())))
                    , false);
        });

        return Command.SINGLE_SUCCESS;
    }
}
