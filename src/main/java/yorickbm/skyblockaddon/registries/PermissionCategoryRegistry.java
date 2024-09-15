package yorickbm.skyblockaddon.registries;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.permissions.PermissionManager;
import yorickbm.skyblockaddon.permissions.util.PermissionCategory;
import yorickbm.skyblockaddon.registries.interfaces.CustomItemStack;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;

import java.util.UUID;

public class PermissionCategoryRegistry extends SkyblockAddonRegistry implements CustomItemStack {

    Island island;
    UUID groupId;

    public PermissionCategoryRegistry(Island island, UUID groupId) {
        this.island = island;
        this.groupId = groupId;
    }

    @Override
    public boolean getNextData(CompoundTag tag) {
        if(this.index >= this.getSize()) return false;
        tag.putInt("categoryId", index);

        index++;
        return true;
    }

    @Override
    public int getSize() {
        return PermissionManager.getInstance().getCategories().size();
    }

    @Override
    public ItemStack getItemFor(CompoundTag tag) {
        PermissionCategory category = PermissionManager.getInstance().getCategories().get(tag.getInt("categoryId"));
        return category.getItemStack();
    }
}
