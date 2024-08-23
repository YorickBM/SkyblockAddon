package yorickbm.skyblockaddon.commands.interfaces;

import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;

public abstract class OverWorldCommandStack implements CommandStack {
    public int execute(CommandSourceStack command, ServerPlayer executor) {
        if (executor.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.not.in.overworld")));
            return 0;
        }

        return Command.SINGLE_SUCCESS;
    }
}
