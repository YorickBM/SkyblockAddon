package yorickbm.guilibrary.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import yorickbm.guilibrary.GUIItem;
import yorickbm.guilibrary.interfaces.GuiClickItemEvent;
import yorickbm.guilibrary.interfaces.ServerInterface;

@Cancelable
public class ItemOpenMenuEvent extends GuiClickItemEvent {
    public ItemOpenMenuEvent(final ServerInterface instance, final ServerPlayer player, final Slot slot, final GUIItem item) {
        super(instance, player, slot, item);

        final CompoundTag modData = item.getActionData();
        if(!modData.contains("gui")) {
            this.setCanceled(true); //Event is canceled, cannot get GUI data
            return;
        }

        //Merge item data into GUI data
        final CompoundTag guiData = instance.getData();
        guiData.merge(slot.getItem().getOrCreateTag());

        MinecraftForge.EVENT_BUS.post(new OpenMenuEvent(
                modData.getString("gui"), //Open the GUI based on the ModData GUI value
                player, //Clicking player is the target
                guiData) //Carry over the previous GUIs data
        );
    }
}
