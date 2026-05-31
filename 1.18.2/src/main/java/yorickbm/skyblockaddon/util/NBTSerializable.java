package yorickbm.skyblockaddon.util;

import net.minecraft.nbt.CompoundTag;
import yorickbm.skyblockaddon.core.persistence.Identifiable;

public interface NBTSerializable extends Identifiable {
    default CompoundTag serializeNBT() {
        return new CompoundTag();
    }

    void deserializeNBT(CompoundTag tag);
}
