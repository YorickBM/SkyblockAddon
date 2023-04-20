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
import java.util.Optional;

public class KickIslandCommand {
    public KickIslandCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island").then(Commands.literal("kick").then(Commands.argument("targets", EntityArgument.players()).executes((command) -> { //.then(Commands.argument("name", MessageArgument.message()))
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

        player.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(island -> {
            if(!island.hasOne()) {
                command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.kick.hasnone")));
                return;
            }

            Optional<ServerPlayer> p = targets.stream().findFirst();
            p.ifPresent(serverPlayer -> serverPlayer.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> {
                if (!i.hasOne() || !i.getIslandId().equals(island.getIslandId())) {
                    command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.kick.notpart")));
                    return;
                }

                player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(g -> {
                    if (g.getIslandById(island.getIslandId()).isOwner(serverPlayer.getUUID())) {
                        command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.kick.notallowed")));
                        return;
                    }

                    i.setIsland("");
                    g.getIslandById(island.getIslandId()).removeIslandMember(serverPlayer.getUUID());

                    serverPlayer.sendMessage(new TextComponent(LanguageFile.getForKey("commands.island.kick.kicked").formatted(player.getGameProfile().getName())).withStyle(ChatFormatting.GREEN), player.getUUID());
                    player.sendMessage(new TextComponent(LanguageFile.getForKey("commands.island.kick.success").formatted(serverPlayer.getGameProfile().getName())).withStyle(ChatFormatting.GREEN), player.getUUID());

                });
            }));

        });

        return Command.SINGLE_SUCCESS;
    }
}
