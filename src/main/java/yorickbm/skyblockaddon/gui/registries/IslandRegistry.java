package yorickbm.skyblockaddon.gui.registries;

import net.minecraft.nbt.CompoundTag;
import yorickbm.skyblockaddon.capabilities.SkyblockAddonWorldCapability;
import yorickbm.skyblockaddon.gui.interfaces.SkyblockAddonRegistry;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.data.IslandData;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.List;

public class IslandRegistry extends SkyblockAddonRegistry {

    protected final List<Island> islands;

    public IslandRegistry(SkyblockAddonWorldCapability cap) {
        islands = cap.getIslands().stream().filter(IslandData::isVisible).toList();
    }

    @Override
    public boolean getNextData(CompoundTag tag) {
        if(this.index >= this.getSize()) return false;
        Island island = islands.get(this.index);
        String username = UsernameCache.getBlocking(island.getOwner());

        tag.putString("SkullOwner", username);
        tag.putString("ownername", username);
        tag.putString("biome", island.getBiome());
        index++;
        return true;
    }

    @Override
    public int getSize() {
        return islands.size();
    }
}
