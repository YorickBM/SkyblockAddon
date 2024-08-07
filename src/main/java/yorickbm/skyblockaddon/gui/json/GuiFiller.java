package yorickbm.skyblockaddon.gui.json;

import com.google.gson.Gson;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;

public class GuiFiller implements JSONSerializable {
    private GuiItemHolder item;
    private String pattern;

    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(String json) {
        Gson gson = new Gson();
        GuiFiller temp = gson.fromJson(json, GuiFiller.class);
        this.item = temp.item;
        this.pattern = temp.pattern;
    }
}
