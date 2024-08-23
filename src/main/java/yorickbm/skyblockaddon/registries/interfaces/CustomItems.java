package yorickbm.skyblockaddon.registries.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

public interface CustomItems {
    Item getItemFor(CompoundTag tag);
}
