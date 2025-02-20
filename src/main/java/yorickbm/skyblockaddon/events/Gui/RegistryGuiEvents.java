package yorickbm.skyblockaddon.events.Gui;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.guilibrary.GUIItemStackHolder;
import yorickbm.skyblockaddon.registries.RegistryEvents;

public class RegistryGuiEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void onBiomeRegistryFiller(RegistryEvents.BiomeRegistry event) {
        if(event.isCanceled()) return; //Event is canceled

        for (int slot = 0; slot < event.getSlots(); slot++) {
            if((slot < 10 || slot > event.getSlots() - 10)  || (slot%9 == 0 || slot%9 == 8)) continue;
            CompoundTag data = new CompoundTag();

            if(!event.getRegistry().hasNext()) break;
            event.getRegistry().getNextData(data);

            GUIItemStackHolder holder = event.processHolder(event.getItemStackHolder().clone(), data);
            holder.setItem(((yorickbm.skyblockaddon.registries.BiomeRegistry) event.getRegistry()).getItemFor(data));

            event.drawItem(slot, holder.getItemStack());
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onGroupRegistryFiller(RegistryEvents.GroupsRegistry event) {
        if(event.isCanceled()) return; //Event is canceled

        for (int slot = 0; slot < event.getSlots(); slot++) {
            if((slot < 10 || slot > event.getSlots() - 10)  || (slot%9 == 0 || slot%9 == 8)) continue;
            CompoundTag data = new CompoundTag();

            if(!event.getRegistry().hasNext()) break;
            event.getRegistry().getNextData(data);

            ItemStack stack = ((yorickbm.skyblockaddon.registries.GroupsRegistry)event.getRegistry()).getItemFor(data);
            if(stack == null) stack = event.processHolder(event.getItemStackHolder().clone(), data).getItemStack();

            event.drawItem(slot, stack);
        }
        event.setCanceled(true);
    }

    @SubscribeEvent()
    public void onIslandRegistryFiller(RegistryEvents.IslandRegistry event) {
        if(event.isCanceled()) return; //Event is canceled
        event.setCanceled(true);

        for (int slot = 0; slot < event.getSlots(); slot++) {
            if((slot < 10 || slot > event.getSlots() - 10)  || (slot%9 == 0 || slot%9 == 8)) continue;
            if(!event.getRegistry().hasNext()) break;

            CompoundTag data = new CompoundTag();
            event.getRegistry().getNextData(data);

           GUIItemStackHolder holder = event.processHolder(event.getItemStackHolder().clone(), data);
            event.drawItem(slot, holder.getItemStack());
        }
    }
}
