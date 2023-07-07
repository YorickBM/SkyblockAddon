package yorickbm.skyblockaddon.commands.OP;

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
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.util.LanguageFile;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.Collection;

public class DemoteIslandMemberCommand {
    public DemoteIslandMemberCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("island")
                        .then(
                                Commands.literal("admin")
                                        .requires(p -> p.hasPermission(3))
                                        .then(Commands.literal("demote").then(Commands.argument("target", EntityArgument.players())
                                                .executes( (command) -> execute(command.getSource(), EntityArgument.getPlayers(command, "target")))
                                        ))
                        )
        );
    }

    private int execute(CommandSourceStack command, Collection<ServerPlayer> targets) {
        if(!(command.getEntity() instanceof Player player)) { //Executed by non-player
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.nonplayer")));
            return Command.SINGLE_SUCCESS;
        }
        if(player.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.notoverworld")));
            return Command.SINGLE_SUCCESS;
        }

        if(!targets.stream().findFirst().isPresent()) {
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.admin.offline")));
            return Command.SINGLE_SUCCESS;
        }

        Player target = targets.stream().findFirst().get();
        target.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> {
            player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(g -> {
                IslandData island = g.getIslandById(i.getIslandId());
                if(island == null) {
                    command.sendFailure(new TextComponent(String.format(LanguageFile.getForKey("commands.island.admin.island.notfound"), i.getIslandId())));
                    return;
                }
                if(!island.isMember(target.getUUID()) && !island.isAdmin(target.getUUID()) && !island.isOwner(target.getUUID())) {
                    command.sendFailure(new TextComponent(String.format(LanguageFile.getForKey("commands.island.admin.island.notpart"), target.getGameProfile().getName())));
                    return;
                }

                if(island.isMember(target.getUUID())) {
                    command.sendFailure(ServerHelper.formattedText(String.format(LanguageFile.getForKey("commands.island.admin.demote.alreadymember"), target.getGameProfile().getName(), i.getIslandId())));
                } else if(island.isAdmin(target.getUUID())) {
                    island.removeAdmin(target.getUUID());
                    command.sendSuccess(ServerHelper.formattedText(String.format(LanguageFile.getForKey("commands.island.admin.demote.member"), target.getGameProfile().getName(), i.getIslandId()), ChatFormatting.GREEN), true);
                } else {
                    if (island.removeOwner(target.getUUID())) {
                        command.sendSuccess(ServerHelper.formattedText(String.format(LanguageFile.getForKey("commands.island.admin.demote.admin"), target.getGameProfile().getName(), i.getIslandId(), island.getOwner(command.getServer())), ChatFormatting.GREEN), true);
                    } else {
                        command.sendFailure(ServerHelper.formattedText(String.format(LanguageFile.getForKey("commands.island.admin.demote.notallowed"), target.getGameProfile().getName(), i.getIslandId())));
                    }
                }
            });
        });

        return Command.SINGLE_SUCCESS;
    }
}
