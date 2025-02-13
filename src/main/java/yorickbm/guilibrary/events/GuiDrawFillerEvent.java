package yorickbm.guilibrary.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import yorickbm.guilibrary.GUIFiller;
import yorickbm.guilibrary.GUIItemStackHolder;
import yorickbm.guilibrary.interfaces.ServerInterface;
import yorickbm.guilibrary.util.FillerPattern;

@Cancelable
public class GuiDrawFillerEvent extends Event {
    protected ServerInterface instance;

    private GUIItemStackHolder item;
    private int slots = 0;
    private FillerPattern pattern;
    private GUIFiller filler;

    public GuiDrawFillerEvent(ServerInterface instance, GUIFiller filler, int slots) {
        this.instance = instance;
        this.item = filler.getItemHolder();
        this.pattern = filler.getPattern();
        this.slots = slots;
        this.filler = filler;
    }

    public GUIItemStackHolder getItemStackHolder() { return this.item; }
    public void setItemStackHolder(GUIItemStackHolder item) { this.item = item; }

    public FillerPattern getPattern() {
        return this.pattern;
    }

    public void setPattern(FillerPattern pattern) {
        this.pattern = pattern;
    }

    public SimpleContainer getContainer() {
        return this.instance.getContainer();
    }

    public CompoundTag getContainerNBT() {
        return this.instance.getData();
    }

    public void drawItem(int slot, ItemStack item) {
        this.instance.setItem(slot, 0, item);
        this.filler.setSlot(slot);
    }

    public int getSlots() { return this.slots; }

    public boolean slotHasItem(int slot) {
        return this.instance.getSlot(slot).hasItem();
    }

    public void setMaxPage(int page) {
        this.instance.setMaxPage(page);
    }
    public int getCurrentPage() {
        return this.instance.getCurrentPage();
    }
}
