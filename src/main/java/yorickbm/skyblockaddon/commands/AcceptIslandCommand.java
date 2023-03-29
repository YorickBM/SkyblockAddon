package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import yorickbm.skyblockaddon.capabilities.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.PlayerIslandProvider;
import yorickbm.skyblockaddon.util.LanguageFile;

public class AcceptIslandCommand {
    public AcceptIslandCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island").then(Commands.literal("accept").executes((command) -> { //.then(Commands.argument("name", MessageArgument.message()))
            return execute(command.getSource()); //, MessageArgument.getMessage(command, "name")
        })));
    }

    private int execute(CommandSourceStack command) { //, Component islandName

        if(!(command.getEntity() instanceof Player player)) { //Executed by non-player
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.nonplayer")));
            return Command.SINGLE_SUCCESS;
        }

        if(player.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.notoverworld")));
            return Command.SINGLE_SUCCESS;
        }

        player.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(island -> {
            if(island.requestType == 1) {
                island.requestType = -1;

                if (island.hasOne()) {
                    command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.accept.hasone")));
                    return;
                }
                Player p = command.getLevel().getPlayerByUUID(island.request);

                p.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(g -> {
                    island.setIsland(i.getIslandId());
                    g.getIslandById(i.getIslandId()).addIslandMember(player.getUUID());
                    g.getIslandById(i.getIslandId()).teleport(player);
                    command.sendSuccess(new TextComponent(LanguageFile.getForKey("commands.island.accept.success").formatted(p.getGameProfile().getName())).withStyle(ChatFormatting.GREEN), false);
                }));
            }
            if(island.requestType == 0) {
                island.requestType = -1;

                Player p = command.getLevel().getPlayerByUUID(island.request);
                if(p.getLevel().dimension() != Level.OVERWORLD) {
                    command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.teleport.user.request.notoverworld").formatted(p.getGameProfile().getName())));
                    return;
                }

                player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(g -> g.getIslandById(island.getIslandId()).teleport(p));

                command.sendSuccess(new TextComponent(LanguageFile.getForKey("commands.island.teleport.user.request.success").formatted(p.getGameProfile().getName())).withStyle(ChatFormatting.GREEN), false);
                p.sendMessage(new TextComponent(LanguageFile.getForKey("commands.island.teleport.user.success").formatted(player.getGameProfile().getName())).withStyle(ChatFormatting.GREEN), p.getUUID());
            }
        });

        return Command.SINGLE_SUCCESS;
    }
}
