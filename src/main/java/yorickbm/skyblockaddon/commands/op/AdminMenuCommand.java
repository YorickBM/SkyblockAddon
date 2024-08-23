package yorickbm.skyblockaddon.commands.op;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.commands.interfaces.OverWorldCommandStack;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.gui.GUIManager;
import yorickbm.skyblockaddon.islands.Island;

import java.util.UUID;

public class AdminMenuCommand extends OverWorldCommandStack {
    public AdminMenuCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
                .then(Commands.literal("admin")
                        .requires(source -> source.getEntity() instanceof ServerPlayer && source.hasPermission(Commands.LEVEL_ADMINS))
                        .then(Commands.literal("menu")
                                .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity(), null))
                                .then(Commands.argument("id", UuidArgument.uuid())
                                        .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity(), UuidArgument.getUuid(context, "id")))
                                )
                        )
                )
        );
    }

    public int execute(CommandSourceStack command, ServerPlayer executor, UUID islandId) {
        if(super.execute(command, executor) == 0) return Command.SINGLE_SUCCESS;

        command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = null;

            if (islandId == null) island = cap.getIslandPlayerIsStandingOn(executor);
            else island = cap.getIslandByUUID(islandId);

            if (island == null) {
                if (islandId == null)
                    command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.admin.no.island")));
                else
                    command.sendFailure(new TextComponent(String.format(SkyBlockAddonLanguage.getLocalizedString("commands.admin.island.not.found.uuid"), islandId.toString())));
                return;
            }
            GUIManager.getInstance().openMenu("overview", executor, island);
        });
        return Command.SINGLE_SUCCESS;
    }
}
