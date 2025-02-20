package yorickbm.guilibrary.events;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import yorickbm.guilibrary.GUIFiller;
import yorickbm.guilibrary.GUIItemStackHolder;
import yorickbm.guilibrary.GUIPlaceholder;
import yorickbm.guilibrary.interfaces.ServerInterface;
import yorickbm.guilibrary.util.FillerPattern;

@Cancelable
public class GuiDrawFillerEvent extends Event {
    protected ServerInterface instance;

    private final GUIItemStackHolder item;
    private int slots = 0;
    private FillerPattern pattern;
    private final GUIFiller filler;

    public GuiDrawFillerEvent(ServerInterface instance, GUIFiller filler, int slots) {
        this.instance = instance;
        this.item = filler.getItemHolder().clone();
        this.pattern = filler.getPattern();
        this.slots = slots;
        this.filler = filler;
    }

    public GUIItemStackHolder getItemStackHolder() { return this.item; }

    public FillerPattern getPattern() {
        return this.pattern;
    }
    public void setPattern(FillerPattern pattern) {
        this.pattern = pattern;
    }

    public void drawItem(int slot, ItemStack item) {
        GUIPlaceholder guiItem = new GUIPlaceholder(this.filler);
        if(guiItem.isClickable()) this.instance.addItem(slot, guiItem);

        this.instance.setItem(slot, 0, item);
    }

    public int getSlots() { return this.slots; }

    public boolean slotIsEmpty(int slot) {
        return !this.instance.getSlot(slot).hasItem();
    }

    public void setMaxPage(int page) {
        this.instance.setMaxPage(page);
    }
    public int getCurrentPage() {
        return this.instance.getCurrentPage();
    }
}
