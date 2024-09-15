package yorickbm.skyblockaddon.permissions.json;

import com.google.gson.Gson;
import yorickbm.skyblockaddon.permissions.util.PermissionCategory;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;

import java.util.ArrayList;
import java.util.List;

public class PermissionCategoryHolder implements JSONSerializable {
    public List<PermissionCategory> categories;

    public PermissionCategoryHolder() {
        this.categories = new ArrayList<>();
    }


    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(String json) {
        Gson gson = new Gson();
        PermissionCategoryHolder temp = gson.fromJson(json, PermissionCategoryHolder.class);

        this.categories = temp.categories;
    }
}
