package yorickbm.skyblockaddon.registries;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.registries.interfaces.CustomItemStack;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;

import java.util.ArrayList;
import java.util.List;

public class GroupsRegistry extends SkyblockAddonRegistry implements CustomItemStack {

    private final List<IslandGroup> groups;
    private final Island island;

    public GroupsRegistry(final Island island) {
        groups = new ArrayList<>(island.getGroups());
        this.island = island;
    }

    @Override
    public void getNextData(final CompoundTag tag) {
        tag.putUUID("group_id", this.groups.get(this.index).getId());
        this.index++;
    }

    @Override
    public int getSize() {
        return this.groups.size();
    }

    @Override
    public ItemStack getItemFor(final CompoundTag tag) {
        if(!tag.contains("group_id")) return null;
        final IslandGroup group = island.getGroup(tag.getUUID("group_id"));
        if(group == null) return null;

        final ItemStack stack = group.getItem();
        stack.getOrCreateTag().put(SkyblockAddon.MOD_ID, tag);
        return stack;
    }
}
