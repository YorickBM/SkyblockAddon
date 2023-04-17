package yorickbm.skyblockaddon.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.apache.commons.lang3.mutable.MutableInt;
import yorickbm.skyblockaddon.Main;
import yorickbm.skyblockaddon.capabilities.IslandGeneratorProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class IslandData {

    private UUID owner = null; //UUID Of owner
    private Vec3i spawn; //Spawn coordinates of island
    private Vec3i center; //Spawn coordinates of island
    private String biome = "Unknown";

    private final List<UUID> islandMembers = new ArrayList<>(); //List of all members of island

    /**
     * Load island data from CompoundTag
     * @param tag CompoundTag containing island data
     */
    public IslandData(CompoundTag tag) {
        if(tag.contains("owner")) owner = UUID.fromString(tag.getString("owner"));

        CompoundTag slocation = (CompoundTag) tag.get("spawn");
        spawn = new Vec3i(slocation.getInt("x"),slocation.getInt("y"),slocation.getInt("z"));
        CompoundTag clocation = (CompoundTag) tag.get("center");
        center = new Vec3i(clocation.getInt("x"),clocation.getInt("y"),clocation.getInt("z"));

        biome = tag.getString("biome");

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
        center = location;
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
        if(!hasOwner()) setOwner(uuid); //Set owner
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

        CompoundTag slocation = new CompoundTag();
        slocation.putInt("x", spawn.getX());
        slocation.putInt("y", spawn.getY());
        slocation.putInt("z", spawn.getZ());
        tag.put("spawn", slocation);

        CompoundTag clocation = new CompoundTag();
        clocation.putInt("x", center.getX());
        clocation.putInt("y", center.getY());
        clocation.putInt("z", center.getZ());
        tag.put("center", clocation);

        tag.putString("biome", biome);

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
        ServerHelper.playSongToPlayer((ServerPlayer) player, SoundEvents.ENDERMAN_TELEPORT, Main.EFFECT_SOUND_VOL, 1f);
    }

    /**
     * Get Game Profile for owner of island
     * @return GameProfile
     */
    public GameProfile getOwner(MinecraftServer server) {
        try {
            if(hasOwner()) {
                Player player = server.getPlayerList().getPlayer(owner);
                if(player != null) return player.getGameProfile();
                else return new GameProfile(owner, UsernameCache.getBlocking(owner));
            }
        } catch (IOException e) {} //API will fail on offline servers
        return new GameProfile(UUID.randomUUID(), "Unknown");
    }

    /**
     * Check if island has owner
     * @return boolean
     */
    public boolean hasOwner() {
        return owner != null;
    }

    /**
     * Get biome name of island
     * @return Printable name of biome
     */
    public String getBiome() {
        return this.biome;
    }

    /**
     * Set islands biome
     * @param biome - Biome you wish to modify to
     */
    public void setBiome(ServerLevel serverlevel, Holder<Biome> biome, String name) {
        this.biome = name; //Set name for GUI
        BoundingBox boundingbox = this.getIslandBoundingBox();

        List<ChunkAccess> list = new ArrayList<>();
        MutableInt mutableint = new MutableInt(0);

        //Get all chunks within bounding box
        for(int j = SectionPos.blockToSectionCoord(boundingbox.minZ()); j <= SectionPos.blockToSectionCoord(boundingbox.maxZ()); ++j) {
            for(int k = SectionPos.blockToSectionCoord(boundingbox.minX()); k <= SectionPos.blockToSectionCoord(boundingbox.maxX()); ++k) {
                ChunkAccess chunkaccess = serverlevel.getChunk(k, j, ChunkStatus.FULL, false);
                if (chunkaccess == null) {
                    continue; //Skip unloaded chunks
                }

                list.add(chunkaccess);
            }
        }

        //Update chunks with biome
        for(ChunkAccess chunkaccess1 : list) {
            chunkaccess1.fillBiomesFromNoise(
                    makeResolver(mutableint, chunkaccess1, boundingbox, biome, (p_262543_) -> true),
                    serverlevel.getChunkSource().getGenerator().climateSampler());
            chunkaccess1.setUnsaved(true);
        }
    }

    /**
     * Get bounding box for island from center point
     * @return BoundingBox of Island
     */
    public BoundingBox getIslandBoundingBox() {
        BlockPos blockpos = quantize(new BlockPos(center.getX() - IslandGeneratorProvider.SIZE,0,center.getZ() - IslandGeneratorProvider.SIZE));
        BlockPos blockpos1 = quantize(new BlockPos(center.getX() + IslandGeneratorProvider.SIZE,256,center.getZ() + IslandGeneratorProvider.SIZE));
        return BoundingBox.fromCorners(blockpos, blockpos1);
    }

    /**
     * Biome Modification Utilities
     */
    private static int quantize(int p_261998_) {
        return QuartPos.toBlock(QuartPos.fromBlock(p_261998_));
    }
    private static BlockPos quantize(BlockPos p_262148_) {
        return new BlockPos(quantize(p_262148_.getX()), quantize(p_262148_.getY()), quantize(p_262148_.getZ()));
    }
    private static BiomeResolver makeResolver(MutableInt p_262615_, ChunkAccess p_262698_, BoundingBox p_262622_, Holder<Biome> p_262705_, Predicate<Holder<Biome>> p_262695_) {
        return (p_262550_, p_262551_, p_262552_, p_262553_) -> {
            int i = QuartPos.toBlock(p_262550_);
            int j = QuartPos.toBlock(p_262551_);
            int k = QuartPos.toBlock(p_262552_);
            Holder<Biome> holder = p_262698_.getNoiseBiome(p_262550_, p_262551_, p_262552_);
            if (p_262622_.isInside(new Vec3i(i, j, k)) && p_262695_.test(holder)) {
                p_262615_.increment();
                return p_262705_;
            } else {
                return holder;
            }
        };
    }
}
