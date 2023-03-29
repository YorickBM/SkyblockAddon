package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import yorickbm.skyblockaddon.capabilities.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.PlayerIslandProvider;
import yorickbm.skyblockaddon.util.IslandData;
import yorickbm.skyblockaddon.util.LanguageFile;

import java.io.IOException;

public class CreateIslandCommand {

    public CreateIslandCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island").then(Commands.literal("create").executes((command) -> { //.then(Commands.argument("name", MessageArgument.message()))
            return execute(command.getSource()); //, MessageArgument.getMessage(command, "name")
        })));
    }

    private int execute(CommandSourceStack command) { //, Component islandName

        if(!(command.getEntity() instanceof Player player)) { //Executed by non-player
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.create.nonplayer")));
            return Command.SINGLE_SUCCESS;
        }

        if(player.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.create.notoverworld")));
            return Command.SINGLE_SUCCESS;
        }

        player.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(island -> {

            if(island.hasOne()) {
                command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.create.hasone")));
                return;
            }

            player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(generator -> {
                    Thread asyncIslandGen = new Thread(() -> {
                        try {
                            Vec3i vec = generator.genIsland(command.getLevel());
                            if(vec.distToCenterSqr(new Vec3(IslandGeneratorProvider.DEFAULT_SPAWN.getX(), IslandGeneratorProvider.DEFAULT_SPAWN.getY(), IslandGeneratorProvider.DEFAULT_SPAWN.getZ())) < 10) {
                                command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.create.fail")));
                                return;
                            }

                            IslandData islandData = new IslandData(player.getUUID(), vec);
                            String id = generator.registerIsland(islandData);
                            island.setIsland(id);
                            islandData.teleport(player);

                            command.sendSuccess(new TextComponent(LanguageFile.getForKey("commands.island.create.success")).withStyle(ChatFormatting.GREEN), true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    asyncIslandGen.start();
            });
        });

        return Command.SINGLE_SUCCESS;
    }

}
