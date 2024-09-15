package yorickbm.skyblockaddon.gui.json;

import com.google.gson.Gson;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import yorickbm.skyblockaddon.islands.Island;
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
    public TextComponent getTitle(Island context) throws NullPointerException {
        TextComponent component = new TextComponent("");
        try {
            for(String string : this.title) {
                Component deserialized = Component.Serializer.fromJson(string);
                if(context != null) component.append(context.parseTextComponent(Objects.requireNonNull(deserialized), new CompoundTag()));
                else component.append(Objects.requireNonNull(deserialized));
            }
        } catch (Exception ex) {
            return (TextComponent) new TextComponent("Invalid JSON in title").withStyle(ChatFormatting.RED);
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
