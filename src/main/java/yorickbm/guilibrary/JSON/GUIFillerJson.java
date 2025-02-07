package yorickbm.guilibrary.JSON;

import com.google.gson.Gson;
import yorickbm.guilibrary.GUIFiller;
import yorickbm.guilibrary.util.FillerPattern;
import yorickbm.guilibrary.util.JSON.JSONSerializable;

import java.util.ArrayList;
import java.util.List;

public class GUIFillerJson implements JSONSerializable {

    private GUIItemStackJson item;
    private FillerPattern pattern;
    private GUIActionJson action = new GUIActionJson();
    private List<String> conditions = new ArrayList<>();

    /*
    Get GUI Item for slot
     */
    public GUIFiller getItem() {

        GUIFiller.Builder builder = new GUIFiller.Builder()
                .setPattern(this.pattern)
                .setItemStack(this.item.getItemStack())
                .setConditions(this.conditions);

        if(this.action.hasPrimary()) builder.setPrimaryClickClass(this.action.getPrimary());
        if(this.action.hasSecondary()) builder.setSecondaryClickClass(this.action.getSecondary());

        builder.setActionData(this.action.getData());

        return builder.build();
    }


    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(String json) {
        Gson gson = new Gson();
        GUIFillerJson temp = gson.fromJson(json, GUIFillerJson.class);

        this.pattern = temp.pattern;
        this.item = temp.item;
        if(temp.action != null) this.action = temp.action;
        if(temp.conditions != null) this.conditions = temp.conditions;
    }
}
