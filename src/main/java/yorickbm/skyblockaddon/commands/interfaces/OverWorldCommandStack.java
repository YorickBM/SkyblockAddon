package yorickbm.skyblockaddon.commands.interfaces;

import com.mojang.brigadier.Command;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;

public abstract class OverWorldCommandStack implements CommandStack {
    public int execute(final CommandSourceStack command, final ServerPlayer executor) {
        if (executor.level.dimension() != Level.OVERWORLD) {
            executor.sendMessage(
                    new TextComponent(
                            SkyBlockAddonLanguage.getLocalizedString("commands.not.in.overworld")
                    ).withStyle(ChatFormatting.RED),
                    executor.getUUID());
            return 0;
        }

        return Command.SINGLE_SUCCESS;
    }
}
