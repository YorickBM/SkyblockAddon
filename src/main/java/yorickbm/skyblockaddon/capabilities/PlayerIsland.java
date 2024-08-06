package yorickbm.skyblockaddon.capabilities;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public class PlayerIsland {
    //Legacy data
    private Vec3i centerLocation = new Vec3i(0,0,0);
    private boolean isOwner = false;
    private boolean isLegacy = false;

    //Legacy functions
    public boolean isOwner() { return isOwner; }
    public Vec3i getLocation() {
        if(centerLocation != null && centerLocation.getX() == 0 && centerLocation.getY() == 0 && centerLocation.getZ() == 0) return null;
        return centerLocation;
    }

    //New data
<<<<<<< Updated upstream
    private String islandId = "";
    private String oldIslandId = ""; //Allows to undo island leave through commando

    private final HashMap<String, Long> islandInvites = new HashMap<>();
=======
    private UUID islandId = null;
    private UUID oldIslandId = null; //Allows to undo island leave through commando

    private final HashMap<UUID, Long> islandInvites = new HashMap<>();
>>>>>>> Stashed changes
    private final HashMap<UUID, Long> teleportInvites = new HashMap<>();
    private long creationTimestamp = Instant.now().getEpochSecond() - 220;

    /**
     * Check if invite is valid with criteria.
     * Within 60 minutes.
     * @param islandId Id of island to be invited to
     * @return Boolean
     */
<<<<<<< Updated upstream
    public boolean inviteValid(String islandId) {
=======
    public boolean inviteValid(UUID islandId) {
>>>>>>> Stashed changes
        Object timestamp = islandInvites.get(islandId);
        islandInvites.remove(islandId); //One time trigger validation

        return timestamp != null && (long)timestamp >= (Instant.now().getEpochSecond() - (60 * 60)); //Check if invite is not older then x seconds
    }

    /**
     * Add island invite to player
     * @param islandId IslandId you are inviting player for
     */
<<<<<<< Updated upstream
    public void addInvite(String islandId) {
=======
    public void addInvite(UUID islandId) {
>>>>>>> Stashed changes
        islandInvites.put(islandId, Instant.now().getEpochSecond());
    }

    /**
     * Check if teleport is still valid for player
     * @param player Players whom request you wish to confirm
     * @return Boolean
     */
    public boolean teleportValid(UUID player) {
        Object timestamp = teleportInvites.get(player);
        teleportInvites.remove(player); //One time trigger validation

        return timestamp != null && (long)timestamp >= Instant.now().getEpochSecond() - 60; //Check if invite is not older then x seconds
    }

    /**
     * Determine if island island can be created or wait is required
     * @return wait time
     */
    public long CreateIslandDelay() {
        long delay = creationTimestamp - (Instant.now().getEpochSecond() - 120); //Check if last attempt is  older then x seconds
        if(delay <= 0) creationTimestamp = Instant.now().getEpochSecond();
        return delay;
    }

    /**
     * Add island teleport request to player for player
     * @param player UUID of whom want to teleport
     */
    public void addTeleport(UUID player) {
        teleportInvites.put(player, Instant.now().getEpochSecond());
    }

    /**
     * Check if player is currently part of an island
     * @return True or False
     */
<<<<<<< Updated upstream
    public boolean hasOne() { return !islandId.isEmpty(); }
=======
    public boolean hasOne() { return islandId != null; }
>>>>>>> Stashed changes

    /**
     * Returns if the player data that has been loaded was legacy data or not
     * @return True or False
     */
    public boolean hasLegacyData() {
        return isLegacy;
    }

    /**
     * Save players island information into CompoundTag
     * @param nbt CompoundTag to save to
     * @return CompoundTag
     */
    public CompoundTag saveNBTData(CompoundTag nbt) {
<<<<<<< Updated upstream
        nbt.putInt("nbt-v", 2);
        nbt.putString("islandId", islandId);
=======
        nbt.putInt("nbt-v", 3);
        nbt.putUUID("islandId", islandId);
>>>>>>> Stashed changes

        return nbt;
    }

    /**
     * Load players island information from CompoundTag
     * @param nbt CompoundTag containing information
     */
    public void loadNBTData(CompoundTag nbt) {
        if(nbt.contains("nbt-v")) {
            if (nbt.getInt("nbt-v") == 2) {
<<<<<<< Updated upstream
                islandId = nbt.getString("islandId");
            } else {
                islandId = "";
=======
                islandId = UUID.fromString(nbt.getString("islandId"));
            } if (nbt.getInt("nbt-v") >= 3) {
                islandId = nbt.getUUID("islandId");
            } else {
                islandId = null;
>>>>>>> Stashed changes
            }
        } else {
            if(nbt.contains("loc-x")) centerLocation = new Vec3i(nbt.getInt("loc-x"),nbt.getInt("loc-y"),nbt.getInt("loc-z"));
            if(nbt.contains("isOwner")) isOwner = nbt.getBoolean("isOwner");
            isLegacy = true;
        }
    }

    /**
     * Set players island by ID
     * @param id ID of island to set
     */
<<<<<<< Updated upstream
    public void setIsland(String id) {
=======
    public void setIsland(UUID id) {
>>>>>>> Stashed changes
        oldIslandId = islandId;
        islandId = id;
    }

    /**
     * Get players previous island he/she/they was part of.
     * @return ID of island
     */
<<<<<<< Updated upstream
    public String getPreviousIsland() {
=======
    public UUID getPreviousIsland() {
>>>>>>> Stashed changes
        return oldIslandId;
    }

    /**
     * Function to clone data over
     * @param oldStore Old island
     */
    public void copyFrom(PlayerIsland oldStore) {
        this.islandId = oldStore.islandId;
        this.oldIslandId = oldStore.oldIslandId;
    }

    /**
     * Get ID for island currently player part of is
     * @return Island ID
     */
<<<<<<< Updated upstream
    public String getIslandId() {
=======
    public UUID getIslandId() {
>>>>>>> Stashed changes
        return islandId;
    }
}
