package yorickbm.guilibrary;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import yorickbm.guilibrary.util.Helper;

import java.util.List;
import java.util.stream.Collectors;

public class GUIItemStackHolder {

    private Item item, _item;
    private int amount, _amount;
    private CompoundTag tag;

    private List<TextComponent> display_name, _display_name;
    private List<List<TextComponent>> lore, _lore;

    public GUIItemStackHolder(Item item, int amount, CompoundTag tag, List<String> display_name, List<List<String>> lore) {
        // Process display name
        this.display_name = display_name.stream()
                .map(Component.Serializer::fromJson)
                .filter(c -> c instanceof TextComponent)
                .map(c -> (TextComponent) c)
                .collect(Collectors.toList());

        // Process lore data
        this.lore = lore.stream()
                .map(l -> l.stream()
                        .map(Component.Serializer::fromJson)
                        .filter(c -> c instanceof TextComponent) // Ensure it's a TextComponent
                        .map(c -> (TextComponent) c) // Cast to TextComponent
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        this.tag = tag;
        this.item = item;
        this.amount = amount;

        //Keep originals
        this._lore = this.lore;
        this._display_name = this.display_name;
        this._item = this.item;
        this._amount = this.amount;
    }

    public List<List<TextComponent>> getLore() { return this.lore; }
    public void setLore(List<List<TextComponent>> lore) { this.lore = lore; }

    public List<TextComponent> getDisplayName() { return this.display_name; }
    public void setDisplayName(List<TextComponent> display_name) { this.display_name = display_name; }

    public void setItem(Item item) { this.item = item; }
    public Item getItem() { return this.item; }

    public GUIItemStackHolder reset() {
        this.lore = this._lore;
        this.display_name = this._display_name;
        this.item = this._item;
        this.amount = this._amount;

        return this;
    }

    public void setAmount(int amount) { this.amount = amount; }
    public int getAmount() { return this.amount; }

    public ItemStack getItemStack() {
        ItemStack stack = new ItemStack(this.item, this.amount);
        stack.getOrCreateTag().put(GUILibraryRegistry.MOD_ID, this.tag);

        stack.setHoverName(this.display_name.stream().reduce(new TextComponent(""), (a, b) -> (TextComponent) a.append(b)));

        Helper.addLore(stack, this.lore.stream()
                .map(innerList -> innerList.stream()
                        .reduce(new TextComponent(""), (a, b) -> (TextComponent) a.append(b))
                ).toArray(TextComponent[]::new));

        return stack;
    }

    public void addData(String key, String value) {
        this.tag.putString(key, value);
    }
}
