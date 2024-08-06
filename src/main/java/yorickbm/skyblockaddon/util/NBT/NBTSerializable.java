package yorickbm.skyblockaddon.util.NBT;

import net.minecraft.nbt.CompoundTag;

public interface NBTSerializable {
    default CompoundTag serializeNBT() {
        return new CompoundTag();
    }
    void deserializeNBT(CompoundTag tag);
}
