package yorickbm.skyblockaddon.islands.data;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import yorickbm.skyblockaddon.util.NBT.NBTSerializable;
import yorickbm.skyblockaddon.util.NBT.NBTUtil;

import java.util.UUID;

public class IslandData implements NBTSerializable {
    private UUID id = null; //UUID Of island
    private UUID owner = null; //UUID Of owner
    private Vec3i spawn; //Spawn coordinates of island
    private Vec3i center; //Spawn coordinates of island
    private String biome = "Unknown";
    private boolean travelability = false;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOwner() {
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

    public boolean canTravel() {
        return travelability;
    }

    public void setTravelability(boolean travelability) {
        this.travelability = travelability;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putUUID("Id", getId());
        tag.putString("owner", getOwner().toString());
        tag.putString("biome", getBiome());
        tag.putBoolean("travelability", canTravel());
        tag.put("spawn", NBTUtil.Vec3iToNBT(getSpawn()));
        tag.put("center", NBTUtil.Vec3iToNBT(getCenter()));

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        setId(tag.getUUID("Id"));
        setOwner(UUID.fromString(tag.getString("owner")));
        setBiome(tag.getString("biome"));
        setTravelability(tag.getBoolean("travelability"));
        setSpawn(NBTUtil.NBTToVec3i(tag.getCompound("spawn")));
        setCenter(NBTUtil.NBTToVec3i(tag.getCompound("center")));
    }
}
