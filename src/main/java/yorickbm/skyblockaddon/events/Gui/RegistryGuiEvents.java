package yorickbm.skyblockaddon.events.Gui;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yorickbm.guilibrary.GUIItemStackHolder;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.registries.RegistryEvents;

public class RegistryGuiEvents {

    @SubscribeEvent
    public void onBiomeRegistryFiller(final RegistryEvents.BiomeRegistry event) {
        if(event.isCanceled()) return; //Event is canceled
        event.setCanceled(true);

        for (int slot = 0; slot < event.getSlots(); slot++) {
            if((slot < 10 || slot > event.getSlots() - 10)  || (slot%9 == 0 || slot%9 == 8)) continue;
            final CompoundTag data = new CompoundTag();

            if(!event.getRegistry().hasNext()) break;
            event.getRegistry().getNextData(data);

            final GUIItemStackHolder holder = event.processHolder(event.getItemStackHolder().clone(), data);
            holder.setItem(((yorickbm.skyblockaddon.registries.BiomeRegistry) event.getRegistry()).getItemFor(data));

            final ItemStack stack = holder.getItemStack();
            stack.getOrCreateTag().put(SkyblockAddon.MOD_ID, data);

            event.drawItem(slot, stack);
        }
    }

    @SubscribeEvent
    public void onGroupRegistryFiller(final RegistryEvents.GroupsRegistry event) {
        if(event.isCanceled()) return; //Event is canceled
        event.setCanceled(true);

        for (int slot = 0; slot < event.getSlots(); slot++) {
            if((slot < 10 || slot > event.getSlots() - 10)  || (slot%9 == 0 || slot%9 == 8)) continue;
            final CompoundTag data = new CompoundTag();

            if(!event.getRegistry().hasNext()) break;
            event.getRegistry().getNextData(data);

            ItemStack stack = ((yorickbm.skyblockaddon.registries.GroupsRegistry)event.getRegistry()).getItemFor(data);
            if(stack == null) stack = event.processHolder(event.getItemStackHolder().clone(), data).getItemStack();

            event.drawItem(slot, stack);
        }
    }

    @SubscribeEvent
    public void onPermissionRegistryFiller(final RegistryEvents.PermissionsRegistry event) {
        if(event.isCanceled()) return; //Event is canceled
        event.setCanceled(true);

        for (int slot = 0; slot < event.getSlots(); slot++) {
            if((slot < 10 || slot > event.getSlots() - 10)  || (slot%9 == 0 || slot%9 == 8)) continue;
            final CompoundTag data = new CompoundTag();

            if(!event.getRegistry().hasNext()) break;
            event.getRegistry().getNextData(data);

            GUIItemStackHolder stack = ((yorickbm.skyblockaddon.registries.PermissionRegistry)event.getRegistry()).getItemFor(data);
            if(stack == null) stack = event.getItemStackHolder().clone();

            event.drawItem(slot, event.processHolder(stack, data).getItemStack());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onDefaultRegistryFiller(final RegistryEvents event) {
        if(event.isCanceled()) return; //Event is canceled
        event.setCanceled(true);

        for (int slot = 0; slot < event.getSlots(); slot++) {
            if((slot < 10 || slot > event.getSlots() - 10)  || (slot%9 == 0 || slot%9 == 8)) continue;
            if(!event.getRegistry().hasNext()) break;

            final CompoundTag data = new CompoundTag();
            event.getRegistry().getNextData(data);

            final GUIItemStackHolder holder = event.processHolder(event.getItemStackHolder().clone(), data);
            final ItemStack stack = holder.getItemStack();
            stack.getOrCreateTag().put(SkyblockAddon.MOD_ID, data);

            event.drawItem(slot, stack);
        }
    }
}
