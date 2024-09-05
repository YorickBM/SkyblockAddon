package yorickbm.skyblockaddon.gui.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import yorickbm.skyblockaddon.islands.Island;

public interface SkyblockAddonMenuProvider extends MenuProvider {
    void setContext(Island context);
    void setNBT(CompoundTag nbt);
}
