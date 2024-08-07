package yorickbm.skyblockaddon.gui.json;

import com.google.gson.Gson;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;

public class GuiItem implements JSONSerializable {
    private GuiItemHolder item;
    private int slot;

    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(String json) {
        Gson gson = new Gson();
        GuiItem temp = gson.fromJson(json, GuiItem.class);
        this.item = temp.item;
        this.slot = temp.slot;
    }
}
