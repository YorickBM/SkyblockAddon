package yorickbm.skyblockaddon.gui;

import net.minecraft.world.MenuProvider;

public interface SkyblockAddonMenuProvider extends MenuProvider {
    void setContext(GuiContext context);
}
