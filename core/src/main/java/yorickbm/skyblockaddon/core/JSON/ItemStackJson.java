package yorickbm.skyblockaddon.core.JSON;

import com.google.gson.Gson;
import yorickbm.skyblockaddon.core.util.JSON.JSONSerializable;

public class ItemStackJson implements JSONSerializable {

    public static class ConditionalLoreEntry {
        public String condition;
        public String[] line;
    }

    protected String[] display_name;
    protected String item;
    protected String[][] lore;
    protected ConditionalLoreEntry[] conditional_lore;

    @Override
    public String toJSON() {
        final Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(final String json) {
        final Gson gson = new Gson();
        final ItemStackJson temp = gson.fromJson(json, ItemStackJson.class);

        this.display_name = temp.display_name;
        this.item = temp.item;
        this.lore = temp.lore;
        this.conditional_lore = temp.conditional_lore;
    }

    public String getItem() {
        return item;
    }

    public String[] getDisplay_name() {
        return display_name;
    }

    public String[][] getLore() {
        return lore;
    }

    public ConditionalLoreEntry[] getConditionalLore() {
        return conditional_lore;
    }
}
