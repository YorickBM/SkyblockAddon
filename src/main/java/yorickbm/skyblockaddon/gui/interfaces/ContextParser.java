package yorickbm.skyblockaddon.gui.interfaces;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public interface ContextParser {
    @NotNull Component parseTextComponent(@NotNull Component original);
}
