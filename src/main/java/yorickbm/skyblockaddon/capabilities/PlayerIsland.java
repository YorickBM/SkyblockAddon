package yorickbm.skyblockaddon.capabilities;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
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
    private String oldIslandId = ""; //Allows admin to undo island leave through commando

    public UUID request;
    public int requestType = -1;

    /**
     * Check if player is currently part of an island
     * @return True or False
     */
    public boolean hasOne() { return islandId != ""; }

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
            switch(nbt.getInt("nbt-v")) {
                case 2:
                    islandId = nbt.getString("islandId");
                    break;
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
     * @param oldStore
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
