package yorickbm.skyblockaddon.permissions.util;

import com.google.gson.Gson;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import yorickbm.skyblockaddon.gui.json.GuiItemHolder;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;
import yorickbm.skyblockaddon.util.NBT.NBTSerializable;

public class PermissionCategory implements JSONSerializable, NBTSerializable {

    protected String id;
    protected GuiItemHolder item;

    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(String json) {
        Gson gson = new Gson();
        PermissionCategory temp = gson.fromJson(json, PermissionCategory.class);

        this.id = temp.id;
        this.item = temp.item;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", this.id);
        tag.put("item", this.item.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
    }

    public ItemStack getItemStack() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", this.id);

        return this.item.getItemStack(null, tag);
    }
}
