package yorickbm.skyblockaddon.registries.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface CustomItemStack {
    ItemStack getItemFor(CompoundTag tag);
}
