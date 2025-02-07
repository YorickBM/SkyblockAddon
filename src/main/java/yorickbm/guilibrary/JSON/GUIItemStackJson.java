package yorickbm.guilibrary.JSON;

import com.google.gson.Gson;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import yorickbm.guilibrary.GUILibraryRegistry;
import yorickbm.guilibrary.util.Helper;
import yorickbm.guilibrary.util.JSON.JSONSerializable;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GUIItemStackJson implements JSONSerializable {

    private List<String> display_name;
    private String item = "minecraft:barrier";
    private int amount = 1;
    private List<List<String>> lore = new ArrayList<>();
    private HashMap<String, String> data = new HashMap<>();

    public ItemStack getItemStack() {
        ItemStack stack = new ItemStack(Helper.getItem(item.toLowerCase(), Items.BARRIER), this.amount);
        setTag(stack);

        //Set display name
        try {
            stack.setHoverName(getDisplayName());
        } catch(Exception ex) {
            stack.setHoverName(new TextComponent("Invalid display name").withStyle(ChatFormatting.RED));
        }

        try {
            //Process lore data
            Component[] finalLore = lore.stream().map(l -> {
                TextComponent component = new TextComponent("");
                l.stream().map(Component.Serializer::fromJson).filter(Objects::nonNull).forEach(component::append);
                return component;
            }).toArray(Component[]::new);
            ServerHelper.addLore(stack, finalLore);
        } catch (Exception ex) {
            ServerHelper.addLore(stack, new TextComponent("Invalid lore").withStyle(ChatFormatting.RED));
        }

        return stack;
    }

    /**
     * Set itemstack data
     */
    public void setTag(ItemStack item) {
        CompoundTag tag = new CompoundTag();

        if(!data.isEmpty()) {
            this.data.forEach(tag::putString);
        }

        item.getOrCreateTag().put(GUILibraryRegistry.MOD_ID, tag); //Insert data under the GUI MOD (Mixin can override this).
    }

    /**
     * Get TextComponent Display name for Item.
     *
     * @return - TextComponent
     */
    public TextComponent getDisplayName() throws NullPointerException {
        TextComponent component = new TextComponent("");

        for(String string : this.display_name) {
            Component deserialized = Component.Serializer.fromJson(string);
            component.append(Objects.requireNonNull(deserialized));
        }

        return component;
    }

    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(String json) {
        Gson gson = new Gson();
        GUIItemStackJson temp = gson.fromJson(json, GUIItemStackJson.class);
        this.display_name = temp.display_name;
        if(temp.item != null) this.item = temp.item;
        if(temp.amount < 1) this.amount = temp.amount;
        if(temp.lore != null) this.lore = temp.lore;
        if(temp.data != null) this.data = temp.data;
    }

}
