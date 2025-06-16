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

    public MemberRegistry(final Island island) {
        this.members = island.getMembers();
        this.island = island;
    }

    /**
     * Load NBT data into tag for current index
     *
     * @param tag - CompoundTag to fill
     */
    @Override
    public void getNextData(final CompoundTag tag) {
        if(this.index >= this.getSize()) return;

        final String username = UsernameCache.getBlocking(this.members.get(this.index));

        tag.putString("SkullOwner", username);
        tag.putString("owner_name", username);

        final Optional<IslandGroup> group = island.getGroupForEntityUUID(this.members.get(this.index));
        group.ifPresentOrElse(
                islandGroup -> tag.putString("group_name", islandGroup.getItem().getDisplayName().getString().trim()),
                () -> tag.putString("group_name", "N/A"));

        tag.putUUID("player_id", this.members.get(this.index));

        this.index++;
    }

    @Override
    public int getSize() {
        return members.size();
    }
}
