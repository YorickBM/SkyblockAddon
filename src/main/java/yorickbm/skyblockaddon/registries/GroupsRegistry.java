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
    public boolean getNextData(CompoundTag tag) {
        if(this.index >= this.getSize()) return false;

        tag.put("group", this.groups.get(this.index).serializeNBT());

        this.index++;
        return true;
    }

    @Override
    public int getSize() {
        return this.groups.size();
    }

    @Override
    public ItemStack getItemFor(CompoundTag tag) {
        IslandGroup group = new IslandGroup();
        group.deserializeNBT(tag.getCompound("group"));

        return group.getItem();
    }
}
