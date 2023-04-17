package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import yorickbm.skyblockaddon.capabilities.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.PlayerIsland;
import yorickbm.skyblockaddon.capabilities.PlayerIslandProvider;
import yorickbm.skyblockaddon.util.IslandData;
import yorickbm.skyblockaddon.util.LanguageFile;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.time.Instant;

public class LeaveIslandCommand {

    public LeaveIslandCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island").then(Commands.literal("leave").executes((command) -> { //.then(Commands.argument("name", MessageArgument.message()))
            return execute(command.getSource()); //, MessageArgument.getMessage(command, "name")
        })));
    }

    private int execute(CommandSourceStack command) { //, Component islandName

        if(!(command.getEntity() instanceof Player player)) { //Executed by non-player
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.nonplayer")));
            return Command.SINGLE_SUCCESS;
        }

        if(player.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.notoverworld")));
            return Command.SINGLE_SUCCESS;
        }

        player.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(island -> {
            if(!island.hasOne()) {
                command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.leave.hasnone")));
                return;
            }

            player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(generator -> {
                IslandData islandData = generator.getIslandById(island.getIslandId());

                LeaveIslandCommand.leaveIsland(islandData, island, player);
            });
        });

        return Command.SINGLE_SUCCESS;
    }

    public static void leaveIsland(IslandData islandData, PlayerIsland island, Player player) {
        if(islandData.isOwner(player.getUUID()) && islandData.getMembers().size() > 0)
            islandData.setOwner(islandData.getMembers().get(0));
        else islandData.setOwner(null);

        islandData.removeIslandMember(player.getUUID());

        island.timestamp = Instant.now().getEpochSecond();
        island.setIsland(""); //Make it empty so its NONE

        player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(g -> player.teleportTo(g.getSpawnLocation().getX(), g.getSpawnLocation().getY(), g.getSpawnLocation().getZ()));
        ServerHelper.playSongToPlayer((ServerPlayer) player, SoundEvents.CHORUS_FRUIT_TELEPORT, 0.4f, 1f);

        Style style = new TextComponent(LanguageFile.getForKey("commands.island.leave.undo")).withStyle(ChatFormatting.GREEN).getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/island undo")).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("Click to undo leaving island...")));
        player.sendMessage(new TextComponent(LanguageFile.getForKey("commands.island.leave.undo")).withStyle(style), player.getUUID());
        player.sendMessage(new TextComponent(LanguageFile.getForKey("commands.island.leave.success")).withStyle(ChatFormatting.GREEN), player.getUUID());
    }

}
