package yorickbm.skyblockaddon.util;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public interface SupportsEncoder {
    CompoundTag serializeNBT();
    CompoundTag deserializeNBT(CompoundTag nbt);

    UUID getId();
}
