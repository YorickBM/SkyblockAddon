package yorickbm.guilibrary.interfaces;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import yorickbm.guilibrary.GUIItem;

@Cancelable
public class GuiClickItemEvent extends Event {

    protected ServerInterface instance;
    protected ServerPlayer target;
    protected Slot slot;
    protected GUIItem item;

    public GuiClickItemEvent(ServerInterface instance, ServerPlayer player, Slot slot, GUIItem item) {
        this.instance = instance;
        this.target = player;
        this.slot = slot;
        this.item = item;
    }

    public ServerInterface getHolder() { return this.instance; }
    public ServerPlayer getTarget() { return this.target; }

    public ItemStack getClickedItem() { return this.slot.getItem(); }
    public GUIItem getItemHolder() { return this.item; }

}
