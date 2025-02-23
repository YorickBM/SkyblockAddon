package yorickbm.skyblockaddon.registries.json;

import com.google.gson.Gson;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public class BiomeRegistryJson implements JSONSerializable {
    LinkedHashMap<String, String> biomes;

    @Override
    public String toJSON() {
        return "";
    }

    @Override
    public void fromJSON(final String json) {
        final Gson gson = new Gson();
        final BiomeRegistryJson temp = gson.fromJson(json, BiomeRegistryJson.class);
        this.biomes = temp.biomes;
    }

    public Map<String, Item> toMap() {
        final Map<String, Item> data = new LinkedHashMap<>();

        this.biomes.forEach((s,v) -> {
            data.put(s.toLowerCase(), ServerHelper.getItem(v.toLowerCase(), Items.DEAD_BUSH));
        });

        return data;
    }
}
