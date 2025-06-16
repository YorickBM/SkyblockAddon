package yorickbm.skyblockaddon.commands.op;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fml.loading.FMLPaths;
import yorickbm.guilibrary.GUILibraryRegistry;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.permissions.PermissionManager;
import yorickbm.skyblockaddon.util.ResourceManager;

public class ConfigReloadCommand {
    public ConfigReloadCommand(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
            .then(Commands.literal("admin")
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("reload")
                        .executes(context -> execute(context.getSource()))
                )
            )
        );
    }

    public int execute(final CommandSourceStack command) {
        ResourceManager.commonSetup();
        PermissionManager.getInstance().loadPermissions();

        GUILibraryRegistry.registerFolder(SkyblockAddon.MOD_ID, FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/guis/"));

        command.sendSuccess(new TextComponent("Configuration files have been reloaded!").withStyle(ChatFormatting.GREEN), true);
        return Command.SINGLE_SUCCESS;
    }
}
