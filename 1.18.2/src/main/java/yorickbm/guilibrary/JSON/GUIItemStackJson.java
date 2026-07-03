package yorickbm.guilibrary.JSON;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Items;
import yorickbm.guilibrary.GUIItemStackHolder;
import yorickbm.guilibrary.util.ConditionEvaluator;
import yorickbm.guilibrary.util.Helper;
import yorickbm.guilibrary.util.JSON.JSONSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GUIItemStackJson implements JSONSerializable {

    private List<String> display_name;
    private String item;
    private int amount;
    private List<LoreLineJson> lore;
    private HashMap<String, String> data;

    public GUIItemStackJson() {
    }

    public GUIItemStackHolder getItemStackHolder() {
        return getItemStackHolder(null);
    }

    public GUIItemStackHolder getItemStackHolder(ConditionEvaluator.Context context) {
        List<List<String>> renderedLore = yorickbm.guilibrary.util.LoreRenderer.render(lore, context);
        return new GUIItemStackHolder(
                Helper.getItem(item.toLowerCase(), Items.BARRIER),
                this.amount,
                getCompoundTag(),
                display_name,
                renderedLore
        );
    }

    public CompoundTag getCompoundTag() {
        CompoundTag tag = new CompoundTag();
        if (!data.isEmpty()) {
            data.forEach(Objects.requireNonNull(tag)::putString);
        }
        return tag;
    }

    public TextComponent getDisplayName() throws NullPointerException {
        TextComponent result = new TextComponent("");
        for (String raw : display_name) {
            result.append(Objects.requireNonNull((Component) Component.Serializer.fromJson(raw)));
        }
        return result;
    }

    public String toJSON() {
        return gson().toJson(this);
    }

    public void fromJSON(String json) {
        Gson gson = gson();
        GUIItemStackJson parsed = gson.fromJson(json, GUIItemStackJson.class);
        this.display_name = parsed.display_name;
        if (parsed.item != null) {
            this.item = parsed.item;
        }
        if (parsed.amount >= 1) {
            this.amount = parsed.amount;
        }
        if (parsed.lore != null) {
            this.lore = parsed.lore;
        }
        if (parsed.data != null) {
            this.data = parsed.data;
        }
    }

    private static Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(LoreLineJson.class, new LoreLineDeserializer())
                .create();
    }
}
