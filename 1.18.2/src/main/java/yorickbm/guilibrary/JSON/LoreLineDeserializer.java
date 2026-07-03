package yorickbm.guilibrary.JSON;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class LoreLineDeserializer implements JsonDeserializer<LoreLineJson> {

    @Override
    public LoreLineJson deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        LoreLineJson line = new LoreLineJson();

        if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            List<LoreSegmentJson> segments = context.deserialize(array, new TypeToken<List<LoreSegmentJson>>() {
            }.getType());
            line.setSegments(segments);
        } else if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            if (obj.has("join")) {
                LoreJoinJson join = context.deserialize(obj.get("join"), LoreJoinJson.class);
                line.setJoin(join);
            } else {
                throw new JsonParseException("Lore line object must have a \"join\" key: " + obj);
            }
        } else {
            throw new JsonParseException("Lore line must be either an array of segments or a {\"join\": ...} object, got: " + json);
        }

        return line;
    }
}
