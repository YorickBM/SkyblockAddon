package yorickbm.skyblockaddon.commands.op;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.guilibrary.GUILibraryRegistry;
import yorickbm.skyblockaddon.commands.interfaces.Cmds;
import yorickbm.skyblockaddon.core.SkyblockAddonCore;
import yorickbm.skyblockaddon.core.permissions.PermissionManager;
import yorickbm.skyblockaddon.core.registries.CategoryRegistry;
import yorickbm.skyblockaddon.core.registries.PermissionGroupRegistry;
import yorickbm.skyblockaddon.core.util.ResourceManager;
import yorickbm.skyblockaddon.core.util.RegistrySelector;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Predicate;

public class ConfigReloadCommand {
    private static final Logger LOGGER = LogManager.getLogger();

    public ConfigReloadCommand(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Cmds.literal("island")
            .then(Cmds.literal("admin")
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Cmds.literal("reload")
                        .executes(context -> execute(context.getSource()))
                )
            )
        );
    }

    public int execute(final CommandSourceStack command) {
        final Predicate<String> isModLoaded = mod -> ModList.get().isLoaded(mod);
        final Path configDir = FMLPaths.CONFIGDIR.get();
        final Path modDir = configDir.resolve(SkyblockAddonCore.MOD_ID);

        // Re-run resource extraction (language, configs, registries)
        ResourceManager.commonSetup(configDir, new RegistrySelector(Map.of()), isModLoaded);

        // Reload groups
        final Path groupsDir = modDir.resolve("registries/groups/");
        PermissionGroupRegistry.getInstance().clear();
        PermissionGroupRegistry.getInstance().loadFromDirectory(groupsDir, isModLoaded);

        // Reload permissions — new per-mod directory takes priority over legacy single file
        final Path newPermsDir  = modDir.resolve("registries/permissions/");
        final Path oldPermsFile = modDir.resolve("registries/PermissionRegistry.json");

        final int count;
        if (oldPermsFile.toFile().isFile()) {
            count = PermissionManager.getInstance().loadPermissions(oldPermsFile);
        } else {
            count = PermissionManager.getInstance().loadPermissions(newPermsDir, isModLoaded);
        }

        // Regenerate permissions.json GUI for current mod set, then reload all GUIs
        final Path categoriesPath = modDir.resolve("registries/categories.json");
        CategoryRegistry.getInstance().load(categoriesPath);
        CategoryRegistry.getInstance().generatePermissionsGui(modDir.resolve("guis/permissions.json"), isModLoaded);

        GUILibraryRegistry.registerFolder(SkyblockAddonCore.MOD_ID, modDir.resolve("guis/"));

        LOGGER.info("Reloaded {} permissions, {} GUIs.", count, GUILibraryRegistry.getGuis());
        command.sendSuccess(new TextComponent(
                "Reloaded " + count + " permissions and " + GUILibraryRegistry.getGuis() + " GUIs."
        ).withStyle(ChatFormatting.GREEN), true);

        return Command.SINGLE_SUCCESS;
    }
}
