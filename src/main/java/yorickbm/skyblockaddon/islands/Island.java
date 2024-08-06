package yorickbm.skyblockaddon.islands;

import net.minecraft.nbt.CompoundTag;
import yorickbm.skyblockaddon.util.NBT.IsUnique;
import yorickbm.skyblockaddon.util.NBT.NBTSerializable;

import java.util.UUID;

public class Island implements IsUnique, NBTSerializable {
    public UUID getId() {
        return UUID.randomUUID();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
    }
}
