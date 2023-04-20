package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import yorickbm.skyblockaddon.capabilities.Providers.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.Providers.PlayerIslandProvider;
import yorickbm.skyblockaddon.util.LanguageFile;

import java.util.Collection;

public class AcceptIslandCommand {
    public AcceptIslandCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island").then(Commands.literal("accept").then(Commands.argument("targets", EntityArgument.players()).executes((command) -> { //.then(Commands.argument("name", MessageArgument.message()))
            return execute(command.getSource(), EntityArgument.getPlayers(command, "targets")); //, MessageArgument.getMessage(command, "name")
        }))));
    }

    private int execute(CommandSourceStack command, Collection<ServerPlayer> targets) { //, Component islandName

        if(!(command.getEntity() instanceof Player player)) { //Executed by non-player
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.nonplayer")));
            return Command.SINGLE_SUCCESS;
        }

        if(player.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.notoverworld")));
            return Command.SINGLE_SUCCESS;
        }

        if(!targets.stream().findFirst().isPresent()) {
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.teleport.user.offline")));
            return Command.SINGLE_SUCCESS;
        }

        Player requester = targets.stream().findFirst().get();
        player.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(island -> {
            if(island.teleportValid(requester.getUUID())) {
                if(requester.getLevel().dimension() != Level.OVERWORLD) {
                    command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.teleport.user.request.notoverworld").formatted(requester.getGameProfile().getName())));
                    return;
                }

                //TODO Invite accept request permission

                command.sendSuccess(new TextComponent(LanguageFile.getForKey("commands.island.teleport.user.request.success").formatted(requester.getGameProfile().getName())).withStyle(ChatFormatting.GREEN), false);
                requester.sendMessage(new TextComponent(LanguageFile.getForKey("commands.island.teleport.user.success").formatted(player.getGameProfile().getName())).withStyle(ChatFormatting.GREEN), requester.getUUID());

                player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(g -> g.getIslandById(island.getIslandId()).teleport(requester));
            } else {
                command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.teleport.user.expired")));
            }
        });

        return Command.SINGLE_SUCCESS;
    }
}
