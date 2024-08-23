package yorickbm.skyblockaddon.gui.json;

import com.google.gson.Gson;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.gui.interfaces.GuiContext;
import yorickbm.skyblockaddon.registries.interfaces.CustomItems;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GuiItemHolder implements JSONSerializable {
    private static final Logger LOGGER = LogManager.getLogger();

    private List<String> display_name;
    private String item;
    private List<List<String>> lore;
    private HashMap<String, String> data;

    /**
     * Get itemstack representative of the data within the Holder.
     *
     * @return - Itemstack
     */
    public ItemStack getItemStack(GuiContext context, CompoundTag nbt) { return getItemStack(context, nbt, null); }
    public ItemStack getItemStack(GuiContext context, CompoundTag nbt, SkyblockAddonRegistry registry) {
        ItemStack stack = new ItemStack(
                registry instanceof CustomItems reg ? reg.getItemFor(nbt) : ServerHelper.getItem(item.toLowerCase(), Items.BARRIER)
        );

        //Add custom NBT data
        stack.addTagElement(SkyblockAddon.MOD_ID, nbt);
        if(nbt.contains("SkullOwner")) stack.getOrCreateTag().putString("SkullOwner", nbt.getString("SkullOwner"));

        //Set display name
        try {
            stack.setHoverName(getDisplayName(context, stack));
        } catch(Exception ex) {
            stack.setHoverName(new TextComponent("Invalid display name").withStyle(ChatFormatting.RED));
        }

        try {
            //Process lore data
            Component[] finalLore = lore.stream().map(l -> {
                TextComponent component = new TextComponent("");
                l.stream().map(Component.Serializer::fromJson).filter(Objects::nonNull).map(context::parseTextComponent).forEach(component::append);
                return component;
            }).toArray(Component[]::new);
            ServerHelper.addLore(stack, finalLore);
        } catch (Exception ex) {
            ServerHelper.addLore(stack, new TextComponent("Invalid lore").withStyle(ChatFormatting.RED));
        }
        return stack;
    }

    public CompoundTag getTag(CompoundTag tag) {
        if(!data.isEmpty()) {
            this.data.forEach(tag::putString);
        }
        return tag;
    }

    /**
     * Get TextComponent Display name for Item.
     *
     * @return - TextComponent
     */
    public TextComponent getDisplayName(GuiContext context, ItemStack stack) throws NullPointerException {
        TextComponent component = new TextComponent("");

        for(String string : this.display_name) {
            Component deserialized = Component.Serializer.fromJson(string);
            if(context != null) component.append(context.parseTextComponent(parseTextComponent(Objects.requireNonNull(deserialized), stack)));
            else component.append(parseTextComponent(Objects.requireNonNull(deserialized), stack));
        }

        return component;
    }

    /**
     * Allow NBT data to be used as variables in text
     */
    public Component parseTextComponent(@NotNull Component original, ItemStack stack) {

        String msg = original.getString();

        for(String key : stack.getOrCreateTagElement(SkyblockAddon.MOD_ID).getAllKeys()) {
            msg = msg.replace("%data" + key + "%", stack.getOrCreateTagElement(SkyblockAddon.MOD_ID).getString(key));
        }

        return new TextComponent(msg).withStyle(original.getStyle());
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
        this.data = temp.data;
    }
}
