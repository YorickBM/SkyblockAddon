package yorickbm.skyblockaddon.commands.op;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import yorickbm.skyblockaddon.capabilities.providers.IslandGeneratorProvider;
import yorickbm.skyblockaddon.util.LanguageFile;
import yorickbm.skyblockaddon.util.ServerHelper;

public class WhereAmICommand {

    public WhereAmICommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("island")
                        .then(
                                Commands.literal("admin")
                                        .requires(p -> p.hasPermission(3))
                                        .then(
                                                Commands.literal("where")
                                                    .executes((command) -> execute(command.getSource()))
                                        )
                        )
        );
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

        command.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(g -> {
            String islandIdOn = g.getIslandIdByLocation(new Vec3i(player.getX(), 121, player.getZ()));
            if(islandIdOn == null || islandIdOn.equals("")) { //Not on an island so we do not affect permission
                command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.admin.where.none")));
                return;
            }

            command.sendSuccess(
                ServerHelper.styledText(
                    LanguageFile.getForKey("commands.island.admin.where.success").formatted(islandIdOn),
                    Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, islandIdOn))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(LanguageFile.getForKey("chat.hover.copy")))),
                    ChatFormatting.GREEN
                ),
                true
            );
        });

        return Command.SINGLE_SUCCESS;
    }

}
