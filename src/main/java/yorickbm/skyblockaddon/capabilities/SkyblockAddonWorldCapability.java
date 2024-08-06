package yorickbm.skyblockaddon.capabilities;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;

public class SkyblockAddonWorldCapability {
    public void saveNBTData(CompoundTag nbt) {
    }

    public void loadNBTData(CompoundTag nbt) {
    }

    /**
     * Get next Vec3i for an island based on snake pattern island generation algorithm
     * @param location Last island location
     * @return Location for next island
     */
    private Vec3i nextGridLocation(final Vec3i location) {
        final int x = location.getX();
        final int z = location.getZ();
        final int d = SkyblockAddon.ISLAND_SIZE * 2 + SkyblockAddon.ISLAND_BUFFER;

        if(x < z) {
            if(-1 * x < z) return new Vec3i(x + d, 0, z);
            return new Vec3i(x, 0, z + d);
        }

        if(x > z) {
            if(-1 * x >= z)  return new Vec3i(x - d, 0, z);
            return new Vec3i(x, 0, z - d);
        }

        if( x <= 0) return new Vec3i(x, 0, z + d);
        return new Vec3i(x, 0, z - d);
    }
}
