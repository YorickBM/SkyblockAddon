package yorickbm.skyblockaddon.gui.interfaces;

import net.minecraft.world.MenuProvider;
import yorickbm.skyblockaddon.islands.Island;

public interface SkyblockAddonMenuProvider extends MenuProvider {
    void setContext(Island context);
}
