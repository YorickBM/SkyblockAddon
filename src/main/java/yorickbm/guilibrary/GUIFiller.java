package yorickbm.guilibrary;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.eventbus.api.Event;
import yorickbm.guilibrary.events.GuiDrawFillerEvent;
import yorickbm.guilibrary.interfaces.GuiClickItemEvent;
import yorickbm.guilibrary.interfaces.ServerInterface;
import yorickbm.guilibrary.util.FillerPattern;

import java.util.ArrayList;
import java.util.List;

public class GUIFiller extends GUIItem {

    private final FillerPattern pattern;
    private final Class<? extends GuiDrawFillerEvent> event;

    // Constructor is private to force using the Builder
    private GUIFiller(Builder builder) {
        super(builder.item, builder.primaryClickClass, builder.secondaryClickClass, builder.data, builder.conditions);

        this.pattern = builder.pattern;
        this.event = builder.event;
    }

    // Getters for the fields (optional, depending on use case)
    public FillerPattern getPattern() {
        return this.pattern;
    }

    public Event getEvent(ServerInterface instance, int slots) {
        try {
            // Create a new instance of the event class, passing the parameters to its constructor
            return this.event.getConstructor(ServerInterface.class, GUIFiller.class, int.class)
                    .newInstance(instance, this, slots);  // Pass the arguments
        } catch (Exception e) {
            return new Event();  // Return null if event creation fails
        }
    }
    public boolean hasEvent() {
        return this.event != null;
    }

    public void setSlot(int slot) {
        super.slot = slot;
    }



    public static class Builder {
        private GUIItemStackHolder item;
        private FillerPattern pattern = FillerPattern.INSIDE;
        Class<? extends GuiClickItemEvent> primaryClickClass, secondaryClickClass;
        private CompoundTag data = new CompoundTag();
        private List<String> conditions = new ArrayList<>();
        private Class<? extends GuiDrawFillerEvent> event = null;

        // Set the slot (mandatory)
        public Builder setPattern(FillerPattern pattern) {
            this.pattern = pattern;
            return this;
        }

        // Set the ItemStack (mandatory)
        public Builder setItemStack(GUIItemStackHolder item) {
            this.item = item;
            return this;
        }

        // Set the primary click event class (mandatory)
        public Builder setPrimaryClickClass(Class<? extends GuiClickItemEvent> primaryClickClass) {
            this.primaryClickClass = primaryClickClass;
            return this;
        }

        // Set the secondary click event class (optional)
        public Builder setSecondaryClickClass(Class<? extends GuiClickItemEvent> secondaryClickClass) {
            this.secondaryClickClass = secondaryClickClass;
            return this;
        }

        public Builder setActionData(CompoundTag data) {
            this.data = data;
            return this;
        }

        public Builder setConditions(List<String> conditions) {
            this.conditions = conditions;
            return this;
        }

        // Build the GUIItem instance
        public GUIFiller build() {
            if (item == null || item.getItemStack().isEmpty()) {
                throw new IllegalArgumentException("An itemstack must be provided.");
            }

            return new GUIFiller(this);
        }

        public Builder setEvent(Class<? extends GuiDrawFillerEvent> event) {
            this.event = event;
            return this;
        }
    }
}

