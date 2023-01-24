package yorickbm.skyblockaddon.capabilities;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.Timestamp;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerIsland {

    private static final Logger LOGGER = LogManager.getLogger();
    private Vec3i centerLocation = new Vec3i(0,0,0);
    private boolean isOwner = false;
    private UUID invite = null;
    private UUID teleport = null;
    private Date teleportRequestDate = null;

    public Vec3i getLocation() {
        if(centerLocation != null && centerLocation.getX() == 0 && centerLocation.getY() == 0 && centerLocation.getZ() == 0) return null;
        return centerLocation;
    }

    public void createIsland(Vec3i location, Player player) {
        isOwner = true;
        centerLocation = location;

        player.teleportTo(getLocation().getX(), getLocation().getY(), getLocation().getZ()); //Teleport player to island
    }

    public boolean acceptTeleport(Player player) {
        teleport = null;
        if(!hasOne()) return false;

        player.teleportTo(getLocation().getX(), getLocation().getY(), getLocation().getZ());
        return true;
    }

    public void joinIsland(PlayerIsland island, Player player) {
        invite = null;
        centerLocation = island.getLocation();
        player.teleportTo(getLocation().getX(), getLocation().getY(), getLocation().getZ()); //Teleport player to island
    }

    public void leaveIsland(Player player) {
        //Teleport user to spawn
        if(new Vec3(player.position().x, 0, player.position().z).distanceTo(new Vec3(getLocation().getX(), 0, getLocation().getZ())) > IslandGeneratorProvider.SIZE) {
            player.teleportTo(IslandGeneratorProvider.DEFAULT_SPAWN.getX(), IslandGeneratorProvider.DEFAULT_SPAWN.getY(), IslandGeneratorProvider.DEFAULT_SPAWN.getZ());
        }

        //Check if user is owner of island
        if(!isOwner) {
            centerLocation = new Vec3i(0,0,0);
            return;
        }
        isOwner = false;

//        AtomicBoolean foundSomeone = new AtomicBoolean(false);
//        for(Player ply : player.getServer().getPlayerList().getPlayers()) {
//            ply.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(island -> {
//                if(island.getLocation().distManhattan(getLocation()) < 1) {
//                    island.isOwner = true;
//                    foundSomeone.set(true);
//                }
//            });
//
//            if(foundSomeone.get()) return;
//        }
//
//        player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(generator -> {
//            generator.destroyIsland(player.getLevel(), getLocation());
//        });

        centerLocation = new Vec3i(0,0,0);
    }

    public void sendInvite(UUID inviter) {
        invite = inviter;
    }
    public void sendTeleportInvite(UUID requester) {
        teleport = requester;
        teleportRequestDate = new Date();
    }

    public boolean hasOne() { return getLocation() != null; }
    public boolean isOwner() { return isOwner; }

    public void copyFrom(PlayerIsland source) {
        centerLocation = source.centerLocation;
        isOwner = source.isOwner;
        invite = source.invite;
    }

    public CompoundTag saveNBTData(CompoundTag nbt) {
        //LOGGER.info("SAVING PLAYER NBT...");
        nbt.putInt("loc-x", centerLocation.getX());
        nbt.putInt("loc-y", centerLocation.getY());
        nbt.putInt("loc-z", centerLocation.getZ());
        nbt.putBoolean("isOwner", isOwner);
//        LOGGER.info("DONE!!");
//        LOGGER.info("Isowner? - " + isOwner + " " + nbt.getBoolean("isOwner"));
//        LOGGER.info("X - " + centerLocation.getX() + " " + nbt.getInt("loc-x"));
//        LOGGER.info("Y - " + centerLocation.getY() + " " + nbt.getInt("loc-y"));
//        LOGGER.info("Z - " + centerLocation.getZ() + " " + nbt.getInt("loc-z"));
        return nbt;
    }

    public void loadNBTData(CompoundTag nbt) {
        //LOGGER.info("LOADING PLAYER NBT");
        if(nbt.contains("loc-x")) centerLocation = new Vec3i(nbt.getInt("loc-x"),nbt.getInt("loc-y"),nbt.getInt("loc-z"));
        else LOGGER.warn("COULD NOT GET LOCATION!");
        if(nbt.contains("isOwner")) isOwner = nbt.getBoolean("isOwner");
        else LOGGER.warn("COULD NOT GET OWNERSHIP!");

//        LOGGER.info("Isowner? - " + isOwner + " " + nbt.getBoolean("isOwner"));
//        LOGGER.info("X - " + centerLocation.getX() + " " + nbt.getInt("loc-x"));
//        LOGGER.info("Y - " + centerLocation.getY() + " " + nbt.getInt("loc-y"));
//        LOGGER.info("Z - " + centerLocation.getZ() + " " + nbt.getInt("loc-z"));
    }

    public UUID getInvite() {
        return invite;
    }
    public UUID getTeleportInvite() {
        if(teleportRequestDate == null || new Date().after(new Date(teleportRequestDate.getTime() + 300000))) {
            teleport = null;
            teleportRequestDate = null;
            return null;
        }
        return teleport;
    }
}
