package yorickbm.skyblockaddon.islands.permissions;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class EmptyPermission extends Permission {

    public EmptyPermission(boolean state) {
        super(state);
    }

    @Override
    public boolean isAllowed(Object... data) {
        return false;
    }

    @Override
    public Item getDisplayItem() {
        return Items.AIR;
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("");
    }

    @Override
    public Component[] getDescription() {
        return null;
    }
}
