package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import yorickbm.skyblockaddon.capabilities.providers.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.providers.PlayerIslandProvider;
import yorickbm.skyblockaddon.gui.island.IslandOverviewHandler;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.util.LanguageFile;

public class IslandCommand {
    public IslandCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island").executes((command) -> { //.then(Commands.argument("name", MessageArgument.message()))
            return execute(command.getSource()); //, MessageArgument.getMessage(command, "name")
        }));
    }

    private int execute(CommandSourceStack command) {
        if(!(command.getEntity() instanceof Player player)) { //Executed by non-player
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.nonplayer")));
            return Command.SINGLE_SUCCESS;
        }

        if(player.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.notoverworld")));
            return Command.SINGLE_SUCCESS;
        }

        player.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(island -> {
            if (!island.hasOne()) {
                command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.hasnone")));
                return;
            }

            player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(generator -> {
                IslandData islandData = generator.getIslandById(island.getIslandId());
                IslandOverviewHandler.openMenu(player, islandData);
            });
        });

        return Command.SINGLE_SUCCESS;
    }
}
