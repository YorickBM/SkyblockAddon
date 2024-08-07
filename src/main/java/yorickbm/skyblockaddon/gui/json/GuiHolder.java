package yorickbm.skyblockaddon.gui.json;

import com.google.gson.Gson;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;

import java.util.List;

public class GuiHolder implements JSONSerializable {
    private String title;
    private int rows;
    private String key;
    private List<GuiFiller> fillers;
    private List<GuiItem> items;

    public TextComponent getTitle() {
        return (TextComponent) Component.Serializer.fromJson(title);
    }

    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(String json) {
        Gson gson = new Gson();
        GuiHolder temp = gson.fromJson(json, GuiHolder.class);
        this.title = temp.title;
        this.rows = temp.rows;
        this.key = temp.key;
        this.fillers = temp.fillers;
        this.items = temp.items;
    }

}
