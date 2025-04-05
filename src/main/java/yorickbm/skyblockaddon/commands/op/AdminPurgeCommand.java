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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.fml.loading.FMLPaths;
import yorickbm.guilibrary.GUILibraryRegistry;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.commands.interfaces.OverWorldCommandStack;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.permissions.PermissionManager;
import yorickbm.skyblockaddon.util.ChunkClearer;
import yorickbm.skyblockaddon.util.FunctionRegistry;
import yorickbm.skyblockaddon.util.ResourceManager;
import yorickbm.skyblockaddon.util.UsernameCache;

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
            final List<UUID> purgable = cap.getPurgableIslands();

            //Register purge function into registry
            final UUID functionKey = UUID.randomUUID();
            FunctionRegistry.registerFunction(functionKey, (e) -> {

                final List<BoundingBox> boundingBoxes = purgable.stream()
                        // Get Island objects for each UUID
                        .map(cap::getIslandByUUID)
                        // Filter only the abandoned islands
                        .filter(Island::isAbandoned)
                        // Get the bounding box for each abandoned island
                        .map(Island::getIslandBoundingBox)
                        // Collect all bounding boxes into a list
                        .collect(Collectors.toList());

                // Once all bounding boxes are collected, pass them to the clearBlocksInBoundingBoxes method
                final ChunkClearer destroyer = new ChunkClearer();
                destroyer.clearBlocksInBoundingBoxes(executor.getLevel(), boundingBoxes);

                executor.sendMessage(
                    new TextComponent(
                            SkyBlockAddonLanguage.getLocalizedString("commands.admin.purge.done")
                                    .formatted((purgable.size() - cap.getPurgableIslands().size()) + ""))
                            .withStyle(ChatFormatting.GREEN),
                    executor.getUUID());
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
