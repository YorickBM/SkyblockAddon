package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.UUID;

public class IslandTeleportCommand {
    public IslandTeleportCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
            .then(Commands.literal("tp")
                .requires(source -> source.getEntity() instanceof ServerPlayer)
                .executes(context -> executePersonal(context.getSource(), (ServerPlayer)context.getSource().getEntity()))
            )
        );
    }

    private int executePersonal(CommandSourceStack command, ServerPlayer executor) {
        if(executor.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.not.in.overworld")));
            return Command.SINGLE_SUCCESS;
        }

        command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByEntityUUID(executor);
            if(island == null) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.has.no.island")));
                return;
            }

            command.sendSuccess(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.teleporting.own")).withStyle(ChatFormatting.GREEN), false);
            island.teleportTo(executor);
        });
        return Command.SINGLE_SUCCESS;
    }

    private int executeRequest(CommandSourceStack command, ServerPlayer executor, ServerPlayer requested) {
        if(executor.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.not.in.overworld")));
            return Command.SINGLE_SUCCESS;
        }

        command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
        });
        return Command.SINGLE_SUCCESS;
    }
}
