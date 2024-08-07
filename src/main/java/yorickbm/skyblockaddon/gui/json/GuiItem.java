package yorickbm.skyblockaddon.gui.json;

import com.google.gson.Gson;
import yorickbm.skyblockaddon.gui.util.GuiActionable;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;

public class GuiItem extends GuiActionable implements JSONSerializable {
    private int slot;

    public int getSlot() { return slot; }
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
        this.action = temp.action;
    }
}
