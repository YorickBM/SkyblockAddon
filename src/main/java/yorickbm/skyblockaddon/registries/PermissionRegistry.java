package yorickbm.skyblockaddon.registries;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
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
    public boolean getNextData(CompoundTag tag) {
        if(this.index >= this.getSize()) return false;
        tag.putInt("permission", index);

        index++;
        return true;
    }

    @Override
    public int getSize() {
        return this.permissions.size();
    }

    @Override
    public ItemStack getItemFor(CompoundTag tag) {
        Permission permission = this.permissions.get(tag.getInt("permission"));
        ItemStack stack = permission.getItemStack();

        ListTag lore = (ListTag) stack.getOrCreateTagElement("display").get("Lore");
        ListTag lore2 = new ListTag();

        lore.forEach(l -> {
            Component comp = Component.Serializer.fromJson(l.getAsString());
            if(comp == null) return; //Failure to create component from lore value

            String newContent = comp.getContents()
                    .replace("permission_category", permission.getCategory())
                    .replace("permission_status", this.island.getPermissionState(permission.getId(), this.groupId))
                    //.replace("permission_group", this.island.getGroup(this.groupId).getItem().getDisplayName().getContents())
                    ;

            lore2.add(StringTag.valueOf(Component.Serializer.toJson(new TextComponent(newContent).withStyle(comp.getStyle()))));
        });

        stack.getOrCreateTagElement("display").put("Lore", lore2);

        return stack;
    }
}
