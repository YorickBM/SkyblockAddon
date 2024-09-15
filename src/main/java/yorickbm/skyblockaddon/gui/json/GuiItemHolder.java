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
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.registries.interfaces.CustomItemStack;
import yorickbm.skyblockaddon.registries.interfaces.CustomItems;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;
import yorickbm.skyblockaddon.util.NBT.NBTSerializable;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GuiItemHolder implements JSONSerializable, NBTSerializable {
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
    public ItemStack getItemStack(Island context, CompoundTag nbt) { return getItemStack(context, nbt, null); }
    public ItemStack getItemStack(Island context, CompoundTag nbt, SkyblockAddonRegistry registry) {
        if(registry instanceof CustomItemStack reg) {
            ItemStack stack = new ItemStack(reg.getItemFor(nbt).getItem());
            stack.setTag(reg.getItemFor(nbt).getTag());
            if(nbt.contains("SkullOwner")) stack.getOrCreateTag().putString("SkullOwner", nbt.getString("SkullOwner"));

            nbt.getAllKeys().forEach(key -> {
                if(!stack.getOrCreateTagElement(SkyblockAddon.MOD_ID).contains(key)) {
                    stack.getOrCreateTagElement(SkyblockAddon.MOD_ID).put(key, Objects.requireNonNull(nbt.get(key)));
                }
            });

            return stack;
        }

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
                if(context != null) l.stream().map(Component.Serializer::fromJson).filter(Objects::nonNull).map(context::parseTextComponent).forEach(component::append);
                else l.stream().map(Component.Serializer::fromJson).filter(Objects::nonNull).forEach(component::append);
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
    public TextComponent getDisplayName(Island context, ItemStack stack) throws NullPointerException {
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

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("item", this.item);

        CompoundTag display_name = new CompoundTag();
        IntStream.range(0, this.display_name.size())
                .forEach(i -> display_name.putString(String.valueOf(i), this.display_name.get(i)));

        CompoundTag lore = new CompoundTag();
        IntStream.range(0, this.lore.size())
                .forEach(i -> {
                    List<String> innerList = this.lore.get(i);
                    CompoundTag fragments = new CompoundTag();
                    IntStream.range(0, innerList.size())
                            .forEach(j -> fragments.putString(String.valueOf(j), innerList.get(j)));
                    lore.put(String.valueOf(i), fragments);
                });

        CompoundTag data = new CompoundTag();
        this.data.forEach(data::putString);

        tag.put("display_name", display_name);
        tag.put("lore", lore);
        tag.put("data", data);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {

        this.item = tag.getString("item");
        this.display_name = new ArrayList<>();
        this.lore = new ArrayList<>();
        this.data = new HashMap<>();

        CompoundTag displayNameTag = tag.getCompound("display_name");
        displayNameTag.getAllKeys().forEach(key -> this.display_name.add(displayNameTag.getString(key)));

        CompoundTag loreTag = tag.getCompound("lore");
        loreTag.getAllKeys().forEach(key -> {
            var fragmentsTag = loreTag.getCompound(key);
            var fragmentsList = fragmentsTag.getAllKeys().stream()
                    .map(fragmentsTag::getString)
                    .collect(Collectors.toList());
            this.lore.add(fragmentsList);
        });

        CompoundTag dataTag = tag.getCompound("data");
        dataTag.getAllKeys().forEach(key -> this.data.put(key, dataTag.getString(key)));

    }
}
