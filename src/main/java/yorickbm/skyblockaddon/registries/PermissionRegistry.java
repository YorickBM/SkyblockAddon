package yorickbm.skyblockaddon.registries;

import net.minecraft.nbt.CompoundTag;
import yorickbm.guilibrary.GUIItemStackHolder;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.permissions.PermissionManager;
import yorickbm.skyblockaddon.permissions.util.Permission;
import yorickbm.skyblockaddon.registries.interfaces.CustomItemHolder;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;

import java.util.List;
import java.util.UUID;

public class PermissionRegistry extends SkyblockAddonRegistry implements CustomItemHolder {

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
    public GUIItemStackHolder getItemFor(CompoundTag tag) {
        Permission permission = this.permissions.get(tag.getInt("permission"));
        return permission.getItemStackHolder(this.island, this.groupId);
    }
}
