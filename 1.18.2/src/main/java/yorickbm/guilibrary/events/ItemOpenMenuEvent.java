package yorickbm.guilibrary.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import yorickbm.guilibrary.GUIItem;
import yorickbm.guilibrary.interfaces.GuiClickItemEvent;
import yorickbm.guilibrary.interfaces.ServerInterface;
import yorickbm.skyblockaddon.components.ItemStackComponent;

@Cancelable
public class ItemOpenMenuEvent extends GuiClickItemEvent {
    public ItemOpenMenuEvent(final ServerInterface instance, final ServerPlayer player, final Slot slot, final GUIItem item) {
        super(instance, player, slot, item);

        final ItemStackComponent modData = item.getActionData().copy();
        if (!modData.contains("gui")) {
            this.setCanceled(true);
            return;
        }
        final String gui = modData.getObject("gui", String.class);
        modData.remove("gui");

        CompoundTag itemData = slot.getItem().getOrCreateTag().copy();
        itemData.remove("display");

        final CompoundTag guiData = instance.getData();
        guiData.merge(modData.getCompound());
        guiData.merge(itemData);

        MinecraftForge.EVENT_BUS.post(new OpenMenuEvent(gui, player, guiData));
    }
}
