package yorickbm.skyblockaddon.permissions.util;

import com.google.gson.Gson;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import yorickbm.skyblockaddon.gui.json.GuiItemHolder;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;

import java.util.UUID;

public class Permission implements JSONSerializable {

    protected String id;
    protected String category;
    protected GuiItemHolder item;
    protected String[] triggers;

    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(String json) {
        Gson gson = new Gson();
        Permission temp = gson.fromJson(json, Permission.class);

        this.category = temp.category;
        this.item = temp.item;
        this.triggers = temp.triggers;
    }

    public ItemStack getItemStack(Island island, UUID groupId) {
        CompoundTag tag = new CompoundTag();
        tag.putString("permissionId", this.id);
        tag.putString("category", this.category);
        tag.putString("group", island.getGroup(groupId).getItem().getDisplayName().getContents());
        tag.putString("status", island.getPermissionState(this.id, groupId));

        return this.item.getItemStack(island, tag);
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }
}
