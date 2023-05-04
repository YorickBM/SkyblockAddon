package yorickbm.skyblockaddon.islands.permissions;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import yorickbm.skyblockaddon.util.ServerHelper;

public abstract class Permission {

    protected boolean state;

    public Permission(boolean state) {
        this.state = state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
    public boolean getState() { return this.state; }

    public boolean isAllowed(Object ...data) {
        return this.state;
    }
    public abstract Item getDisplayItem();

    public abstract net.minecraft.network.chat.Component getDisplayName();
    public abstract Component[] getDescription();

    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(getDisplayItem());
        if(getDisplayName() != null) item.setHoverName(getDisplayName());
        if(getDescription() != null) ServerHelper.addLore(item, ServerHelper.combineComponents(getDescription()));

        return item;
    }
}
