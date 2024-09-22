package yorickbm.skyblockaddon.registries;

import net.minecraft.nbt.CompoundTag;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MemberRegistry extends SkyblockAddonRegistry {

    private final List<UUID> members;
    private final Island island;

    public MemberRegistry(Island island) {
        this.members = island.getMembers();
        this.island = island;
    }

    /**
     * Load NBT data into tag for current index
     *
     * @param tag - CompoundTag to fill
     * @return - If there is another entry
     */
    @Override
    public boolean getNextData(CompoundTag tag) {
        if(this.index >= this.getSize()) return false;

        String username = UsernameCache.getBlocking(this.members.get(this.index));

        tag.putString("SkullOwner", username);
        tag.putString("ownername", username);

        Optional<IslandGroup> group = island.getGroupForEntityUUID(this.members.get(this.index));
        group.ifPresentOrElse(
                islandGroup -> tag.putString("group", islandGroup.getItem().getDisplayName().getString().trim()),
                () -> tag.putString("group", "N/A"));

        tag.putUUID("playerId", this.members.get(this.index));

        this.index++;
        return true;
    }

    @Override
    public int getSize() {
        return members.size();
    }
}
