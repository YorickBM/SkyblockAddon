package yorickbm.skyblockaddon.registries;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.registries.interfaces.CustomItemStack;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;

import java.util.ArrayList;
import java.util.List;

public class GroupsRegistry extends SkyblockAddonRegistry implements CustomItemStack {

    private final List<IslandGroup> groups;
    private final Island island;

    public GroupsRegistry(Island island) {
        groups = new ArrayList<>(island.getGroups());
        this.island = island;
    }

    @Override
    public void getNextData(CompoundTag tag) {
        tag.putUUID("groupId", this.groups.get(this.index).getId());
        this.index++;
    }

    @Override
    public int getSize() {
        return this.groups.size();
    }

    @Override
    public ItemStack getItemFor(CompoundTag tag) {
        if(!tag.contains("groupId")) return null;
        IslandGroup group = island.getGroup(tag.getUUID("groupId"));
        if(group == null) return null;

        return group.getItem();
    }
}
