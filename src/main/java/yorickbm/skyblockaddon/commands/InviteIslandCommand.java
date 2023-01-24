package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import yorickbm.skyblockaddon.capabilities.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.PlayerIslandProvider;
import yorickbm.skyblockaddon.util.LanguageFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public class InviteIslandCommand {
    public InviteIslandCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island").then(Commands.literal("invite").then(Commands.argument("targets", EntityArgument.players()).executes((command) -> { //.then(Commands.argument("name", MessageArgument.message()))
            return execute(command.getSource(), EntityArgument.getPlayers(command, "targets")); //, MessageArgument.getMessage(command, "name")
        }))));
    }

    private int execute(CommandSourceStack command, Collection<ServerPlayer> targets) { //, Component islandName

        if(!(command.getEntity() instanceof Player)) { //Executed by non-player
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.nonplayer")));
            return Command.SINGLE_SUCCESS;
        }
        Player player = (Player)command.getEntity();

        if(player.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.notoverworld")));
            return Command.SINGLE_SUCCESS;
        }

        player.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(island -> {
            if(!island.hasOne()) {
                command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.invite.hasnone")));
                return;
            }

            Optional<ServerPlayer> p = targets.stream().findFirst();
            if(p.isPresent()) {
                p.get().getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> {
                    if(i.hasOne()) {
                        command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.invite.hasone")));
                        return;
                    }

                    Style style = new TextComponent(LanguageFile.getForKey("commands.island.invite.invitation").formatted(player.getGameProfile().getName())).withStyle(ChatFormatting.GREEN).getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island accept")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Click to accept invite!")));

                    p.get().sendMessage(new TextComponent(LanguageFile.getForKey("commands.island.invite.invitation").formatted(player.getGameProfile().getName())).withStyle(style), p.get().getUUID());
                    player.sendMessage(new TextComponent(LanguageFile.getForKey("commands.island.invite.success").formatted(p.get().getGameProfile().getName())).withStyle(ChatFormatting.GREEN), player.getUUID());
                    i.sendInvite(player.getUUID());
                });
            }

        });

        return Command.SINGLE_SUCCESS;
    }
}
