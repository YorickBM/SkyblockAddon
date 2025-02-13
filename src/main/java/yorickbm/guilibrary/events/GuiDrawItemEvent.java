package yorickbm.guilibrary.events;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import yorickbm.guilibrary.GUIItem;
import yorickbm.guilibrary.GUIItemStackHolder;
import yorickbm.guilibrary.interfaces.ServerInterface;

@Cancelable
public class GuiDrawItemEvent extends Event {

    protected ServerInterface instance;

    private int slot = -1;
    private GUIItemStackHolder item;
    private GUIItem itemHolder;

    public GuiDrawItemEvent(ServerInterface instance, GUIItem item) {
        this.instance = instance;
        this.itemHolder = item;

        this.slot = item.getSlot();
        this.item = item.getItemHolder();
    }

    public GUIItemStackHolder getItemStackHolder() { return this.item; }
    public void setItemStackHolder(GUIItemStackHolder item) { this.item = item; }

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
        this.instance.setItem(this.slot, 0, this.item.getItemStack());
    }
    public GUIItem getItemHolder() {  return this.itemHolder; }
}
