package yorickbm.skyblockaddon.commands.OP;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import yorickbm.skyblockaddon.capabilities.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.PlayerIslandProvider;
import yorickbm.skyblockaddon.util.LanguageFile;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.Collection;
import java.util.UUID;

//execute as playername run island join islandId
public class SetPlayersIslandCommand {
    public SetPlayersIslandCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("island")
            .then(
                Commands.literal("admin")
                .requires(p -> p.hasPermission(3))
                .then(
                    Commands.literal("setId")
                    .then(
                        Commands.argument("target", EntityArgument.players())
                        .then(Commands.argument("id", UuidArgument.uuid())
                            .executes((command) -> execute(command.getSource(), EntityArgument.getPlayers(command, "target"), UuidArgument.getUuid(command,"id")))
                        )
                    )
                )
            )
        );
    }

    private int execute(CommandSourceStack command, Collection<ServerPlayer> targets, UUID id) {
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
                if(i.hasOne()) {
                    player.sendMessage(ServerHelper.formattedText(
                            LanguageFile.getForKey("commands.island.admin.setId.hasone").formatted(target.getGameProfile().getName(), i.getIslandId())
                            , ChatFormatting.GREEN, ChatFormatting.ITALIC
                    ), player.getUUID());

                    g.getIslandById(i.getIslandId()).removeIslandMember(player.getUUID());
                }

                g.getIslandById(id.toString()).addIslandMember(target.getUUID());
                i.setIsland(id.toString());

                command.sendSuccess(
                    ServerHelper.formattedText(
                        LanguageFile.getForKey("commands.island.admin.setId.success").formatted(target.getGameProfile().getName(), id.toString()),
                        ChatFormatting.GREEN
                    ),
                    true
                );
                target.sendMessage(
                    ServerHelper.formattedText(LanguageFile.getForKey("commands.island.admin.setId.teleport"), ChatFormatting.GREEN)
                    ,target.getUUID());

                g.getIslandById(id.toString()).teleport(target);
            });
        });

        return Command.SINGLE_SUCCESS;
    }
}
