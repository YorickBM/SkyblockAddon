package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import yorickbm.skyblockaddon.capabilities.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.PlayerIslandProvider;
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
                command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.kick.hasnone")));
                return;
            }

            Optional<ServerPlayer> p = targets.stream().findFirst();
            if(p.isPresent()) {
                p.get().getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> {
                    if(!i.hasOne() || i.getLocation().getX() != island.getLocation().getX() || i.getLocation().getZ() != island.getLocation().getZ()) {
                        command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.kick.notpart")));
                        return;
                    }
                    
                    if(i.isOwner()) {
                        command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.kick.notallowed")));
                        return;
                    }


                    i.leaveIsland(p.get());
                    p.get().sendMessage(new TextComponent(LanguageFile.getForKey("commands.island.kick.kicked").formatted(player.getGameProfile().getName())).withStyle(ChatFormatting.GREEN), player.getUUID());
                    player.sendMessage(new TextComponent(LanguageFile.getForKey("commands.island.kick.success").formatted(p.get().getGameProfile().getName())).withStyle(ChatFormatting.GREEN), player.getUUID());
                });
            }

        });

        return Command.SINGLE_SUCCESS;
    }
}
