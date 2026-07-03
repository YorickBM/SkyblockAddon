package yorickbm.skyblockaddon.core.permissions;

import com.google.gson.Gson;
import yorickbm.skyblockaddon.core.JSON.ItemStackJson;
import yorickbm.skyblockaddon.core.JSON.PermissionDataJson;
import yorickbm.skyblockaddon.core.util.JSON.JSONSerializable;

public class Permission implements JSONSerializable {

    protected String id;
    protected ItemStackJson item;
    protected String category;
    protected String[] triggers;
    protected PermissionDataJson data;
    protected int priority;
    protected int order;

    public Permission() {
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }

    public void fromJSON(String json) {
        Gson gson = new Gson();
        Permission parsed = gson.fromJson(json, Permission.class);
        this.id = parsed.id;
        this.item = parsed.item;
        this.category = parsed.category;
        this.triggers = parsed.triggers;
        this.data = parsed.data;
        this.priority = parsed.priority;
        this.order = parsed.order;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public ItemStackJson getItem() {
        return item;
    }

    public boolean hasTrigger(String trigger) {
        if (triggers == null) return false;
        for (String t : triggers) {
            if (t.equals(trigger)) return true;
        }
        return false;
    }

    public PermissionDataJson getData() {
        return data;
    }

    public int getPriority() {
        return priority;
    }

    public int getOrder() {
        return order;
    }
}