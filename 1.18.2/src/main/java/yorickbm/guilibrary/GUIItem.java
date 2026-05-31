package yorickbm.guilibrary;

import yorickbm.guilibrary.interfaces.GuiClickItemEvent;
import yorickbm.skyblockaddon.components.ItemStackComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUIItem {
    private final GUIItemStackHolder item;

    protected int slot;
    private final Class<? extends GuiClickItemEvent> primaryClickClass, secondaryClickClass;
    private final ItemStackComponent data;
    private final List<String> conditions;

    private final UUID id;

    private GUIItem(final Builder builder) {
        this.item = builder.item;
        this.slot = builder.slot;
        this.primaryClickClass = builder.primaryClickClass;
        this.secondaryClickClass = builder.secondaryClickClass;
        this.data = builder.data;
        this.conditions = builder.conditions;
        this.id = UUID.randomUUID();
    }

    public GUIItem(final GUIItemStackHolder item, final Class<? extends GuiClickItemEvent> primaryClickClass, final Class<? extends GuiClickItemEvent> secondaryClickClass, final ItemStackComponent data, final List<String> conditions) {
        this.item = item;
        this.slot = -1;
        this.primaryClickClass = primaryClickClass;
        this.secondaryClickClass = secondaryClickClass;
        this.data = data;
        this.conditions = conditions;
        this.id = UUID.randomUUID();
    }

    public GUIItemStackHolder getItemHolder() {
        return item;
    }

    public int getSlot() {
        return this.slot;
    }

    public boolean hasCondition(final String condition) { return conditions.contains(condition); }

    public Class<? extends GuiClickItemEvent> getPrimaryClickClass() {
        return this.primaryClickClass;
    }

    public Class<? extends GuiClickItemEvent> getSecondaryClickClass() {
        return this.secondaryClickClass;
    }

    public ItemStackComponent getActionData() {
        return data;
    }

    public static class Builder {
        private GUIItemStackHolder item;
        private int slot = -1;
        Class<? extends GuiClickItemEvent> primaryClickClass, secondaryClickClass;
        private ItemStackComponent data = new ItemStackComponent();
        private List<String> conditions = new ArrayList<>();

        public Builder setSlot(final int slot) {
            this.slot = slot;
            return this;
        }

        public Builder setItemStack(final GUIItemStackHolder item) {
            this.item = item;
            return this;
        }

        public Builder setPrimaryClickClass(final Class<? extends GuiClickItemEvent> primaryClickClass) {
            this.primaryClickClass = primaryClickClass;
            return this;
        }

        public Builder setSecondaryClickClass(final Class<? extends GuiClickItemEvent> secondaryClickClass) {
            this.secondaryClickClass = secondaryClickClass;
            return this;
        }

        public Builder setActionData(final ItemStackComponent data) {
            this.data = data;
            return this;
        }

        public Builder setConditions(final List<String> conditions) {
            this.conditions = conditions;
            return this;
        }

        public GUIItem build() {
            if (item == null || item.getItemStack().isEmpty()) {
                throw new IllegalArgumentException("An itemstack must be provided.");
            }
            return new GUIItem(this);
        }
    }
}
