package yorickbm.skyblockaddon.registries;

import net.minecraft.nbt.CompoundTag;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MemberRegistry extends SkyblockAddonRegistry {

    protected final Map<UUID, UUID> members;
    private final List<Map.Entry<UUID, UUID>> entries;

    public MemberRegistry(Island island) {
        this.members = island.getMembers();
        this.entries =  new ArrayList<>(this.members.entrySet());
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
        Map.Entry<UUID, UUID> entry = this.entries.get(this.index);

        String username = UsernameCache.getBlocking(entry.getKey());

        tag.putString("SkullOwner", username);
        tag.putString("ownername", username);

        tag.putUUID("playerId", entry.getKey());
        tag.putUUID("groupId", entry.getValue());

        index++;
        return true;
    }

    @Override
    public int getSize() {
        return members.size();
    }
}
