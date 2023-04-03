package yorickbm.skyblockaddon.util;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IslandData {

    private UUID owner = null; //UUID Of owner
    private Vec3i spawn; //Spawn coordinates of island

    private final List<UUID> islandMembers = new ArrayList<>(); //List of all members of island

    /**
     * Load island data from CompoundTag
     * @param tag CompoundTag containing island data
     */
    public IslandData(CompoundTag tag) {
        if(tag.contains("owner")) owner = UUID.fromString(tag.getString("owner"));

        CompoundTag location = (CompoundTag) tag.get("spawn");
        spawn = new Vec3i(location.getInt("x"),location.getInt("y"),location.getInt("z"));

        CompoundTag members = (CompoundTag) tag.get("members");
        int count = members.getInt("count");
        for (int i = 0; i < count; i++) {
            islandMembers.add(UUID.fromString(members.getString("member-"+i)));
        }
    }

    /**
     * Allow for generation from legacy data
     */
    public IslandData(UUID playerUUID, Vec3i location) {
        owner = playerUUID;
        spawn = location;
    }

    /**
     * Remove player from members list, and make it the owner of the island
     * @param uuid Player you wish to make owner of island
     */
    public void setOwner(UUID uuid) {
        islandMembers.remove(uuid);
        owner = uuid;
    }

    /**
     * Add member to island by UUID
     * @param uuid UUID of p[layer you wish to add
     */
    public void addIslandMember(UUID uuid) {
        islandMembers.add(uuid);
    }

    /**
     * Remove member from island by uuid
     * @param uuid UUID of player you wish to remove
     * @return True or false if player was part of island
     */
    public boolean removeIslandMember(UUID uuid) {
        if(!islandMembers.contains(uuid)) return false;

        islandMembers.remove(uuid);
        return true;
    }

    /**
     * Change spawn location for island
     * @param location New location you wish islands spawn to be at
     */
    public void setSpawn(Vec3i location) {
        spawn = location;
    }

    /**
     * Get Vec3i for island spawn location
     * @return Vec3i
     */
    public Vec3i getSpawn() {
        if(spawn == null) return Vec3i.ZERO;
        return spawn;
    }

    /**
     * Check if certain player is owner of island
     * @param uuid UUID of player you wish to check
     * @return True or false based on fact if player is owner
     */
    public boolean isOwner(UUID uuid) {
        if (owner == null) return false;
        return owner.toString().equals(uuid.toString());
    }

    /**
     * Check if island is leaveable, or should be removed as the last player is leaving
     * @return True or false based on fact if the player may leave or requires to delete
     */
    public boolean canLeave() {
        return islandMembers.size() >= 1;
    }

    /**
     * Check if UUID is part of island members and by indication not owner
     * @param uuid UUID of player you wish to check
     * @return True or False based on fact if player is member of island
     */
    public boolean hasMember(UUID uuid) {
        return islandMembers.contains(uuid);
    }

    /**
     * Check if island has any member and/or owner part of the island.
     * If not island could be pured I.E
     * @return True or False
     */
    public boolean hasSomeone() {
        return owner != null || islandMembers.size() > 0;
    }

    /**
     * Get all members that are part of Island
     * @return List of UUID's
     */
    public List<UUID> getMembers() {
        return islandMembers;
    }

    /**
     * Turn island data into a NBT Tag contain its data that can be used to create a new instance.
     * @return NBT Tag that can be stored
     */
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        if(owner != null) tag.putString("owner", owner.toString());
        if(spawn == null) spawn = Vec3i.ZERO;

        CompoundTag location = new CompoundTag();
        location.putInt("x", spawn.getX());
        location.putInt("y", spawn.getY());
        location.putInt("z", spawn.getZ());
        tag.put("spawn", location);

        CompoundTag members = new CompoundTag();
        members.putInt("count",islandMembers.size());
        for(int i = 0; i < islandMembers.size(); i++)
            members.putString("member-"+i, islandMembers.get(i).toString());
        tag.put("members", members);

        return tag;
    }

    /**
     * Teleport player to islands spawn coordinates
     * @param player Player to teleport
     */
    public void teleport(Player player) {
        player.teleportTo(spawn.getX(), spawn.getY(), spawn.getZ());
    }
}
