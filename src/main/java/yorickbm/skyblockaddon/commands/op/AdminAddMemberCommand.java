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
import net.minecraft.world.level.Level;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.Objects;
import java.util.UUID;

public class AdminAddMemberCommand {
    public AdminAddMemberCommand(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
            .then(Commands.literal("admin")
                .requires(source -> source.getEntity() instanceof ServerPlayer && source.hasPermission(Commands.LEVEL_ADMINS))
                .then(Commands.literal("addMember")
                    .then(Commands.argument("islandId", UuidArgument.uuid())
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity(),
                                        UuidArgument.getUuid(context, "islandId"),
                                        EntityArgument.getPlayer(context, "player")))
                        )
                        .then(Commands.argument("uuid", UuidArgument.uuid())
                                .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity(),
                                        UuidArgument.getUuid(context, "islandId"),
                                        UuidArgument.getUuid(context, "uuid")))
                        )
                    )
                )
            )
        );
    }

    public int execute(final CommandSourceStack command, final ServerPlayer executor, final UUID islandId, final ServerPlayer target) {
        execute(command, executor, islandId, target.getUUID()); //Convert target into UUID
        return Command.SINGLE_SUCCESS;
    }

    public int execute(final CommandSourceStack command, final ServerPlayer executor, final UUID islandId, final UUID target) {

        Objects.requireNonNull(command.getServer().getLevel(Level.OVERWORLD)).getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            final Island island = cap.getIslandByUUID(islandId);
            if (island == null) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.island.not.found.uuid").formatted(islandId)));
                return;
            }

            //Determine if target does not already have an island
            if(cap.getIslandByEntityUUID(target) != null) {
                if(island.isPartOf(target)) {
                    command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.island.member.part").formatted(UsernameCache.getBlocking(target))));
                    return;
                } else {
                    command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.island.has.island").formatted(UsernameCache.getBlocking(target))));
                    return;
                }
            }

            //Add target to islands basic group
            if(!island.addMember(target, SkyblockAddon.MOD_UUID)) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.island.member.failure").formatted(UsernameCache.getBlocking(target), UsernameCache.getBlocking(island.getOwner()))));
                return;
            }

            //Inform admin
            command.sendSuccess(
                    new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.island.member.added").formatted(UsernameCache.getBlocking(target), UsernameCache.getBlocking(island.getOwner())))
                            .withStyle(ChatFormatting.GREEN)
                    , false);
        });

        return Command.SINGLE_SUCCESS;
    }
}
