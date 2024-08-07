package yorickbm.skyblockaddon.islands;

import net.minecraft.nbt.CompoundTag;
import yorickbm.skyblockaddon.islands.data.IslandData;
import yorickbm.skyblockaddon.util.NBT.IsUnique;
import yorickbm.skyblockaddon.util.NBT.NBTSerializable;

import java.util.UUID;

public class Island extends IslandData implements IsUnique, NBTSerializable {
    CompoundTag legacyDataOnlyHereWhileTesting;

    public Island() {
    }

    public boolean isPartOf(UUID player) {
        return isOwner(player);
    }

    public boolean isOwner(UUID uuid) {
        return getOwner().equals(uuid);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.put("permissions", legacyDataOnlyHereWhileTesting);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        legacyDataOnlyHereWhileTesting = nbt.getCompound("permissions");
    }
}
