package yorickbm.skyblockaddon.registries;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.permissions.PermissionManager;
import yorickbm.skyblockaddon.permissions.util.Permission;
import yorickbm.skyblockaddon.registries.interfaces.CustomItemStack;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;

import java.util.List;
import java.util.UUID;

public class PermissionRegistry extends SkyblockAddonRegistry implements CustomItemStack {

    List<Permission> permissions;
    Island island;
    UUID groupId;

    public PermissionRegistry(Island island, String category, UUID groupId) {
        permissions = PermissionManager.getInstance().getPermissionsFor(category);
        this.island = island;
        this.groupId = groupId;
    }

    @Override
    public void getNextData(CompoundTag tag) {
        tag.putInt("permission", index);
        index++;
    }

    @Override
    public int getSize() {
        return this.permissions.size();
    }

    @Override
    public ItemStack getItemFor(CompoundTag tag) {
        Permission permission = this.permissions.get(tag.getInt("permission"));
        ItemStack stack = permission.getItemStack(this.island, this.groupId);
        stack.getOrCreateTagElement(SkyblockAddon.MOD_ID).putString("permission_id", permission.getId());
        stack.getOrCreateTagElement(SkyblockAddon.MOD_ID).putUUID("group_id", this.groupId);
        return stack;
    }
}
