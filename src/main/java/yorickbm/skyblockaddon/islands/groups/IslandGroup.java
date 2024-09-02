package yorickbm.skyblockaddon.islands.groups;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import yorickbm.skyblockaddon.util.NBT.IsUnique;
import yorickbm.skyblockaddon.util.NBT.NBTSerializable;
import yorickbm.skyblockaddon.util.NBT.NBTUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IslandGroup implements IsUnique, NBTSerializable {

    private UUID uuid;
    private ItemStack item;

    private final List<UUID> members = new ArrayList<>();

    public IslandGroup() {
        this.uuid = UUID.randomUUID();
        this.item = new ItemStack(Items.PAPER);
    }
    public IslandGroup(UUID uuid, ItemStack item, boolean allowAll) {
        this.uuid = uuid;
        this.item = item;
    }

    public List<UUID> getMembers() {
        return this.members;
    }
    public void addMember(UUID entity) {
        this.members.add(entity);
    }
    public void removeMember(UUID entity) {
        this.members.remove(entity);
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

        CompoundTag members = new CompoundTag();
        for(int i = 0; i < this.members.size(); i++) {
            members.putUUID(i+"", this.members.get(i));
        }
        tag.put("members", members);

        tag.put("item", NBTUtil.ItemStackToNBT(this.item));

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.uuid = tag.getUUID("uuid");

        CompoundTag members = tag.getCompound("members");
        for(String key : members.getAllKeys()) {
            this.members.add(members.getUUID(key));
        }

        this.item = NBTUtil.NBTToItemStack(tag.getCompound("item"));

    }
}
