package yorickbm.skyblockaddon.capabilities;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

public class PlayerIsland {

    private static final Logger LOGGER = LogManager.getLogger();

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
    private String islandId = "";
    private String oldIslandId = ""; //Allows to undo island leave through commando

    private HashMap<String, Long> islandInvites = new HashMap<>();
    private HashMap<UUID, Long> teleportInvites = new HashMap<>();

    /**
     * Check if invite is valid with criteria.
     * Within 60 minutes.
     * @param islandId Id of island to be invited to
     * @return Boolean
     */
    public boolean inviteValid(String islandId) {
        if(!islandInvites.containsKey(islandId)) return false; //We dont have invite registered for this id

        long timestamp = islandInvites.get(islandId);
        islandInvites.remove(islandId); //One time trigger validation

        return timestamp >= Instant.now().getEpochSecond() - 60 * 60; //Check if invite is not older then x seconds
    }

    /**
     * Add island invite to player
     * @param islandId IslandId you are inviting player for
     */
    public void addInvite(String islandId) {
        islandInvites.put(islandId, Instant.now().getEpochSecond());
    }

    /**
     * Check if teleport is still valid for player
     * @param player Players whom request you wish to confirm
     * @return Boolean
     */
    public boolean teleportValid(UUID player) {
        if(!teleportInvites.containsKey(player)) return false; //We dont have teleport request registered for this player

        long timestamp = teleportInvites.get(player);
        teleportInvites.remove(player); //One time trigger validation

        return timestamp <= Instant.now().getEpochSecond() - 60; //Check if invite is not older then x seconds
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
    public boolean hasOne() { return !islandId.equals(""); }

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
        nbt.putInt("nbt-v", 2);
        nbt.putString("islandId", islandId);

        return nbt;
    }

    /**
     * Load players island information from CompoundTag
     * @param nbt CompoundTag containing information
     */
    public void loadNBTData(CompoundTag nbt) {
        if(nbt.contains("nbt-v")) {
            switch (nbt.getInt("nbt-v")) {
                case 2 -> islandId = nbt.getString("islandId");
                default -> islandId = "";
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
    public void setIsland(String id) {
        oldIslandId = islandId;
        islandId = id;
    }

    /**
     * Get players previous island he/she/they was part of.
     * @return ID of island
     */
    public String getPreviousIsland() {
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
    public String getIslandId() {
        return islandId;
    }
}
