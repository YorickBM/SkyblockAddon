package yorickbm.skyblockaddon.util.NBT;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class NBTUtil {

    public static CompoundTag Vec3iToNBT(Vec3i location) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", location.getX());
        tag.putInt("y", location.getY());
        tag.putInt("z", location.getZ());
        return tag;
    }

    public static Vec3i NBTToVec3i(CompoundTag tag) {
        return new Vec3i(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
    }

    public static CompoundTag ItemStackToNBT(ItemStack item) {
        CompoundTag tag = new CompoundTag();

        tag.putString("registryName", Objects.requireNonNull(item.getItem().getRegistryName()).toString());
        tag.putString("name", Component.Serializer.toJson(item.getDisplayName()));
        tag.put("NBT", item.getOrCreateTag());

        return tag;
    }

    public static ItemStack NBTToItemStack(CompoundTag tag) {
        ItemStack item = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(tag.getString("registryName"))));
        item.setHoverName(Component.Serializer.fromJson(tag.getString("name")));
        item.setTag(tag.getCompound("NBT"));
        return item;
    }
}