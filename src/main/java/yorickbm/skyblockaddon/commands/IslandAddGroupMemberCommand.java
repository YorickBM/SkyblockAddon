package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.commands.interfaces.OverWorldCommandStack;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class IslandAddGroupMemberCommand extends OverWorldCommandStack {
    public IslandAddGroupMemberCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
            .requires(source -> source.getEntity() instanceof ServerPlayer)
                .then(Commands.literal("group")
                    .then(Commands.argument("groupName", StringArgumentType.string())
                        .suggests(this::groupOptions)
                        .then(Commands.literal("addMember")
                            .then(Commands.argument("player", EntityArgument.player())
                                .executes(
                                    context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity(), EntityArgument.getPlayer(context, "player"), StringArgumentType.getString(context, "groupName"))
                                )
                            )
                        )
                    )
                )
        );
    }

    private CompletableFuture<Suggestions> groupOptions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        ServerPlayer player = (ServerPlayer) context.getSource().getEntity();
        if(player == null) return builder.buildFuture();

        context.getSource().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByEntityUUID(player.getUUID());
            if (island == null) {
                return;
            }

            island.getGroups().forEach(g -> {
                builder.suggest(g.getItem().getDisplayName().getString().trim());
            });

        });

        return builder.buildFuture();
    }

    public int execute(CommandSourceStack command, ServerPlayer executor, ServerPlayer target, String groupName) {
        if(super.execute(command, executor) == 0) return Command.SINGLE_SUCCESS;

        command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByEntityUUID(executor.getUUID());
            if (island == null) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.has.no.island")));
                return;
            }

            Optional<UUID> groupId = island.getGroupByName(groupName);
            if(groupId.isEmpty() || !island.hasGroup(groupId.get())) {
                command.sendFailure(new TextComponent(String.format(SkyBlockAddonLanguage.getLocalizedString("commands.group.not.found"), groupId)));
                return;
            }

            IslandGroup group = island.getGroup(groupId.get());
            group.addMember(target.getUUID());

            command.sendSuccess(new TextComponent(String.format(SkyBlockAddonLanguage.getLocalizedString("commands.group.added.member"), target.getDisplayName().getString().trim(), group.getItem().getDisplayName().getString().trim())).withStyle(ChatFormatting.GREEN), false);

        });

        return Command.SINGLE_SUCCESS;
    }
}
