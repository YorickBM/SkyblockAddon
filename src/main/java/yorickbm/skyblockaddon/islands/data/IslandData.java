package yorickbm.skyblockaddon.islands.data;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.NotNull;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.util.BiomeUtil;
import yorickbm.skyblockaddon.util.NBT.NBTSerializable;
import yorickbm.skyblockaddon.util.NBT.NBTUtil;
import yorickbm.skyblockaddon.util.UsernameCache;
import yorickbm.skyblockaddon.util.geometry.Square;

import java.util.*;

public class IslandData implements NBTSerializable {
    private UUID id = UUID.randomUUID(); //UUID Of island
    private UUID owner = SkyblockAddon.MOD_UUID; //UUID Of owner
    private Vec3i spawn; //Spawn coordinates of island
    private Vec3i center; //Spawn coordinates of island
    private String biome = "Unknown";
    private boolean travelability = false;

    private final List<UUID> members = new ArrayList<>();
    private final Map<UUID, IslandGroup> islandGroups = new HashMap<>();
    private final List<ChunkPos> playerLoadedChunks = new ArrayList<>();

    public IslandData() {
    }

    public List<UUID> getMembers() {
        return this.members;
    }
    public void removeMember(final UUID entity, @NotNull final UUID id) {
        this.members.remove(entity);
        this.removeGroupMember(entity, id);
    }
    public void removeGroupMember(final UUID entity, @NotNull final UUID id) {
        this.islandGroups.get(id).removeMember(entity);
        if(this.members.contains(entity) && !this.isInAnyGroup(entity)) {
            this.addMember(entity, SkyblockAddon.MOD_UUID); //Add back into default group since he/she is an island member
        }
    }
    public boolean addMember(final UUID entity, @NotNull final UUID id) {
        if(getOwner().equals(entity)) return false;

        if(this.getOwner().equals(SkyblockAddon.MOD_UUID)) {
            setOwner(entity);
        } else {
            this.islandGroups.forEach(((uuid, islandGroup) -> islandGroup.removeMember(entity))); //Remove entity from all groups

            final IslandGroup group = this.islandGroups.get(id);
            if(group == null) return false;

            group.addMember(entity);
            if(id.equals(SkyblockAddon.MOD_UUID) && !this.members.contains(entity)) this.members.add(entity);
        }
        return true;
    }

    public Collection<IslandGroup> getGroups() { return islandGroups.values(); }
    public boolean hasGroup(final UUID uuid) {
        return islandGroups.containsKey(uuid);
    }
    public IslandGroup getGroup(final UUID uuid) {
        return islandGroups.get(uuid);
    }
    public void addGroup(final IslandGroup group) {
        islandGroups.put(group.getId(), group);
    }
    public boolean removeGroup(final UUID uuid) {
        if(islandGroups.size() <= 1 || uuid.equals(SkyblockAddon.MOD_UUID) || uuid.equals(SkyblockAddon.MOD_UUID2)) return false;
        if(!islandGroups.containsKey(uuid)) return false;

        if(!islandGroups.get(uuid).getMembers().isEmpty()) { //Move them all to default group
            islandGroups.get(uuid).getMembers().forEach(p -> {
                //Add island members from group to default island member group
                if(this.members.contains(p)) this.getMembersGroup().addMember(p);
                //Non-members will default back to default group as they are in no group
            });
        }

        islandGroups.remove(uuid);
        return true;
    }
    public boolean isInAnyGroup(final UUID entity) {
        return this.islandGroups.values().stream().anyMatch(ig -> ig.hasMember(entity));
    }

    public IslandGroup getDefaultGroup() { return this.getGroup(SkyblockAddon.MOD_UUID2); }
    public IslandGroup getMembersGroup() { return this.getGroup(SkyblockAddon.MOD_UUID); }

    public UUID getId() {
        return id;
    }
    public void setId(final UUID id) {
        this.id = id;
    }

    public UUID getOwner() {
        if (owner == null) return SkyblockAddon.MOD_UUID;
        return owner;
    }
    public void setOwner(final UUID owner) {
        this.owner = owner;
    }

    public Vec3i getSpawn() {
        return spawn;
    }
    public void setSpawn(final Vec3i spawn) {
        this.spawn = spawn;
    }

    public Vec3i getCenter() {
        return center;
    }
    protected void setCenter(final Vec3i center) {
        this.center = center;
    }

    public String getBiome() {
        return biome;
    }
    public void setBiome(final String biome) {
        this.biome = biome;
    }

    public boolean isVisible() {
        return travelability;
    }
    public void setVisibility(final boolean travelability) {
        this.travelability = travelability;
    }

    /**
     * Get bounding box for island from center point.
     *
     * @return BoundingBox of Island
     */
    public BoundingBox getIslandBoundingBox() {
        final int size = SkyblockAddon.ISLAND_SIZE;
        final BlockPos blockpos = BiomeUtil.quantize(new BlockPos(center.getX() - size, -450, center.getZ() - size));
        final BlockPos blockpos1 = BiomeUtil.quantize(new BlockPos(center.getX() + size, 450, center.getZ() + size));
        return BoundingBox.fromCorners(blockpos, blockpos1);
    }

    /**
     * Get Island Bounding Box as square geometry.
     *
     * @return Square Geometry
     */
    public Square getIslandBoundingBoxAsSquare() {
        final BoundingBox box = getIslandBoundingBox();
        return new Square(new Vec3i(box.minX(), 0, box.minZ()), new Vec3i(box.maxX(), 0, box.maxZ()));
    }

    @Override
    public CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();

        tag.putUUID("Id", getId());
        tag.putString("owner", getOwner().toString());
        tag.putString("biome", getBiome());
        tag.putBoolean("travelability", isVisible());
        tag.put("spawn", NBTUtil.Vec3iToNBT(getSpawn()));
        tag.put("center", NBTUtil.Vec3iToNBT(getCenter()));

        final CompoundTag groups = new CompoundTag();
        for(final IslandGroup group : getGroups()) {
            groups.put(group.getId().toString(), group.serializeNBT());
        }
        tag.put("groups", groups);

        final CompoundTag members = new CompoundTag();
        for(int i = 0; i < this.members.size(); i++) {
            members.putUUID(i+"", this.members.get(i));
        }
        tag.put("members", members);

        return tag;
    }

    @Override
    public void deserializeNBT(final CompoundTag tag) {
        setId(tag.getUUID("Id"));
        if (tag.getString("owner").length() > 3) setOwner(UUID.fromString(tag.getString("owner")));

        setBiome(tag.getString("biome"));
        setVisibility(tag.getBoolean("travelability"));
        setSpawn(NBTUtil.NBTToVec3i(tag.getCompound("spawn")));
        setCenter(NBTUtil.NBTToVec3i(tag.getCompound("center")));

        final CompoundTag groups = tag.getCompound("groups");
        for(final String groupUuid : groups.getAllKeys()) {
            final IslandGroup group = new IslandGroup();
            group.deserializeNBT(groups.getCompound(groupUuid));
            this.islandGroups.put(group.getId(), group);
        }

        if(this.islandGroups.isEmpty() || this.islandGroups.size() < 2) {
            final ItemStack item = new ItemStack(Items.RED_MUSHROOM);
            item.setHoverName(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("gui.group.default.name")).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));

            final IslandGroup defaultG = new IslandGroup(SkyblockAddon.MOD_UUID, item, true);
            this.islandGroups.put(defaultG.getId(), defaultG);

            final ItemStack item2 = new ItemStack(Items.BROWN_MUSHROOM);
            item2.setHoverName(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("gui.group.nonmember.name")).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));

            final IslandGroup defaultG2 = new IslandGroup(SkyblockAddon.MOD_UUID2, item2, true);
            this.islandGroups.put(defaultG2.getId(), defaultG2);
        }

        final CompoundTag members = tag.getCompound("members");
        for(final String key : members.getAllKeys()) {
            this.members.add(members.getUUID(key));
        }

    }

    public String getPermissionState(final String id, final UUID groupId) {
        return islandGroups.get(groupId).canDo(id) ? SkyBlockAddonLanguage.getLocalizedString("island.permission.enabled") : SkyBlockAddonLanguage.getLocalizedString("island.permission.disabled");
    }

    public Optional<IslandGroup> getGroupForEntity(final Entity entity) {
        return getGroupForEntityUUID(entity.getUUID());
    }

    public Optional<IslandGroup> getGroupForEntityUUID(final UUID uuid) {
        Optional<IslandGroup> result = islandGroups.values().stream().filter(g -> g.hasMember(uuid)).findFirst();
        if(result.isEmpty()) result = Optional.of(getDefaultGroup());
        return result;
    }

    public Optional<UUID> getGroupByName(final String groupName) {
        return getGroups().stream().filter(g -> g.getItem().getDisplayName().getString().trim().equalsIgnoreCase(groupName)).map(IslandGroup::getId).findFirst();
    }

    private String name = "";
    public String getName() {
        if(this.name.isEmpty()) {
            UsernameCache.get(getOwner()).thenAccept(r -> this.name = r);
        }
        return this.name.isEmpty() ? "..." : this.name;
    }
    public void updateName() {
        UsernameCache.get(getOwner()).thenAccept(r -> this.name = r);
    }

    /**
     * Determine whether there are still members and/or an owner present on the island.
     * @return true - if abandoned
     */
    public boolean isAbandoned() {
        final boolean hasOwner = !this.getOwner().equals(SkyblockAddon.MOD_UUID);
        final boolean hasMembers = !this.getMembers().isEmpty();

        //If members found, but no owner we make a random member owner
        //Should not be possible but here to protect against edge cases.
        if(!hasOwner && hasMembers) this.getMembers().stream().findFirst().ifPresent(this::setOwner);

        return !hasOwner && !hasMembers;
    }

    public List<ChunkPos> getModifiedChunks() {
        return Collections.unmodifiableList(playerLoadedChunks);
    }
    public boolean storeChunk(final ChunkAccess chunk) {
        if(playerLoadedChunks.contains(chunk.getPos())) return false;
        return playerLoadedChunks.add(chunk.getPos());
    }

}
