package yorickbm.skyblockaddon.islands;

import net.minecraft.nbt.CompoundTag;
import yorickbm.skyblockaddon.util.SupportsEncoder;

import java.util.UUID;

public class Island implements SupportsEncoder {
    public UUID getId() {
        return UUID.randomUUID();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        return tag;
    }

    @Override
    public CompoundTag deserializeNBT(CompoundTag nbt) {
        return nbt;
    }
}
