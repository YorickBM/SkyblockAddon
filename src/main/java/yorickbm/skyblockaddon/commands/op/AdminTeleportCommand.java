package yorickbm.skyblockaddon.commands.op;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.commands.interfaces.OverWorldCommandStack;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.UUID;

public class AdminTeleportCommand extends OverWorldCommandStack {
    public AdminTeleportCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
            .then(Commands.literal("admin")
                .requires(source -> source.getEntity() instanceof ServerPlayer && source.hasPermission(Commands.LEVEL_ADMINS))
                .then(Commands.literal("tp")
                    .then(Commands.argument("uuid", UuidArgument.uuid())
                        .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity(), UuidArgument.getUuid(context, "uuid")))
                    )
                )
            )
        );
    }

    public int execute(CommandSourceStack command, ServerPlayer executor, UUID uuid) {
        if(super.execute(command, executor) == 0) return Command.SINGLE_SUCCESS;

        command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByUUID(uuid);
            if (island == null) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.island.not.found.uuid").formatted(uuid.toString())));
                return;
            }

            command.sendSuccess(new TextComponent(
                    SkyBlockAddonLanguage.getLocalizedString("commands.teleporting.other")
                            .formatted(UsernameCache.getBlocking(island.getOwner())))
                    .withStyle(ChatFormatting.GREEN), false);
            island.teleportTo(executor);
        });

        return Command.SINGLE_SUCCESS;
    }
}
