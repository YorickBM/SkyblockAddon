package yorickbm.skyblockaddon.util;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;

public class NBTUtil {

    public static CompoundTag Vec3iToNBT(Vec3i location) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", location.getX());
        tag.putInt("y", location.getY());
        tag.putInt("z", location.getZ());
        return tag;
    }

    public static Vec3i NBTToVec3i(CompoundTag tag) {
        return new Vec3i(tag.getInt("x"),tag.getInt("y"),tag.getInt("z"));
    }

}
