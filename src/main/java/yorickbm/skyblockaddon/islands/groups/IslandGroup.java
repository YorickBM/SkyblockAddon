package yorickbm.skyblockaddon.islands.groups;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import yorickbm.skyblockaddon.util.NBT.IsUnique;
import yorickbm.skyblockaddon.util.NBT.NBTSerializable;

import java.util.UUID;

public class IslandGroup implements IsUnique, NBTSerializable {

    private UUID uuid;
    private final ItemStack item;

    public IslandGroup() {
        this.uuid = UUID.randomUUID();
        this.item = new ItemStack(Items.PAPER);
    }
    public IslandGroup(UUID uuid, ItemStack item, boolean allowAll) {
        this.uuid = uuid;
        this.item = item;
    }

    public ItemStack getItem() {
        return this.item;
    }

    @Override
    public UUID getId() {
        return this.uuid;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("uuid", this.uuid);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.uuid = tag.getUUID("uuid");
    }
}
