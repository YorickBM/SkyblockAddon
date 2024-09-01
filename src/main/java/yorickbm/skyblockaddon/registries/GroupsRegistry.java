package yorickbm.skyblockaddon.registries;

import net.minecraft.nbt.CompoundTag;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;

public class GroupsRegistry extends SkyblockAddonRegistry {
    @Override
    public boolean getNextData(CompoundTag tag) {
        return false;
    }

    @Override
    public int getSize() {
        return 0;
    }
}
