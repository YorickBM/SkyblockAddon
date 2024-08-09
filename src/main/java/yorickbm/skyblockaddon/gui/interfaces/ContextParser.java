package yorickbm.skyblockaddon.gui.interfaces;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public interface ContextParser {
    Component parseTextComponent(@NotNull Component original);
}
