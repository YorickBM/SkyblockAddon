package yorickbm.skyblockaddon.gui.util;

import net.minecraft.world.MenuProvider;

public interface SkyblockAddonMenuProvider extends MenuProvider {
    void setContext(GuiContext context);
}
