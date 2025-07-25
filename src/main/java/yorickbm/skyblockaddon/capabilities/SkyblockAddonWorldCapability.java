package yorickbm.skyblockaddon.capabilities;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.configs.SkyblockAddonConfig;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.data.IslandData;
import yorickbm.skyblockaddon.legacy.LegacyFormatter;
import yorickbm.skyblockaddon.util.BuildingBlock;
import yorickbm.skyblockaddon.util.NBT.NBTEncoder;
import yorickbm.skyblockaddon.util.NBT.NBTUtil;
import yorickbm.skyblockaddon.util.ResourceManager;
import yorickbm.skyblockaddon.util.exceptions.NBTNotFoundException;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SkyblockAddonWorldCapability {
    private static final Logger LOGGER = LogManager.getLogger();

    Vec3i lastLocation;
    Queue<Vec3i> reusableLocations;
    HashMap<UUID, Island> islandsByUUID;

    //Reverse Lookup Caches
    LoadingCache<UUID, Optional<UUID>> CACHE_islandByPlayerUUID;
    LoadingCache<BoundingBox, Optional<UUID>> CACHE_islandByBoundingBox;

    MinecraftServer serverInstance;

    public SkyblockAddonWorldCapability(final MinecraftServer server) {
        serverInstance = server;

        islandsByUUID = new HashMap<>();
        lastLocation = Vec3i.ZERO;
        reusableLocations = new LinkedList();

        initializeCaches(server);
    }

    public void clearCacheForPlayer(final UUID uuid) {
        CACHE_islandByPlayerUUID.invalidate(uuid);
        CACHE_islandByPlayerUUID.put(uuid, Optional.empty());
    }

    /**
     * Creates reverse lookup caches for optimized performance.
     */
    public void initializeCaches(final MinecraftServer server) {
        CACHE_islandByPlayerUUID = CacheBuilder.newBuilder()
                .expireAfterAccess(6, TimeUnit.HOURS)
                .maximumSize((long) Math.floor(server.getMaxPlayers() * 2.5))
                .build(new CacheLoader<>() {
                    @Override
                    public @NotNull Optional<UUID> load(@Nonnull final UUID key) {
                        final Optional<Island> island = islandsByUUID.values().stream().filter(isl -> isl.isPartOf(key)).findFirst();
                        return island.map(IslandData::getId);
                    }
                });

        CACHE_islandByBoundingBox = CacheBuilder.newBuilder()
                .expireAfterAccess(24, TimeUnit.HOURS)
                .build(new CacheLoader<>() {
                    @Override
                    public @NotNull Optional<UUID> load(@Nonnull final BoundingBox key) {
                        final Optional<Island> island = islandsByUUID.values().stream().filter(isl -> isl.getIslandBoundingBox().isInside(key.getCenter())).findFirst();
                        return island.map(IslandData::getId);
                    }
                });
    }

    /**
     * Fill reverse lookup caches with data for entity.
     * I.E. Island UUID for entity.
     *
     * @param entity - Player whose data is loaded into reverse lookup cache
     */
    public void loadIslandIntoReverseCache(final Entity entity) {
        try {
            final Optional<UUID> island = CACHE_islandByPlayerUUID.get(entity.getUUID());
            if (island.isEmpty()) return; //Got no island
            getIslandByUUID(island.get()).getName(); //Load owners name into cache
            CACHE_islandByBoundingBox.put(getIslandByUUID(island.get()).getIslandBoundingBox(), island); //Store into bounding box cache
        } catch (final ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get island by its UUID
     *
     * @param id - UUID to lookup
     * @return - Island associated with islandId
     */
    public Island getIslandByUUID(final UUID id) {
        return islandsByUUID.get(id);
    }

    /**
     * Get island where entity is a part of.
     *
     * @param uuid - UUID to check
     * @return - Island of entity
     */
    public Island getIslandByEntityUUID(final UUID uuid) {
        final Optional<UUID> islandId = CACHE_islandByPlayerUUID.getIfPresent(uuid); //Check if cache contains island.
        if(islandId == null || islandId.isEmpty()) {
            final Optional<Island> island = islandsByUUID.values().stream().filter(isl -> isl.isPartOf(uuid)).findFirst();
            island.ifPresent(value -> CACHE_islandByPlayerUUID.put(uuid, Optional.ofNullable(value.getId())));
            return island.orElse(null);
        }

        return islandId.map(this::getIslandByUUID).orElse(null);
    }

    /**
     * Get the island where the entity is currently standing on.
     *
     * @param entity - Entity whom to check
     * @return - Island entity is on
     */
    public Island getIslandPlayerIsStandingOn(final Entity entity) {
        return getIslandByPos(entity.getOnPos());
    }

    /**
     * Get the island for a specific block position
     * @param pos - Block position
     * @return - Island block position falls in
     */
    public Island getIslandByPos(final BlockPos pos) {
        final Optional<UUID> islandId = CACHE_islandByBoundingBox.asMap().entrySet().stream()
                .filter(entry -> entry.getKey().isInside(pos))
                .map(Map.Entry::getValue)
                .findFirst().orElse(Optional.empty());
        if (islandId.isPresent()) return getIslandByUUID(islandId.get());

        final Optional<Island> island = islandsByUUID.values().stream().filter(isl -> isl.getIslandBoundingBox().isInside(pos)).findFirst();
        island.ifPresent(value -> CACHE_islandByBoundingBox.put(value.getIslandBoundingBox(), Optional.of(value.getId()))); //Store island into cache
        return island.orElse(null);
    }

    /**
     * Save data into NBT.
     */
    public void saveNBTData(final CompoundTag nbt) {
        nbt.put("lastIsland", NBTUtil.Vec3iToNBT(lastLocation));
        nbt.putInt("nbt-v", 6);

        ListTag listTag = new ListTag();
        for (Vec3i vec : reusableLocations) {
            CompoundTag vecTag = new CompoundTag();
            vecTag.putInt("x", vec.getX());
            vecTag.putInt("y", vec.getY());
            vecTag.putInt("z", vec.getZ());
            listTag.add(vecTag);
        }
        nbt.put("reusableLocations", listTag);

        final Path worldPath = serverInstance.getWorldPath(LevelResource.ROOT).normalize();
        final Path filePath = worldPath.resolve("islanddata");

        NBTEncoder.saveToFile(getIslands(), filePath);
    }

    /**
     * Load data from NBT.
     */
    public void loadNBTData(final CompoundTag nbt) {
        lastLocation = NBTUtil.NBTToVec3i(nbt.getCompound("lastIsland"));

        final Path worldPath = serverInstance.getWorldPath(LevelResource.ROOT).normalize();
        final Path filePath = worldPath.resolve("islanddata");

        if (nbt.contains("reusableLocations", 9)) { // 9 = ListTag type
            ListTag listTag = nbt.getList("reusableLocations", 10); // 10 = CompoundTag type
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag vecTag = listTag.getCompound(i);
                int x = vecTag.getInt("x");
                int y = vecTag.getInt("y");
                int z = vecTag.getInt("z");
                reusableLocations.add(new Vec3i(x, y, z));
            }
        }

        //legacy check
        if(nbt.contains("nbt-v") && nbt.getInt("nbt-v") < 5) {
            LOGGER.info("Converted {} island(s).", LegacyFormatter.formatLegacy(nbt, filePath));
        }
        if(nbt.contains("nbt-v") && nbt.getInt("nbt-v") == 5) {
            LOGGER.info("Converted {} island(s).", LegacyFormatter.formatBeta(filePath));
        }

        final Collection<Island> islands = NBTEncoder.loadFromFolder(filePath, Island.class);
        islands.forEach(island -> islandsByUUID.put(island.getId(), island)); //Store islands in map
        LOGGER.info("Loaded {} island(s).", islands.size());
    }

    /**
     * Get collection of all created islands for the world.
     *
     * @return Collection of islands
     */
    public Collection<Island> getIslands() {
        return islandsByUUID.values();
    }

    /**
     * Generate an island from NBT data file stored within resources of this plugin.
     * Island is template given from Iskall85 Vaulthunters S3 modpack.
     *
     * @param worldServer World you wish island to be generated within
     * @return Spawn location of island
     */
    public Vec3i genIsland(final ServerLevel worldServer) {
        final Vec3i islandLocation = reusableLocations.isEmpty() ? lastLocation : reusableLocations.remove();
        if(islandLocation == lastLocation) lastLocation = nextGridLocation(lastLocation);

        final CompoundTag nbt = ResourceManager.getIslandNBT(worldServer.getServer());

        final ListTag paletteNbt = nbt.getList("palette", 10);
        final ListTag blocksNbt = nbt.getList("blocks", 10);

        final ArrayList<BuildingBlock> blocks = new ArrayList<>();
        final ArrayList<BlockState> palette = new ArrayList<>();
        int bigestX = 0, bigestZ = 0;

        for (int i = 0; i < paletteNbt.size(); i++) palette.add(NbtUtils.readBlockState(paletteNbt.getCompound(i)));
        for (int i = 0; i < blocksNbt.size(); i++) {
            final CompoundTag blockNbt = blocksNbt.getCompound(i);
            final ListTag blockPosNbt = blockNbt.getList("pos", 3);

            if (blockPosNbt.getInt(0) > bigestX) bigestX = blockPosNbt.getInt(0);
            if (blockPosNbt.getInt(2) > bigestZ) bigestZ = blockPosNbt.getInt(2);

            blocks.add(new BuildingBlock(
                    new BlockPos(
                            blockPosNbt.getInt(0),
                            blockPosNbt.getInt(1),
                            blockPosNbt.getInt(2)
                    ),
                    palette.get(blockNbt.getInt("state"))
            ));
        }

        if (blocks.isEmpty()) {
            throw new NBTNotFoundException();
        }

        final int finalBigestX = bigestX;
        final int finalBigestZ = bigestZ;
        final int height = Integer.parseInt(SkyblockAddonConfig.getForKey("island.spawn.height"));
        blocks.stream().filter(block -> !block.getState().isAir()).forEach(block -> block.place(worldServer, islandLocation.offset(-(finalBigestX / 2), height, -(finalBigestZ / 2))));

        final ChunkAccess chunk = worldServer.getChunk(new BlockPos(islandLocation.getX(), height, islandLocation.getZ()));
        final int topHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, islandLocation.getX(), islandLocation.getZ()) + 2;

        final Vec3i rslt = new Vec3i(islandLocation.getX(), topHeight, islandLocation.getZ());
        return rslt;
    }

    /**
     * Get next Vec3i for an island based on snake pattern island generation algorithm.
     *
     * @param location Last island location
     * @return Location for next island
     */
    private Vec3i nextGridLocation(Vec3i location) {
        int x = location.getX();
        int z = location.getZ();
        final int d = SkyblockAddon.ISLAND_SIZE * 2 + SkyblockAddon.ISLAND_BUFFER;

        if (x < z) {
            if (-1 * x < z) return new Vec3i(x + d, 0, z);
            return new Vec3i(x, 0, z + d);
        }

        if (x > z) {
            if (-1 * x >= z) return new Vec3i(x - d, 0, z);
            return new Vec3i(x, 0, z - d);
        }

        if (x <= 0) return new Vec3i(x, 0, z + d);
        return new Vec3i(x, 0, z - d);
    }

    /**
     * Register a new island through object
     * @param island - Island to register
     */
    public void registerIsland(final Island island, final UUID entity) {
        islandsByUUID.put(island.getId(), island);

        //Register island into cache
        CACHE_islandByPlayerUUID.put(entity, Optional.of(island.getId()));
        CACHE_islandByBoundingBox.put(island.getIslandBoundingBox(), Optional.of(island.getId()));
    }

    public List<UUID> getPurgableIslands() {
        return islandsByUUID.entrySet().stream() // Create a stream of map entries
                .filter(entry -> entry.getValue().isAbandoned() && !entry.getValue().getModifiedChunks().isEmpty()) // Filter entries where island is abandoned
                .map(Map.Entry::getKey) // Map to the UUID (key) of the entry
                .collect(Collectors.toList()); // Collect into a List<UUID>
    }

    public void clearIslandCache(Island data) {
        CACHE_islandByBoundingBox.invalidate(data.getIslandBoundingBox());
        Stream.concat(data.getMembers().stream(), Stream.of(data.getOwner()))
            .forEach(uuid -> {
                CACHE_islandByPlayerUUID.invalidate(uuid);
                CACHE_islandByPlayerUUID.put(uuid, Optional.empty());
            });
        islandsByUUID.remove(data.getId());
    }

    public void removeIslandNBT(Island data) {
        final Path worldPath = serverInstance.getWorldPath(LevelResource.ROOT).normalize();
        final Path filePath = worldPath.resolve("islanddata");

        NBTEncoder.removeFileFromFolder(filePath, data);
    }

    public void islandSpaceReusable(Vec3i center) {
        reusableLocations.add(new Vec3i(center.getX(), 0, center.getZ()));
    }
}
