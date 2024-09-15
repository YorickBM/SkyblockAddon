package yorickbm.skyblockaddon.permissions.json;

import com.google.gson.Gson;
import yorickbm.skyblockaddon.permissions.util.Permission;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;

import java.util.ArrayList;
import java.util.List;

public class PermissionHolder implements JSONSerializable {
    public List<Permission> permissions;

    public PermissionHolder() {
        this.permissions = new ArrayList<>();
    }


    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(String json) {
        Gson gson = new Gson();
        PermissionHolder temp = gson.fromJson(json, PermissionHolder.class);

        this.permissions = temp.permissions;
    }
}
