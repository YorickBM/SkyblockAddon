package yorickbm.skyblockaddon.core.JSON;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import yorickbm.skyblockaddon.core.permissions.Permission;
import yorickbm.skyblockaddon.core.util.JSON.JSONSerializable;

import java.util.List;

public class PermissionJson implements JSONSerializable {

    public String mod;
    public List<Permission> permissions;

    public PermissionJson() {
    }

    public String toJSON() {
        return gson().toJson(this);
    }

    public void fromJSON(String json) {
        PermissionJson parsed = gson().fromJson(json, PermissionJson.class);
        this.mod = parsed.mod;
        this.permissions = parsed.permissions;
    }

    private static Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(LoreLineJson.class, new LoreLineDeserializer())
                .create();
    }
}