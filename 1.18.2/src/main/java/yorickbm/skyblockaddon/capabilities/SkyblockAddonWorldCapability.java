package yorickbm.skyblockaddon.capabilities;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;
import yorickbm.skyblockaddon.core.islands.IslandManager;
import yorickbm.skyblockaddon.core.util.geometry.Vec3i;
import yorickbm.skyblockaddon.islands.ForgeIsland;
import yorickbm.skyblockaddon.islands.IslandStructurePlacer;
import yorickbm.skyblockaddon.legacy.LegacyFormatter;
import yorickbm.skyblockaddon.util.NBTEncoder;
import yorickbm.skyblockaddon.util.NBTUtil;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class SkyblockAddonWorldCapability {
    private static final Logger LOGGER = LogUtils.getLogger();
    MinecraftServer serverInstance;

    public SkyblockAddonWorldCapability(final MinecraftServer server) {
        serverInstance = server;

        //Init Island Manager
        IslandManager.getInstance().initializeCaches(server.getMaxPlayers());
    }

    /**
     * Save data into NBT.
     */
    public void saveNBTData(final CompoundTag nbt) {
        nbt.put("lastIsland", NBTUtil.Vec3iToNBT(IslandManager.getInstance().getLastLocation()));
        nbt.putInt("nbt-v", 6);

        ListTag listTag = new ListTag();
        for (Vec3i vec : IslandManager.getInstance().getReusableLocations()) {
            CompoundTag vecTag = new CompoundTag();
            vecTag.putInt("x", vec.getX());
            vecTag.putInt("y", vec.getY());
            vecTag.putInt("z", vec.getZ());
            listTag.add(vecTag);
        }
        nbt.put("reusableLocations", listTag);

        saveIslandsToDisk();
    }

    /**
     * Persist every in-memory island to its NBT file. Single source of truth for island-file
     * writes - both the capability serialize hook and {@code WorldEvent.Save} call this so the
     * two save paths can't diverge on which islands get written or how they're serialised.
     * Must run on the server thread (reads {@link IslandManager} singleton state).
     */
    public void saveIslandsToDisk() {
        final Path worldPath = serverInstance.getWorldPath(LevelResource.ROOT).normalize();
        final Path filePath = worldPath.resolve("islanddata");

        NBTEncoder.saveToFile(IslandManager.getInstance().getIslands().stream()
                .map(ForgeIsland::new) // constructor copies data from Island
                .collect(Collectors.toList()), filePath);
    }

    /**
     * Load data from NBT.
     */
    public void loadNBTData(final CompoundTag nbt) {
        Vec3i lastLocation = NBTUtil.NBTToVec3i(nbt.getCompound("lastIsland"));
        List<Vec3i> reusableLocations = new ArrayList<>();

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

        final Collection<ForgeIsland> islands = NBTEncoder.loadFromFolder(filePath, ForgeIsland.class);
        IslandManager.getInstance().initializeData(islands.stream().toList(), reusableLocations, lastLocation);
        LOGGER.info("Loaded {} island(s).", islands.size());
    }

    public net.minecraft.core.Vec3i genIsland(final ServerLevel level) {
        return new IslandStructurePlacer(serverInstance).genIsland(level);
    }

    public void removeIslandNBT(ForgeIsland data) {
        final Path worldPath = serverInstance.getWorldPath(LevelResource.ROOT).normalize();
        final Path filePath = worldPath.resolve("islanddata");

        NBTEncoder.removeFileFromFolder(filePath, data);
    }

    public List<UUID> getPurgableIslands() {
        return IslandManager.getInstance().getEntrySet().entrySet().stream() // Create a stream of map entries
                .filter(entry -> entry.getValue().isAbandoned()
                        && entry.getValue() instanceof ForgeIsland fi
                        && !fi.getModifiedChunks().isEmpty())
                .map(Map.Entry::getKey) // Map to the UUID (key) of the entry
                .collect(Collectors.toList()); // Collect into a List<UUID>
    }
}