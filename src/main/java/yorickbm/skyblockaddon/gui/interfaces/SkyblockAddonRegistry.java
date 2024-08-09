package yorickbm.skyblockaddon.gui.interfaces;

import net.minecraft.nbt.CompoundTag;

public abstract class SkyblockAddonRegistry {
    protected int index = 0;

    public abstract boolean getNextData(CompoundTag tag);
    public abstract int getSize();

    public void setIndex(int number) {
        index = number;
    }
}
