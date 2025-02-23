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
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.UUID;

public class IslandInviteCommand extends OverWorldCommandStack {
    public IslandInviteCommand(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
            .then(Commands.literal("invite")
                .then(Commands.argument("player", EntityArgument.player())
                    .requires(source -> source.getEntity() instanceof ServerPlayer)
                        .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity(), EntityArgument.getPlayer(context, "player")))
                )
            )
        );
    }

    public int execute(final CommandSourceStack command, final ServerPlayer executor, final ServerPlayer target) {
        if(super.execute(command, executor) == 0) return Command.SINGLE_SUCCESS;

        if(target.getUUID().equals(executor.getUUID())) {
            command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.invite.self")));
            return Command.SINGLE_SUCCESS;
        }

        command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            final Island sourceIsland = cap.getIslandByEntityUUID(executor.getUUID());
            if (sourceIsland == null) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.has.no.island")));
                return;
            }

            final UUID functionKey = UUID.randomUUID();
            FunctionRegistry.registerFunction(functionKey, (e) -> {
                if(super.execute(command, e) == 0) return true;

                final Island targetIsland = cap.getIslandByEntityUUID(target.getUUID());
                if(targetIsland != null) {
                    targetIsland.kickMember(target, target.getUUID()); //Kick himself off the island.
                }

                if(sourceIsland.addMember(executor, target.getUUID())) {
                    target.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.invite.failure").formatted(executor.getDisplayName().getString(), sourceIsland.getOwner())).withStyle(ChatFormatting.RED), target.getUUID());
                    return true;
                }

                executor.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.invite.accepted").formatted(target.getDisplayName().getString())).withStyle(ChatFormatting.GREEN), executor.getUUID());
                target.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.invite.teleporting")).withStyle(ChatFormatting.GREEN), target.getUUID());
                return true;
            }, 5);

            command.sendSuccess(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.invite.success").formatted(target.getDisplayName().getString())).withStyle(ChatFormatting.GREEN), false);
            target.sendMessage(new TextComponent(
                            SkyBlockAddonLanguage.getLocalizedString("commands.invite.invited")
                                    .formatted(UsernameCache.getBlocking(sourceIsland.getOwner()), executor.getDisplayName().getString()))
                            .withStyle(ChatFormatting.GREEN)
                            .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, FunctionRegistry.getCommand(functionKey))))
                    , target.getUUID());
        });

        return Command.SINGLE_SUCCESS;
    }
}
