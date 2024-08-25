package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.commands.interfaces.OverWorldCommandStack;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;

public class IslandCreateCommand extends OverWorldCommandStack {
    public IslandCreateCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
            .then(Commands.literal("create")
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
            if(island != null) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.create.already")));
                return;
            }

            Thread asyncIslandGen = new Thread(() -> {
                Vec3i vec = cap.genIsland(command.getLevel());
                if(vec == null) {
                    command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.create.failure")));
                    return;
                }

                Island newIsland = new Island(executor.getUUID(), vec);
                cap.registerIsland(newIsland, executor.getUUID());

                executor.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.create.success")).withStyle(ChatFormatting.GREEN), executor.getUUID());
                newIsland.teleportTo(executor);
            });
            executor.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.create.generating")).withStyle(ChatFormatting.GREEN), executor.getUUID());
            asyncIslandGen.start();
        });

        return Command.SINGLE_SUCCESS;
    }
}
