package yorickbm.skyblockaddon.commands.op;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.capabilities.SkyblockAddonWorldCapability;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.chunk.ChunkTaskScheduler;
import yorickbm.skyblockaddon.commands.interfaces.OverWorldCommandStack;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.util.FunctionRegistry;
import yorickbm.skyblockaddon.chunk.IslandChunkManager;
import yorickbm.skyblockaddon.util.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class AdminPurgeCommand extends OverWorldCommandStack {

    public AdminPurgeCommand(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("island")
                        .then(Commands.literal("admin")
                                .requires(source -> source.getEntity() instanceof ServerPlayer &&
                                        source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.literal("purge")
                                        .then(Commands.argument("target", UuidArgument.uuid())
                                                .executes(ctx -> executeUUID(
                                                        ctx.getSource(),
                                                        (ServerPlayer) ctx.getSource().getEntity(),
                                                        UuidArgument.getUuid(ctx, "target"),
                                                        0)) // default thread count
                                                .then(Commands.argument("clearCache", IntegerArgumentType.integer(0, 1))
                                                        .executes(ctx -> executeUUID(
                                                                ctx.getSource(),
                                                                (ServerPlayer) ctx.getSource().getEntity(),
                                                                UuidArgument.getUuid(ctx, "target"),
                                                                IntegerArgumentType.getInteger(ctx, "clearCache")
                                                        ))
                                                )
                                        )
                                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                                .executes(ctx -> executeAmount(
                                                        ctx.getSource(),
                                                        (ServerPlayer) ctx.getSource().getEntity(),
                                                        IntegerArgumentType.getInteger(ctx, "count"),
                                                        0)) // default thread count
                                                .then(Commands.argument("clearCache", IntegerArgumentType.integer(0, 1))
                                                        .executes(ctx -> executeAmount(
                                                                ctx.getSource(),
                                                                (ServerPlayer) ctx.getSource().getEntity(),
                                                                IntegerArgumentType.getInteger(ctx, "count"),
                                                                IntegerArgumentType.getInteger(ctx, "clearCache")
                                                        ))
                                                )
                                        )
                                )
                        )
        );
    }

    private int executeUUID(CommandSourceStack command, ServerPlayer executor, UUID uuid, int clearCache) {
        if (super.execute(command, executor) == 0) return Command.SINGLE_SUCCESS;

        executor.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            List<UUID> purgable = new ArrayList<>(cap.getPurgableIslands());

            if (!purgable.contains(uuid)) {
                executor.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.purge.notallowed")).withStyle(ChatFormatting.RED), executor.getUUID());
                return;
            }

            registerAndPromptPurge(command, executor, cap, List.of(uuid), clearCache);
        });

        return Command.SINGLE_SUCCESS;
    }

    private int executeAmount(CommandSourceStack command, ServerPlayer executor, int count, int clearCache) {
        if (super.execute(command, executor) == 0) return Command.SINGLE_SUCCESS;

        executor.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            List<UUID> purgable = new ArrayList<>(cap.getPurgableIslands());

            if (purgable.isEmpty()) {
                executor.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.purge.zero")).withStyle(ChatFormatting.RED), executor.getUUID());
                return;
            }

            List<UUID> selected = purgable.stream().limit(count).collect(Collectors.toList());
            registerAndPromptPurge(command, executor, cap, selected, clearCache);
        });

        return Command.SINGLE_SUCCESS;
    }

    private void registerAndPromptPurge(CommandSourceStack command, ServerPlayer executor, SkyblockAddonWorldCapability cap, List<UUID> islandUUIDs, int clearCache) {
        UUID functionKey = UUID.randomUUID();

        FunctionRegistry.registerFunction(functionKey, (e) -> {
            startPurgeWithIslands(cap, executor, islandUUIDs, clearCache);
            return true;
        }, 5);

        command.sendSuccess(new TextComponent(
                        SkyBlockAddonLanguage.getLocalizedString("commands.admin.purge.ask")
                                .formatted(String.valueOf(islandUUIDs.size())))
                        .withStyle(ChatFormatting.GREEN)
                        .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                FunctionRegistry.getCommand(functionKey)))),
                false);
    }

    private void startPurgeWithIslands(SkyblockAddonWorldCapability cap, ServerPlayer executor, List<UUID> islandUUIDs, int clearCache) {
        ChunkTaskScheduler.init();
        if(clearCache > 0) ChunkTaskScheduler.clear();

        List<Island> islands = islandUUIDs.stream()
                .map(cap::getIslandByUUID)
                .filter(Objects::nonNull)
                .filter(Island::isAbandoned)
                .collect(Collectors.toList());

        ProgressBar progressBar = new ProgressBar(executor, "Batches...", islands.size());
        progressBar.start();

        IslandChunkManager manager = new IslandChunkManager();
        manager.createDummies(islands, progressBar, island -> {
                    cap.islandSpaceReusable(island.getCenter());
                    cap.removeIslandNBT(island);
                    cap.clearIslandCache(island);
                    progressBar.finishedOne();
                },
                () -> {
                    progressBar.kill();
                    executor.sendMessage(new TextComponent(
                            SkyBlockAddonLanguage.getLocalizedString("commands.admin.purge.done")
                                    .formatted(String.valueOf(islands.size())))
                            .withStyle(ChatFormatting.GREEN), executor.getUUID());
                },
                executor.getLevel());
    }
}


//f6ad3af8-6861-4fd2-a512-024735d3403c -> -3005 150 -16017
//c8adf14c-2238-48f0-9c66-c79bc5e9f464 -> 25993 121 22994
