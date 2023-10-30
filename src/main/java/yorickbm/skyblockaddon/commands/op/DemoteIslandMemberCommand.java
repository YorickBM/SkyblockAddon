package yorickbm.skyblockaddon.commands.op;

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
import yorickbm.skyblockaddon.islands.IslandData;
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
            command.sendFailure(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.not.player")));
            return Command.SINGLE_SUCCESS;
        }

        if(player.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.not.overworld")));
            return Command.SINGLE_SUCCESS;
        }

        if(targets.stream().findFirst().isEmpty()) {
            command.sendFailure(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.not.online")));
            return Command.SINGLE_SUCCESS;
        }

        Player target = targets.stream().findFirst().get();
        target.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(g -> {
            IslandData island = g.getIslandById(i.getIslandId());
            if(island == null) {
                command.sendFailure(new TextComponent(String.format(SkyblockAddonLanguageConfig.getForKey("commands.not.found"), i.getIslandId())));
                return;
            }
            if(!island.isMember(target.getUUID())) {
                command.sendFailure(new TextComponent(String.format(SkyblockAddonLanguageConfig.getForKey("commands.not.part"), target.getGameProfile().getName())));
                return;
            }

            if(island.isOwner(target.getUUID())) {
                if (island.removeOwner(target.getUUID())) {
                    command.sendSuccess(ServerHelper.formattedText(String.format(SkyblockAddonLanguageConfig.getForKey("commands.admin.demote.admin"), target.getGameProfile().getName(), i.getIslandId(), island.getOwner(command.getServer()).getName()), ChatFormatting.GREEN), true);
                } else {
                    command.sendFailure(ServerHelper.formattedText(String.format(SkyblockAddonLanguageConfig.getForKey("commands.admin.demote.not.possible"), target.getGameProfile().getName(), i.getIslandId())));
                }
            } else if(island.isIslandAdmin(target.getUUID())) {
                island.removeAdmin(target.getUUID());
                command.sendSuccess(ServerHelper.formattedText(String.format(SkyblockAddonLanguageConfig.getForKey("commands.admin.demote.member"), target.getGameProfile().getName(), i.getIslandId()), ChatFormatting.GREEN), true);
            } else {
                command.sendFailure(ServerHelper.formattedText(String.format(SkyblockAddonLanguageConfig.getForKey("commands.admin.demote.already.member"), target.getGameProfile().getName(), i.getIslandId())));
            }
        }));

        return Command.SINGLE_SUCCESS;
    }
}
