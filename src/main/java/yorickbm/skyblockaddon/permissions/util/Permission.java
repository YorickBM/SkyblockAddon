package yorickbm.skyblockaddon.permissions.util;

import com.google.gson.Gson;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import yorickbm.guilibrary.JSON.GUIItemStackJson;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.permissions.json.PermissionDataHolder;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;

import java.util.Arrays;
import java.util.UUID;

public class Permission implements JSONSerializable {

    protected String id;
    protected String category;
    protected GUIItemStackJson item;
    protected String[] triggers;
    protected PermissionDataHolder data;

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
        this.data = temp.data;
    }

    public ItemStack getItemStack(Island island, UUID groupId) {
        CompoundTag tag = new CompoundTag();
        tag.putString("permissionId", this.id);
        tag.putString("category", this.category);
        tag.putString("groupname", island.getGroup(groupId).getItem().getDisplayName().getString());
        tag.putString("status", island.getPermissionState(this.id, groupId));

        return this.item.getItemStackHolder().getItemStack();
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public boolean hasTrigger(String trigger) {
        return Arrays.stream(triggers).toList().contains(trigger);
    }

    public PermissionDataHolder getData() { return this.data; }
}
