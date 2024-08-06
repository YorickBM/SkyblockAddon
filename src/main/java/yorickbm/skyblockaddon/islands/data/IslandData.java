package yorickbm.skyblockaddon.islands.data;

import net.minecraft.core.Vec3i;

import java.util.UUID;

public class IslandData {
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

    public void setCenter(Vec3i center) {
        this.center = center;
    }

    public String getBiome() {
        return biome;
    }

    public void setBiome(String biome) {
        this.biome = biome;
    }

    public boolean isTravelability() {
        return travelability;
    }

    public void setTravelability(boolean travelability) {
        this.travelability = travelability;
    }
}
