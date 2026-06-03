package yorickbm.skyblockaddon.commands.op;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.capabilities.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.commands.interfaces.Cmds;
import yorickbm.skyblockaddon.commands.interfaces.OverWorldCommandStack;
import yorickbm.skyblockaddon.core.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.core.islands.IslandManager;
import yorickbm.skyblockaddon.core.util.UsernameCache;
import yorickbm.skyblockaddon.islands.ForgeIsland;

import java.util.UUID;

public class AdminTeleportCommand extends OverWorldCommandStack {
    public AdminTeleportCommand(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Cmds.literal("island")
            .then(Cmds.literal("admin")
                .requires(source -> source.hasPermission(Commands.LEVEL_MODERATORS))
                .then(Cmds.literal("tp")
                    .then(Cmds.argument("uuid", UuidArgument.uuid())
                        // /island admin tp <uuid> — teleports self (in-game only)
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
                        .executes(context -> execute(
                                context.getSource(),
                                (ServerPlayer) context.getSource().getEntity(),
                                UuidArgument.getUuid(context, "uuid")))
                        // /island admin tp <uuid> <player> — teleports specified player (console + in-game)
                        .then(Cmds.argument("player", EntityArgument.player())
                            .requires(source -> source.hasPermission(Commands.LEVEL_MODERATORS))
                            .executes(context -> execute(
                                    context.getSource(),
                                    EntityArgument.getPlayer(context, "player"),
                                    UuidArgument.getUuid(context, "uuid"))))
                    )
                )
            )
        );
    }

    public int execute(final CommandSourceStack command, final ServerPlayer executor, final UUID uuid) {
        if (super.execute(command, executor) == 0) return Command.SINGLE_SUCCESS;

        command.getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD)
            .getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
                final ForgeIsland island = (ForgeIsland) IslandManager.getInstance().getIslandByUUID(uuid);
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
