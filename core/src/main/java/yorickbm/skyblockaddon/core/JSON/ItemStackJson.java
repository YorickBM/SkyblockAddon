package yorickbm.skyblockaddon.core.JSON;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import yorickbm.skyblockaddon.core.util.JSON.JSONSerializable;

import java.util.List;

public class ItemStackJson implements JSONSerializable {

    protected String[] display_name;
    protected String item;
    protected List<LoreLineJson> lore;

    public ItemStackJson() {
    }

    public String toJSON() {
        return gson().toJson(this);
    }

    public void fromJSON(String json) {
        Gson gson = gson();
        ItemStackJson parsed = gson.fromJson(json, ItemStackJson.class);
        this.display_name = parsed.display_name;
        this.item = parsed.item;
        this.lore = parsed.lore;
    }

    public String getItem() {
        return item;
    }

    public String[] getDisplay_name() {
        return display_name;
    }

    public List<LoreLineJson> getLore() {
        return lore;
    }

    private static Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(LoreLineJson.class, new LoreLineDeserializer())
                .create();
    }
}