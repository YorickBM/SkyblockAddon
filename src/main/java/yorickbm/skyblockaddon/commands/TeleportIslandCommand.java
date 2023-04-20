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
import yorickbm.skyblockaddon.capabilities.Providers.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.Providers.PlayerIslandProvider;
import yorickbm.skyblockaddon.util.LanguageFile;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.Collection;
import java.util.Optional;

public class TeleportIslandCommand {
    public TeleportIslandCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
                .then(Commands.literal("tp").executes((command) -> execute(command.getSource(), null))
                .then(Commands.argument("targets", EntityArgument.players()).executes((command) -> execute(command.getSource(),  EntityArgument.getPlayers(command, "targets"))))
        ));
    }

    private int execute(CommandSourceStack command, Collection<ServerPlayer> targets) { //, Component islandName

        if(!(command.getEntity() instanceof Player player)) { //Executed by non-player
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.nonplayer")));
            return Command.SINGLE_SUCCESS;
        }

        if(player.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.notoverworld")));
            return Command.SINGLE_SUCCESS;
        }

        player.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(island -> {
            if(targets == null || targets.isEmpty()) {
                if(!island.hasOne()) {
                    command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.teleport.hasnone")));
                    return;
                }

                player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(generator -> {
                    generator.getIslandById(island.getIslandId()).teleport(player);

                    command.sendSuccess(new TextComponent(LanguageFile.getForKey("commands.island.teleport.success")).withStyle(ChatFormatting.GREEN), true);
                });
            } else {
                Optional<ServerPlayer> p = targets.stream().findFirst();
                if(p.isEmpty()) {
                    command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.teleport.user.notfound")));
                    return;
                }
                p.get().getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> {
                    if(!i.hasOne()) {
                        command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.teleport.user.hasnone").formatted(p.get().getGameProfile().getName())));
                        return;
                    }
                    i.addTeleport(player.getUUID());
                    command.sendSuccess(
                            ServerHelper.formattedText(
                                LanguageFile.getForKey("commands.island.teleport.user.request.send").formatted(p.get().getGameProfile().getName()),
                                    ChatFormatting.GREEN
                            )
                        , false);

                    p.get().sendMessage(
                            ServerHelper.styledText(
                                    LanguageFile.getForKey("commands.island.teleport.user.request").formatted(player.getGameProfile().getName(),player.getGameProfile().getName()),
                                    Style.EMPTY
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island accept " + player.getGameProfile().getName()))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(LanguageFile.getForKey("chat.hover.run.teleport")))),
                                    ChatFormatting.GREEN
                            ),
                            p.get().getUUID()
                    );
                });
            }
        });

        return Command.SINGLE_SUCCESS;
    }
}
