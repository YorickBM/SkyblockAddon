package yorickbm.guilibrary;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.eventbus.api.Event;
import yorickbm.guilibrary.interfaces.GuiClickItemEvent;
import yorickbm.guilibrary.interfaces.ServerInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUIItem {

    private final GUIItemStackHolder item;

    protected int slot;
    private final Class<? extends GuiClickItemEvent> primaryClickClass, secondaryClickClass;
    private final CompoundTag data;
    private final List<String> conditions;

    private final UUID id; //Unique ID to differentiate duplicate items

    // Constructor is private to force using the Builder
    private GUIItem(Builder builder) {
        this.item = builder.item;
        this.slot = builder.slot;
        this.primaryClickClass = builder.primaryClickClass;
        this.secondaryClickClass = builder.secondaryClickClass;
        this.data = builder.data;
        this.conditions = builder.conditions;

        this.id = UUID.randomUUID();
    }

    public GUIItem(GUIItemStackHolder item, Class<? extends GuiClickItemEvent> primaryClickClass, Class<? extends GuiClickItemEvent> secondaryClickClass, CompoundTag data, List<String> conditions) {
        this.item = item;
        this.slot = -1;
        this.primaryClickClass = primaryClickClass;
        this.secondaryClickClass = secondaryClickClass;
        this.data = data;
        this.conditions = conditions;

        this.id = UUID.randomUUID();
    }

    // Getters for the fields (optional, depending on use case)
    public GUIItemStackHolder getItemHolder() {
        return item;
    }

    public int getSlot() {
        return slot;
    }

    public boolean hasCondition(String condition) { return conditions.contains(condition); }

    public Event getPrimaryClick(ServerInterface instance, ServerPlayer player, Slot slot) {
        if(primaryClickClass == null) return new Event();
        return createEventInstance(primaryClickClass, instance, player, slot);
    }

    public Event getSecondaryClick(ServerInterface instance, ServerPlayer player, Slot slot) {
        if(secondaryClickClass == null) return new Event();
        return createEventInstance(secondaryClickClass, instance, player, slot);
    }

    // Helper method to create a new event instance using reflection
    private Event createEventInstance(Class<? extends GuiClickItemEvent> eventClass, ServerInterface instance, ServerPlayer player, Slot slot) {
        try {
            // Create a new instance of the event class, passing the parameters to its constructor
            return eventClass.getConstructor(ServerInterface.class, ServerPlayer.class, Slot.class, GUIItem.class)
                    .newInstance(instance, player, slot, this);  // Pass the arguments
        } catch (Exception e) {
            return new Event();  // Return null if event creation fails
        }
    }

    public CompoundTag getActionData() {
        return data;
    }

    public static class Builder {
        private GUIItemStackHolder item;
        private int slot = -1;
        Class<? extends GuiClickItemEvent> primaryClickClass, secondaryClickClass;
        private CompoundTag data = new CompoundTag();
        private List<String> conditions = new ArrayList<>();

        // Set the slot (mandatory)
        public Builder setSlot(int slot) {
            this.slot = slot;
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
        public GUIItem build() {
            if (slot < 0) {
                throw new IllegalArgumentException("A slot must be provided.");
            }
            if (item == null || item.getItemStack().isEmpty()) {
                throw new IllegalArgumentException("An itemstack must be provided.");
            }

            return new GUIItem(this);
        }
    }
}

