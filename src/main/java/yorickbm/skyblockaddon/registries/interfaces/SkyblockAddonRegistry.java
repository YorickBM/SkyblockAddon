package yorickbm.skyblockaddon.registries.interfaces;

import net.minecraft.nbt.CompoundTag;

public abstract class SkyblockAddonRegistry {
    protected int index = 0;

    public abstract boolean getNextData(CompoundTag tag);
    public abstract int getSize();

    public void setIndex(int number) {
        if(number < 0) {
            this.index = 0;
        }
        else if (number > this.getSize()) {
            this.index = this.getSize()-1;
        }
        else {
            this.index = number;
        }
    }

    public int getIndex() { return this.index; }
}
