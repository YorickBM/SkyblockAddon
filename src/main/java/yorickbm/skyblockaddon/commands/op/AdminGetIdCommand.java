package yorickbm.skyblockaddon.commands.op;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.commands.interfaces.OverWorldCommandStack;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.UUID;

public class AdminGetIdCommand extends OverWorldCommandStack {
    public AdminGetIdCommand(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
            .then(Commands.literal("admin")
                .requires(source -> source.getEntity() instanceof ServerPlayer && source.hasPermission(Commands.LEVEL_MODERATORS))
                .then(Commands.literal("getId")
                    .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity(), (ServerPlayer) null))
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity(), EntityArgument.getPlayer(context, "player")))
                    )
                    .then(Commands.argument("uuid", UuidArgument.uuid())
                            .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity(), UuidArgument.getUuid(context, "uuid")))
                    )
                )
            )
        );
    }

    public int execute(final CommandSourceStack command, final ServerPlayer executor, final ServerPlayer target) {

        if(target == null) { //Require over-world if admin is not running a player check
            if(super.execute(command, executor) == 0) return Command.SINGLE_SUCCESS;

            command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
                final Island island = cap.getIslandPlayerIsStandingOn(executor);
                if (island == null) {
                    command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.no.island")));
                    return;
                }

                command.sendSuccess(
                        new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.island.found.id").formatted(island.getId().toString()))
                                .withStyle(ChatFormatting.GREEN)
                                .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, island.getId().toString())))
                        , false);
            });

            return Command.SINGLE_SUCCESS;
        }

        execute(command, executor, target.getUUID());

        return Command.SINGLE_SUCCESS;
    }

    public int execute(final CommandSourceStack command, final ServerPlayer executor, final UUID target) {

        command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            final Island island = cap.getIslandByEntityUUID(target);
            if (island == null) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.island.not.found.player").formatted(UsernameCache.getBlocking(target))));
                return;
            }

            command.sendSuccess(
                    new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.island.found.id").formatted(island.getId().toString()))
                            .withStyle(ChatFormatting.GREEN)
                            .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, island.getId().toString())))
                    , false);
        });

        return Command.SINGLE_SUCCESS;
    }
}
