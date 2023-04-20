package yorickbm.skyblockaddon.commands.OP;

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
import yorickbm.skyblockaddon.capabilities.Providers.PlayerIslandProvider;
import yorickbm.skyblockaddon.util.LanguageFile;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.Collection;

public class GetIslandIdCommand {
    public GetIslandIdCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("island")
            .then(
                Commands.literal("admin")
                .requires(p -> p.hasPermission(3))
                .then(
                    Commands.literal("getId")
                    .then(
                        Commands.argument("target", EntityArgument.players())
                        .executes((command) -> execute(command.getSource(), EntityArgument.getPlayers(command, "target")))
                    )
                )
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
        targets.stream().findFirst().get().getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> {
           if(!i.hasOne()) {
               command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.admin.getId.hasnone")));
               return;
           }

            player.sendMessage(
                ServerHelper.styledText(
                    LanguageFile.getForKey("commands.island.admin.getId.success").formatted(targets.stream().findFirst().get().getGameProfile().getName()),
                    Style.EMPTY
                            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, i.getIslandId()))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Click to copy!"))),
                    ChatFormatting.GREEN //TODO: Make it use language file for hover.
                ),
                    player.getUUID()
            );
           command.sendSuccess(new TextComponent("-> " + i.getIslandId()), false);

        });

        return Command.SINGLE_SUCCESS;
    }
}
