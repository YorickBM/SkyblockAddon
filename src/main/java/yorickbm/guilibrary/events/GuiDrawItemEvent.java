package yorickbm.guilibrary.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import yorickbm.guilibrary.GUIItem;
import yorickbm.guilibrary.interfaces.ServerInterface;

@Cancelable
public class GuiDrawItemEvent extends Event {

    protected ServerInterface instance;

    private int slot = -1;
    private ItemStack item = ItemStack.EMPTY;
    private GUIItem itemHolder;

    public GuiDrawItemEvent(ServerInterface instance, GUIItem item) {
        this.instance = instance;
        this.itemHolder = item;

        this.slot = item.getSlot();
        this.item = item.getItem();
    }

    public ItemStack getItemStack() {
        return this.item;
    }

    public void setItemStack(ItemStack item) {
        this.item = item;
    }

    public int getSlot() {
        return this.slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ServerInterface getHolder() {
        return this.instance;
    }

    public void drawItem() {
        this.instance.setItem(this.slot, 0, this.item);
    }

    public GUIItem getItemHolder() {  return this.itemHolder; }
}
