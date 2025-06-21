package yorickbm.skyblockaddon.permissions.util;

import com.google.gson.Gson;
import yorickbm.guilibrary.GUIItemStackHolder;
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
        final Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(final String json) {
        final Gson gson = new Gson();
        final Permission temp = gson.fromJson(json, Permission.class);

        this.category = temp.category;
        this.item = temp.item;
        this.triggers = temp.triggers;
        this.data = temp.data;
    }

    public GUIItemStackHolder getItemStackHolder(final Island island, final UUID groupId) {
        final GUIItemStackHolder stack = this.item.getItemStackHolder();
        stack.putData("permission_id", this.id);
        stack.putData("category", this.category);
        stack.putData("group_name", island.getGroup(groupId).getItem().getDisplayName().getString());
        stack.putData("status", island.getPermissionState(this.id, groupId));
        return stack;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public boolean hasTrigger(final String trigger) {
        return Arrays.stream(triggers).toList().contains(trigger);
    }

    public PermissionDataHolder getData() { return this.data; }
}
