package yorickbm.skyblockaddon.commands.op;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.commands.interfaces.OverWorldCommandStack;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.util.FunctionRegistry;
import yorickbm.skyblockaddon.util.IslandChunkManager;
import yorickbm.skyblockaddon.util.ProgressBar;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AdminPurgeCommand extends OverWorldCommandStack {

    public AdminPurgeCommand(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
                .then(Commands.literal("admin")
                        .requires(source -> source.getEntity() instanceof ServerPlayer && source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(Commands.literal("purge")
                                .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity()))
                        )
                )
        );
    }

    public int execute(final CommandSourceStack command, final ServerPlayer executor) {
        if(super.execute(command, executor) == 0) return Command.SINGLE_SUCCESS;

        executor.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            final List<UUID> purgable = cap.getPurgableIslands().stream().findFirst().stream().toList();;

            //Register purge function into registry
            final UUID functionKey = UUID.randomUUID();
            FunctionRegistry.registerFunction(functionKey, (e) -> {

                final List<Island> islands = purgable.stream()
                        // Get Island objects for each UUID
                        .map(cap::getIslandByUUID)
                        // Filter only the abandoned islands
                        .filter(Island::isAbandoned)
                        // Collect all bounding boxes into a list
                        .collect(Collectors.toList());

                //Create progressbar
                final ProgressBar progressBar = new ProgressBar(executor, "Purging...", islands.size());
                progressBar.start();

                // Once all bounding boxes are collected, pass them to the clearBlocksInBoundingBoxes method
                IslandChunkManager manager = new IslandChunkManager(
                        4,
                        island -> {
                            progressBar.finishedOne();
                            //TODO: Remove NBT File
                            //TODO: Remove island data from cache
                            //TODO: Remove island data from persistent cache
                        },
                        () -> {
                            progressBar.kill();
                            executor.sendMessage(
                                new TextComponent(
                                        SkyBlockAddonLanguage.getLocalizedString("commands.admin.purge.done")
                                                .formatted((purgable.size() - cap.getPurgableIslands().size()) + ""))
                                        .withStyle(ChatFormatting.GREEN),
                                executor.getUUID());
                        }
                );
                manager.createDummies(islands, executor.getLevel(), executor, progressBar);


                return true;
            }, 5); //Register function to purge under hash 'functionKey' for 5 minutes.

            //Sending clickable message to accept request to requested
            command.sendSuccess(new TextComponent(
                            SkyBlockAddonLanguage.getLocalizedString("commands.admin.purge.ask")
                                    .formatted(purgable.size() + ""))
                            .withStyle(ChatFormatting.GREEN)
                            .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, FunctionRegistry.getCommand(functionKey))))
                    , false);
        });
        return Command.SINGLE_SUCCESS;
    }

}

//f6ad3af8-6861-4fd2-a512-024735d3403c -> -3005 150 -16017
//c8adf14c-2238-48f0-9c66-c79bc5e9f464 -> 25993 121 22994
