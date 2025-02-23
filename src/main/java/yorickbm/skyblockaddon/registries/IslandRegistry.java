package yorickbm.skyblockaddon.registries;

import net.minecraft.nbt.CompoundTag;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.SkyblockAddonWorldCapability;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.data.IslandData;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class IslandRegistry extends SkyblockAddonRegistry {
    protected final List<Island> islands;

    public IslandRegistry(SkyblockAddonWorldCapability cap) {
        this.islands = cap.getIslands().stream()
                .filter(IslandData::isVisible) //Only public islands
                .filter(i -> !Objects.equals(i.getOwner(), SkyblockAddon.MOD_UUID)) //No islands with no owner
                .sorted(Comparator.comparing(IslandData::getName)) //Sort alphabetically
                .toList();
    }

    @Override
    public void getNextData(CompoundTag tag) {
        Island island = islands.get(this.index);
        String username = UsernameCache.getBlocking(island.getOwner());

        tag.putString("SkullOwner", username);
        tag.putString("owner_name", username);

        tag.putString("biome", island.getBiome());

        tag.putUUID("island_id", island.getId());
        tag.putUUID("player_id", island.getOwner());

        index++;
    }

    @Override
    public int getSize() {
        return islands.size();
    }
}
