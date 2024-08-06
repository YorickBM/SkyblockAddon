package yorickbm.skyblockaddon.util.NBT;

import net.minecraft.nbt.CompoundTag;

public interface NBTSerializable {
    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}
