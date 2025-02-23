package yorickbm.skyblockaddon.registries;

import net.minecraft.nbt.CompoundTag;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.List;
import java.util.UUID;

public class GroupMemberRegistry extends SkyblockAddonRegistry {

    private final List<UUID> members;
    private final IslandGroup group;

    public GroupMemberRegistry(final Island island, final UUID groupId) {
        this.group = island.getGroup(groupId);
        this.members = this.group.getMembers();
    }

    @Override
    public void getNextData(final CompoundTag tag) {
        final String username = UsernameCache.getBlocking(this.members.get(this.index));

        tag.putString("SkullOwner", username);
        tag.putString("owner_name", username);
        tag.putString("group_name", group.getItem().getDisplayName().getString().trim());
        tag.putUUID("player_id", this.members.get(this.index));

        this.index++;
    }

    @Override
    public int getSize() {
        return this.members.size();
    }
}
