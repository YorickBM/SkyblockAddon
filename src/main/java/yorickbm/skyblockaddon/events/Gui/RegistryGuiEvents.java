package yorickbm.skyblockaddon.events.Gui;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yorickbm.guilibrary.GUIItemStackHolder;
import yorickbm.skyblockaddon.registries.RegistryEvents;

public class RegistryGuiEvents {
    //TODO Add registries on filler items

    @SubscribeEvent
    public void onBiomeRegistryFiller(RegistryEvents.BiomeRegistry event) {
        if(event.isCanceled()) return; //Event is canceled

        for (int slot = 0; slot < event.getSlots(); slot++) {
            if((slot < 10 || slot > event.getSlots() - 10)  || (slot%9 == 0 || slot%9 == 8)) continue;
            CompoundTag data = new CompoundTag();
            boolean hasNext = event.getRegistry().getNextData(data);

            GUIItemStackHolder holder = event.processHolder(event.getItemStackHolder().reset(), data);
            holder.setItem(((yorickbm.skyblockaddon.registries.BiomeRegistry) event.getRegistry()).getItemFor(data));

            event.drawItem(slot, holder.getItemStack());

            if(!hasNext) break;
        }
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRegistryFiller(RegistryEvents event) {
        if(event.isCanceled()) return; //Event is canceled

        for (int slot = 0; slot < event.getSlots(); slot++) {
            if((slot < 10 || slot > event.getSlots() - 10)  || (slot%9 == 0 || slot%9 == 8)) continue;
            CompoundTag data = new CompoundTag();
            boolean hasNext = event.getRegistry().getNextData(data);

            GUIItemStackHolder holder = event.processHolder(event.getItemStackHolder().reset(), data);
            event.drawItem(slot, holder.getItemStack());

            if(!hasNext) break;
        }
        event.setCanceled(true);
    }
}
