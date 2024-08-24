package yorickbm.skyblockaddon.commands;

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

import java.util.UUID;

public class IslandLeaveCommand extends OverWorldCommandStack {

    public IslandLeaveCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
            .then(Commands.literal("leave")
                .requires(source -> source.getEntity() instanceof ServerPlayer)
                .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity()))
            )
        );
    }

    @Override
    public int execute(CommandSourceStack command, ServerPlayer executor) {
        if(super.execute(command, executor) == 0) return Command.SINGLE_SUCCESS;

        command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByEntityUUID(executor.getUUID());
            if (island == null) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.has.no.island")));
                return;
            }

            UUID functionKey = UUID.randomUUID();
            FunctionRegistry.registerFunction(functionKey, (e) -> {
                if(super.execute(command, e) == 0) return;

                e.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.leave.undo")).withStyle(ChatFormatting.GREEN), e.getUUID());
                if(!island.addMember(e, e.getUUID())) {
                    e.sendMessage(
                        new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.leave.undo.failure").formatted(island.getId()))
                        .withStyle(ChatFormatting.RED)
                        .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, island.getId().toString())))
                    , e.getUUID());
                }
            }, 15);

            if(!island.kickMember(executor, executor.getUUID())) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.leave.failure")));
                return;
            }

            cap.clearCacheForPlayer(executor.getUUID());

            command.sendSuccess(
                new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.leave"))
                    .withStyle(ChatFormatting.GREEN)
                    .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, FunctionRegistry.getCommand(functionKey))))
                , false);
        });

        return Command.SINGLE_SUCCESS;
    }

}
