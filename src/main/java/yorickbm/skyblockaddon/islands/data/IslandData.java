package yorickbm.skyblockaddon.islands.data;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.util.BiomeUtil;
import yorickbm.skyblockaddon.util.NBT.NBTSerializable;
import yorickbm.skyblockaddon.util.NBT.NBTUtil;
import yorickbm.skyblockaddon.util.geometry.Square;

import java.util.*;

public class IslandData implements NBTSerializable {
    private static final Logger LOGGER = LogManager.getLogger();

    private UUID id = UUID.randomUUID(); //UUID Of island
    private UUID owner = SkyblockAddon.MOD_UUID; //UUID Of owner
    private Vec3i spawn; //Spawn coordinates of island
    private Vec3i center; //Spawn coordinates of island
    private String biome = "Unknown";
    private boolean travelability = false;

    private final List<UUID> members = new ArrayList<>();
    private final Map<UUID, IslandGroup> islandGroups = new HashMap<>();

    public IslandData() {
    }

    public List<UUID> getMembers() {
        return this.members;
    }
    public void removeMember(UUID entity, @NotNull UUID id) {
        this.members.remove(entity);
        this.removeGroupMember(entity, id);
    }
    public void removeGroupMember(UUID entity, @NotNull UUID id) {
        this.islandGroups.get(id).removeMember(entity);
        if(this.members.contains(entity) && !this.isInAnyGroup(entity)) {
            this.addMember(entity, SkyblockAddon.MOD_UUID); //Add back into default group since he/she is an island member
        }
    }
    public boolean addMember(UUID entity, @NotNull UUID id) {
        if(getOwner().equals(entity)) return false;

        if(this.getOwner().equals(SkyblockAddon.MOD_UUID)) {
            setOwner(entity);
        } else {
            this.islandGroups.forEach(((uuid, islandGroup) -> islandGroup.removeMember(entity))); //Remove entity from all groups
            if(id.equals(SkyblockAddon.MOD_UUID) && !this.members.contains(entity)) this.members.add(entity);
            this.islandGroups.get(id).addMember(entity);
        }
        return true;
    }

    public Collection<IslandGroup> getGroups() { return islandGroups.values(); }
    public IslandGroup getGroup(UUID uuid) { return islandGroups.get(uuid); }
    public void addGroup(IslandGroup group) {islandGroups.put(group.getId(), group);}
    public boolean removeGroup(UUID uuid) {
        if(islandGroups.size() <= 1) return false;
        islandGroups.remove(uuid);
        return true;
    }
    public boolean isInAnyGroup(UUID entity) {
        return this.islandGroups.values().stream().anyMatch(ig -> ig.hasMember(entity));
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOwner() {
        if (owner == null) return SkyblockAddon.MOD_UUID;
        return owner;
    }
    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Vec3i getSpawn() {
        return spawn;
    }
    public void setSpawn(Vec3i spawn) {
        this.spawn = spawn;
    }

    public Vec3i getCenter() {
        return center;
    }
    protected void setCenter(Vec3i center) {
        this.center = center;
    }

    public String getBiome() {
        return biome;
    }
    public void setBiome(String biome) {
        this.biome = biome;
    }

    public boolean isVisible() {
        return travelability;
    }
    public void setVisibility(boolean travelability) {
        this.travelability = travelability;
    }

    /**
     * Get bounding box for island from center point.
     *
     * @return BoundingBox of Island
     */
    public BoundingBox getIslandBoundingBox() {
        int size = SkyblockAddon.ISLAND_SIZE;
        BlockPos blockpos = BiomeUtil.quantize(new BlockPos(center.getX() - size, -100, center.getZ() - size));
        BlockPos blockpos1 = BiomeUtil.quantize(new BlockPos(center.getX() + size, 350, center.getZ() + size));
        return BoundingBox.fromCorners(blockpos, blockpos1);
    }

    /**
     * Get Island Bounding Box as square geometry.
     *
     * @return Square Geometry
     */
    public Square getIslandBoundingBoxAsSquare() {
        BoundingBox box = getIslandBoundingBox();
        return new Square(new Vec3i(box.minX(), 0, box.minZ()), new Vec3i(box.maxX(), 0, box.maxZ()));
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putUUID("Id", getId());
        tag.putString("owner", getOwner().toString());
        tag.putString("biome", getBiome());
        tag.putBoolean("travelability", isVisible());
        tag.put("spawn", NBTUtil.Vec3iToNBT(getSpawn()));
        tag.put("center", NBTUtil.Vec3iToNBT(getCenter()));

        CompoundTag groups = new CompoundTag();
        for(IslandGroup group : getGroups()) {
            groups.put(group.getId().toString(), group.serializeNBT());
        }
        tag.put("groups", groups);

        CompoundTag members = new CompoundTag();
        for(int i = 0; i < this.members.size(); i++) {
            members.putUUID(i+"", this.members.get(i));
        }
        tag.put("members", members);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        setId(tag.getUUID("Id"));
        if (tag.getString("owner").length() > 3) setOwner(UUID.fromString(tag.getString("owner")));
        setBiome(tag.getString("biome"));
        setVisibility(tag.getBoolean("travelability"));
        setSpawn(NBTUtil.NBTToVec3i(tag.getCompound("spawn")));
        setCenter(NBTUtil.NBTToVec3i(tag.getCompound("center")));

        CompoundTag groups = tag.getCompound("groups");
        for(String groupUuid : groups.getAllKeys()) {
            IslandGroup group = new IslandGroup();
            group.deserializeNBT(groups.getCompound(groupUuid));
            this.islandGroups.put(group.getId(), group);
        }

        if(this.islandGroups.isEmpty()) {
            ItemStack item = new ItemStack(Items.RED_MUSHROOM);
            item.setHoverName(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("gui.group.default.name")).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));

            IslandGroup defaultG = new IslandGroup(SkyblockAddon.MOD_UUID, item, true);
            this.islandGroups.put(defaultG.getId(), defaultG);
        }

        CompoundTag members = tag.getCompound("members");
        for(String key : members.getAllKeys()) {
            this.members.add(members.getUUID(key));
        }

    }

    public String getPermissionState(String id, UUID groupId) {
        return islandGroups.get(groupId).canDo(id) ? SkyBlockAddonLanguage.getLocalizedString("island.permission.enabled") : SkyBlockAddonLanguage.getLocalizedString("island.permission.disabled");
    }

    public Optional<IslandGroup> getGroupForEntity(Entity entity) {
        return islandGroups.values().stream().filter(g -> g.hasMember(entity.getUUID())).findFirst();
    }

    public Optional<IslandGroup> getGroupForEntityUUID(UUID uuid) {
        return islandGroups.values().stream().filter(g -> g.hasMember(uuid)).findFirst();
    }
}
