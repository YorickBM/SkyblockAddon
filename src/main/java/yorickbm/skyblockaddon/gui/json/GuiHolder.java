package yorickbm.skyblockaddon.gui.json;

import com.google.gson.Gson;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.w3c.dom.Text;
import yorickbm.skyblockaddon.gui.util.GuiContext;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;

import java.util.List;
import java.util.Objects;

public class GuiHolder implements JSONSerializable {
    private List<String> title;
    private int rows;
    private String key;
    private List<GuiFiller> fillers;
    private List<GuiItem> items;

    /**
     * Get Gui Items
     */
    public List<GuiItem> getItems() { return items; }

    /**
     * Get Gui Fillers
     */
    public List<GuiFiller> getFillers() { return fillers; }

    /**
     * Get GUI configured key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Convert string formated Text Component for GUI title.
     *
     * @return - TextComponent
     */
    public TextComponent getTitle(GuiContext context) {
        TextComponent component = new TextComponent("");
        for(String string : this.title) {
            Component deserialized = Component.Serializer.fromJson(string);
            if(context != null) component.append(context.parseTextComponent(Objects.requireNonNull(deserialized)));
            else component.append(Objects.requireNonNull(deserialized));
        }
        return component;
    }

    /**
     * Get amount of rows for GUI.
     */
    public int getRows() {
        return rows;
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
