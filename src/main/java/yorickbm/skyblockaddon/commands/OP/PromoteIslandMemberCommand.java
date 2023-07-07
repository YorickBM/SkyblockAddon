package yorickbm.skyblockaddon.commands.OP;

import com.mojang.authlib.GameProfile;
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

public class PromoteIslandMemberCommand {
    public PromoteIslandMemberCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("island")
                        .then(
                                Commands.literal("admin")
                                        .requires(p -> p.hasPermission(3))
                                        .then(Commands.literal("promote").then(Commands.argument("target", EntityArgument.players())
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
                if(!island.isMember(target.getUUID())) {
                    command.sendFailure(new TextComponent(String.format(LanguageFile.getForKey("commands.island.admin.island.notpart"), target.getGameProfile().getName())));
                    return;
                }

                if(island.isIslandMember(target.getUUID())) {
                    island.makeAdmin(target.getUUID());
                    command.sendSuccess(ServerHelper.formattedText(String.format(LanguageFile.getForKey("commands.island.admin.promote.admin"), target.getGameProfile().getName(), i.getIslandId()), ChatFormatting.GREEN), true);
                } else if(island.isIslandAdmin(target.getUUID())) {
                    GameProfile oldOwner = island.getOwner(player.getServer()); //Collect old owner
                    island.setOwner(target.getUUID()); //Update owner
                    command.sendSuccess(ServerHelper.formattedText(String.format(LanguageFile.getForKey("commands.island.admin.promote.owner"), target.getGameProfile().getName(), i.getIslandId(), oldOwner.getName()), ChatFormatting.GREEN), true);
                } else {
                    command.sendFailure(ServerHelper.formattedText(String.format(LanguageFile.getForKey("commands.island.admin.promote.alreadyowner"), target.getGameProfile().getName(), i.getIslandId())));
                }
            });
        });

        return Command.SINGLE_SUCCESS;
    }
}
