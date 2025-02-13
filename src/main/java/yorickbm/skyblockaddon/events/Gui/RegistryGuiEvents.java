package yorickbm.skyblockaddon.events.Gui;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.guilibrary.GUIItemStackHolder;
import yorickbm.skyblockaddon.registries.RegistryEvents;

public class RegistryGuiEvents {
    //TODO Add registries on filler items

    @SubscribeEvent
    public void onBiomeRegistryFiller(RegistryEvents.BiomeRegistry event) {
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
}
