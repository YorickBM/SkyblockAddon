package yorickbm.guilibrary.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import yorickbm.guilibrary.GUIFiller;
import yorickbm.guilibrary.interfaces.ServerInterface;
import yorickbm.guilibrary.util.FillerPattern;

@Cancelable
public class GuiDrawFillerEvent extends Event {
    protected ServerInterface instance;

    private ItemStack item = ItemStack.EMPTY;
    private int slots = 0;
    private FillerPattern pattern;
    private GUIFiller filler;

    public GuiDrawFillerEvent(ServerInterface instance, GUIFiller filler, int slots) {
        this.instance = instance;
        this.item = filler.getItem();
        this.pattern = filler.getPattern();
        this.slots = slots;
        this.filler = filler;
    }

    public ItemStack getItemStack() {
        return this.item;
    }

    public void setItemStack(ItemStack item) {
        this.item = item;
    }

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

    public void drawItems() {
        switch(this.pattern) {
            case EDGES -> {
                for (int slot = 0; slot < this.slots; slot++) {
                    if(slot >= 10 && slot <= this.slots - 10  && slot%9 != 0 && slot%9 != 8) continue;
                    if(!instance.getSlot(slot).hasItem()) {
                        instance.setItem(slot, 0, this.item);
                        filler.setSlot(slot);
                    }
                }
            }
            case EMPTY -> {
                for (int slot = 0; slot < this.slots; slot++) {
                    if(!instance.getSlot(slot).hasItem()) {
                        instance.setItem(slot, 0, this.item);
                        filler.setSlot(slot);
                    }
                }
            }
            case INSIDE -> {
                for (int slot = 0; slot < this.slots; slot++) {
                    if((slot < 10 || slot > this.slots - 10)  || (slot%9 == 0 || slot%9 == 8)) continue;
                    if(!instance.getSlot(slot).hasItem()) {
                        instance.setItem(slot, 0, this.item);
                        filler.setSlot(slot);
                    }
                }
            }
        }
    }
}
