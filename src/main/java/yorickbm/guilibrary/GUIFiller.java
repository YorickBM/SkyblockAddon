package yorickbm.guilibrary;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import yorickbm.guilibrary.interfaces.GuiClickItemEvent;
import yorickbm.guilibrary.util.FillerPattern;

import java.util.ArrayList;
import java.util.List;

public class GUIFiller extends GUIItem {

    private final FillerPattern pattern;
    // Constructor is private to force using the Builder
    private GUIFiller(Builder builder) {
        super(builder.item, builder.primaryClickClass, builder.secondaryClickClass, builder.data, builder.conditions);

        this.pattern = builder.pattern;
    }

    // Getters for the fields (optional, depending on use case)
    public FillerPattern getPattern() {
        return pattern;
    }

    public void setSlot(int slot) {
        super.slot = slot;
    }

    public static class Builder {
        private ItemStack item;
        private FillerPattern pattern = FillerPattern.INSIDE;
        Class<? extends GuiClickItemEvent> primaryClickClass, secondaryClickClass;
        private CompoundTag data = new CompoundTag();
        private List<String> conditions = new ArrayList<>();

        // Set the slot (mandatory)
        public Builder setPattern(FillerPattern pattern) {
            this.pattern = pattern;
            return this;
        }

        // Set the ItemStack (mandatory)
        public Builder setItemStack(ItemStack item) {
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
            if (item == null || item.isEmpty()) {
                throw new IllegalArgumentException("An itemstack must be provided.");
            }

            return new GUIFiller(this);
        }
    }
}

