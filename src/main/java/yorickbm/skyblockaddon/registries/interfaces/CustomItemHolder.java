package yorickbm.skyblockaddon.registries.interfaces;


import net.minecraft.nbt.CompoundTag;
import yorickbm.guilibrary.GUIItemStackHolder;

public interface CustomItemHolder {
    GUIItemStackHolder getItemFor(CompoundTag tag);
}
