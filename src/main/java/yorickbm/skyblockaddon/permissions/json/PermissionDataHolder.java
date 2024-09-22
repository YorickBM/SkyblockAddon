package yorickbm.skyblockaddon.permissions.json;

import com.google.gson.Gson;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;

import java.util.Arrays;
import java.util.List;

public class PermissionDataHolder implements JSONSerializable {

    protected String[] skyblockaddon;
    protected String[] items;
    protected String[] blocks;
    protected String[] entities;
    protected boolean stacked = false;

    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(String json) {
        Gson gson = new Gson();
        PermissionDataHolder temp = gson.fromJson(json, PermissionDataHolder.class);

        this.skyblockaddon = temp.skyblockaddon;
        this.items = temp.items;
        this.blocks = temp.blocks;
        this.entities = temp.entities;
        this.stacked = temp.stacked;
    }

    public List<String> getSkyblockaddonData() { return Arrays.stream(this.skyblockaddon).toList(); }
    public List<String> getItemsData() { return Arrays.stream(this.items).toList(); }
    public List<String> getBlocksData() { return Arrays.stream(this.blocks).toList(); }
    public List<String> getEntitiesData() { return Arrays.stream(this.entities).toList(); }
    public boolean isStacked() { return this.stacked; }
}
