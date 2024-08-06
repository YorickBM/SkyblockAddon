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
import yorickbm.skyblockaddon.configs.SkyblockAddonLanguageConfig;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.UUID;

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
            command.sendFailure(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.not.player")));
            return Command.SINGLE_SUCCESS;
        }

        if(player.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.not.overworld")));
            return Command.SINGLE_SUCCESS;
        }

        command.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(g -> {
            UUID islandIdOn = g.getIslandIdByLocation(new Vec3i(player.getX(), 121, player.getZ()));
            if(islandIdOn == null) { //Not on an island so we do not affect permission
                command.sendFailure(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.admin.where.none")));
                return;
            }

            command.sendSuccess(
                ServerHelper.styledText(
                        SkyblockAddonLanguageConfig.getForKey("commands.admin.where.success").formatted(islandIdOn),
                    Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, islandIdOn.toString()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(SkyblockAddonLanguageConfig.getForKey("chat.hover.copy")))),
                    ChatFormatting.GREEN
                ),
                true
            );
        });

        return Command.SINGLE_SUCCESS;
    }

}
