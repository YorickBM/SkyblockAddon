package yorickbm.skyblockaddon.gui.json;

import com.google.gson.Gson;
import yorickbm.skyblockaddon.gui.util.FillerPattern;
import yorickbm.skyblockaddon.gui.util.GuiActionable;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;

public class GuiFiller extends GuiActionable implements JSONSerializable {
    private FillerPattern pattern;

    public FillerPattern getPattern() { return pattern; }

    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(String json) {
        Gson gson = new Gson();
        GuiFiller temp = gson.fromJson(json, GuiFiller.class);
        super.item = temp.item;
        this.pattern = temp.pattern;
        super.action = temp.action;
    }
}
