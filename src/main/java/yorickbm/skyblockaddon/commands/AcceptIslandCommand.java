package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import yorickbm.skyblockaddon.capabilities.PlayerIslandProvider;
import yorickbm.skyblockaddon.util.LanguageFile;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class AcceptIslandCommand {
    public AcceptIslandCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island").then(Commands.literal("accept").executes((command) -> { //.then(Commands.argument("name", MessageArgument.message()))
            return execute(command.getSource()); //, MessageArgument.getMessage(command, "name")
        })));
    }

    private int execute(CommandSourceStack command) { //, Component islandName

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
            if(island.getInvite() != null) {
                if (island.hasOne()) {
                    command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.accept.hasone")));
                    return;
                }
                Player p = command.getLevel().getPlayerByUUID(island.getInvite());
                p.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> island.joinIsland(i, player));
                command.sendSuccess(new TextComponent(LanguageFile.getForKey("commands.island.accept.success").formatted(p.getGameProfile().getName())).withStyle(ChatFormatting.GREEN), false);
            }
            if(island.getTeleportInvite() != null) {
                Player p = command.getLevel().getPlayerByUUID(island.getTeleportInvite());
                if(p.getLevel().dimension() != Level.OVERWORLD) {
                    command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.teleport.user.request.notoverworld").formatted(p.getGameProfile().getName())));
                    return;
                }

                island.acceptTeleport(p);
                command.sendSuccess(new TextComponent(LanguageFile.getForKey("commands.island.teleport.user.request.success").formatted(p.getGameProfile().getName())).withStyle(ChatFormatting.GREEN), false);
                p.sendMessage(new TextComponent(LanguageFile.getForKey("commands.island.teleport.user.success").formatted(player.getGameProfile().getName())).withStyle(ChatFormatting.GREEN), p.getUUID());
            }
        });

        return Command.SINGLE_SUCCESS;
    }
}
