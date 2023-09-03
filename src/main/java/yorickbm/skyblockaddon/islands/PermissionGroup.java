package yorickbm.skyblockaddon.islands;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.islands.permissions.Permission;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.*;

public class PermissionGroup {

    private static final Logger LOGGER = LogManager.getLogger();

    private final String name;
    private final Item displayItem;
    private final HashMap<Permissions, Permission> permissions;
    private final boolean canBeRemoved;
    private final List<UUID> members = new ArrayList<>();

    public PermissionGroup(String name, Item displayItem, boolean defaultState) {
        this.name = name;
        this.permissions = new HashMap<>();
        this.displayItem = displayItem;
        this.canBeRemoved = !name.equals("Admin") && !name.equals("Default") && !name.equals("Members");

        for(Permissions perm : Permissions.values()) {
            try {
                Class<? extends  Permission> c = (Class<? extends Permission>) Class.forName("yorickbm.skyblockaddon.islands.permissions." + perm.name());
                this.permissions.put(perm, c.getConstructor(boolean.class).newInstance(defaultState));
            } catch (Exception ex) {
                LOGGER.warn(perm.name() + " permission has not been initialized!");
                LOGGER.error(ex);
            }
        }
    }

    public PermissionGroup(CompoundTag tag) {
        this.name = tag.getString("name");
        this.displayItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(tag.getString("displayItem")));
        this.canBeRemoved = !name.equals("Admin") && !name.equals("Default") && !name.equals("Members");
        this.permissions = new HashMap<>();

        for(Permissions perm : Permissions.values()) {
            try {
                Class<? extends  Permission> c = (Class<? extends Permission>) Class.forName("yorickbm.skyblockaddon.islands.permissions." + perm.name());
                this.permissions.put(perm, c.getConstructor(boolean.class).newInstance(tag.getBoolean(perm.name())));
            } catch (Exception ex) {
                LOGGER.warn(perm.name() + " permission has not been initialized!");
                LOGGER.error(ex);
            }
        }

        CompoundTag members = tag.getCompound("members");
        for(int i = 0; i < members.getInt("count"); i++) {
            addMember(members.getUUID("member-" + i));
        }
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", this.name);
        tag.putString("displayItem", this.displayItem.getRegistryName().getPath());

        for(Permissions perm : Permissions.values()) {
            tag.putBoolean(perm.name(), this.permissions.get(perm).getState());
        }

        CompoundTag members = new CompoundTag();
        members.putInt("count", this.members.size());
        for(int i = 0; i < this.members.size(); i++) {
            members.putUUID("member-" + i, this.members.get(i));
        }
        tag.put("members", members);

        return tag;
    }

    public void addMember(UUID member) {
        if(!hasMember(member)) members.add(member);
    }
    public void removeMember(UUID member) {
        members.remove(member);
    }
    public boolean hasMember(UUID member) {
        return members.contains(member);
    }

    public String getName() { return this.name; }
    public boolean canBeRemoved() { return this.canBeRemoved; }

    public Permission getPermission(Permissions permission) {
        return permissions.get(permission);
    }
    public Collection<Permission> getPermissions() { return permissions.values(); }

    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(this.displayItem);
        item.setHoverName(ServerHelper.formattedText(this.name, ChatFormatting.BLUE, ChatFormatting.BOLD));
        return item;
    }

    public List<UUID> getMembers() { return this.members; }
}
