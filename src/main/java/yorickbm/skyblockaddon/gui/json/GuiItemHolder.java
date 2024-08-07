package yorickbm.skyblockaddon.gui.json;

import com.google.gson.Gson;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.List;

public class GuiItemHolder implements JSONSerializable {
    private String display_name;
    private String item;
    private List<String> lore;

    private String onClick;
    private String onSecondClick;
    private String data;

    /**
     * Get itemstack representative of the data within the Holder.
     *
     * @return - Itemstack
     */
    public ItemStack getItemStack() {
        ItemStack stack = new ItemStack(getItem());
        stack.setHoverName(getDisplayName());
        for (String string : lore) {
            ServerHelper.addLore(stack, Component.Serializer.fromJson(string));
        }
        return stack;
    }

    /**
     * Get item from ForgeRegistries.
     * If not found returns BARRIER.
     *
     * @return - Minecraft Registry Item
     */
    public Item getItem() {
        Item mcItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(item));
        return mcItem != null ? mcItem : Items.BARRIER;
    }

    /**
     * Get TextComponent Display name for Item.
     *
     * @return - TextComponent
     */
    public TextComponent getDisplayName() {
        return (TextComponent) Component.Serializer.fromJson(display_name);
    }

    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(String json) {
        Gson gson = new Gson();
        GuiItemHolder temp = gson.fromJson(json, GuiItemHolder.class);
        this.display_name = temp.display_name;
        this.item = temp.item;
        this.lore = temp.lore;
        this.onClick = temp.onClick;
        this.onSecondClick = temp.onSecondClick;
        this.data = temp.data;
    }
}
