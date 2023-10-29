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
import yorickbm.skyblockaddon.capabilities.providers.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.providers.PlayerIslandProvider;
import yorickbm.skyblockaddon.configs.SkyblockAddonLanguageConfig;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.Collection;

public class AcceptIslandCommand {
    public AcceptIslandCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island").then(Commands.literal("accept").then(Commands.argument("targets", EntityArgument.players()).executes((command) -> { //.then(Commands.argument("name", MessageArgument.message()))
            return execute(command.getSource(), EntityArgument.getPlayers(command, "targets")); //, MessageArgument.getMessage(command, "name")
        }))));
    }

    private int execute(CommandSourceStack command, Collection<ServerPlayer> targets) { //, Component islandName

        if(!(command.getEntity() instanceof Player player)) { //Executed by non-player
            command.sendFailure(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.not.player")));
            return Command.SINGLE_SUCCESS;
        }

        if(player.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.not.overworld")));
            return Command.SINGLE_SUCCESS;
        }

        player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(g -> player.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(island -> {

            if(!g.getIslandById(island.getIslandId()).isMember(player.getUUID())) {
                player.sendMessage(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("commands.not.permitted"), ChatFormatting.RED), player.getUUID());
                return;
            }

            if(targets.stream().findFirst().isEmpty()) {
                command.sendFailure(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.teleport.request.offline")));
                return;
            }

            Player requester = targets.stream().findFirst().get();
            if(island.teleportValid(requester.getUUID())) {
                if(requester.getLevel().dimension() != Level.OVERWORLD) {
                    command.sendFailure(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.teleport.request.not.overworld").formatted(requester.getGameProfile().getName())));
                    return;
                }

                command.sendSuccess(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.teleport.request.success").formatted(requester.getGameProfile().getName())).withStyle(ChatFormatting.GREEN), false);
                requester.sendMessage(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.teleport.user.success").formatted(player.getGameProfile().getName())).withStyle(ChatFormatting.GREEN), requester.getUUID());

                g.getIslandById(island.getIslandId()).teleport(requester);
            } else {
                command.sendFailure(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.teleport.request.expired")));
            }
        }));

        return Command.SINGLE_SUCCESS;
    }
}
