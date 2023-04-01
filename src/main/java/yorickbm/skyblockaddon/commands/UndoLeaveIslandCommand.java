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
import yorickbm.skyblockaddon.util.IslandData;
import yorickbm.skyblockaddon.util.LanguageFile;

import java.time.Instant;

public class UndoLeaveIslandCommand {
    public UndoLeaveIslandCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island").then(Commands.literal("undo").executes((command) -> { //.then(Commands.argument("name", MessageArgument.message()))
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
            if(island.getPreviousIsland() == "") {
                command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.undo.hasnone")));
                return;
            }
            if(island.timestamp < (Instant.now().getEpochSecond() - (60*60))) {
                command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.undo.expired")));
                return;
            }

            player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(g -> {
                String previous = island.getPreviousIsland();
                boolean hadPrevious = false;
                
                if (island.hasOne()) {
                    IslandData islandData = g.getIslandById(island.getIslandId());
                    LeaveIslandCommand.leaveIsland(islandData, island, player);
                    hadPrevious = true;
                }

                island.timestamp = 0;

                IslandData islandData = g.getIslandById(previous);
                island.setIsland(previous); //Set old island back
                islandData.teleport(player);

                if (hadPrevious)
                    command.sendSuccess(new TextComponent(LanguageFile.getForKey("commands.island.undo.hasone")).withStyle(ChatFormatting.GREEN), false);
                else
                    command.sendSuccess(new TextComponent(LanguageFile.getForKey("commands.island.undo.success")).withStyle(ChatFormatting.GREEN), false);
            });
        });

        return Command.SINGLE_SUCCESS;
    }
}
