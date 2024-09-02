package yorickbm.skyblockaddon.registries;

import net.minecraft.nbt.CompoundTag;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;

import java.util.ArrayList;
import java.util.List;

public class GroupsRegistry extends SkyblockAddonRegistry {

    protected final List<IslandGroup> groups;

    public GroupsRegistry(Island island) {
        groups = new ArrayList<>(island.getGroups());
    }

    @Override
    public boolean getNextData(CompoundTag tag) {
        if(this.index >= this.getSize()) return false;

        tag.putUUID("groupId", this.groups.get(this.index).getId());

        return ++this.index < this.getSize();
    }

    @Override
    public int getSize() {
        return this.groups.size();
    }
}
