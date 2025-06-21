package yorickbm.skyblockaddon.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.configs.SkyblockAddonConfig;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.util.ProgressBar;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class IslandChunkManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int CHUNK_SIZE = 16;
    private static final Set<Task> ACTIVE_TASKS = ConcurrentHashMap.newKeySet();

    public static Set<Task> getActiveTasks() {
        return Collections.unmodifiableSet(ACTIVE_TASKS);
    }

    public static void clean() {
        ACTIVE_TASKS.clear();
        ChunkTaskScheduler.clear();
    }

    public static void createDummies(List<Island> islands, ProgressBar bar, Consumer<Island> onIslandFinished, Runnable onDone, ServerLevel level) {
        for (Island island : islands) {
            LOGGER.debug("Creating task for island: " + island.getId());

            Task task = new Task(island, onIslandFinished, onDone, bar);
            ACTIVE_TASKS.add(task);

            // For the island itself (logical root task)
            task.incrementSubTaskCount();
            bar.incrementTasks();

            for (final ChunkPos chunkPos : island.getModifiedChunks()) {
                if(isChunkEmpty(chunkPos, level)) {
                    //Set progressbar to chunk as handeld
                    task.incrementSubTaskCount();
                    task.onSubTaskCompleted();

                    bar.incrementTasks();
                    bar.finishedOne();
                    continue; //Chunk is empty so we ignore it!
                }

                task.incrementSubTaskCount();  // One per chunk to process later
                task.requestChunk(chunkPos);
                requestChunkLoad(level, chunkPos, task);
            }

            // Once chunks are all processed, root task will complete
            bar.finishedOne();
            task.onSubTaskCompleted(); // Decrement root-level task
        }
    }

    private static boolean isChunkEmpty(ChunkPos pos, ServerLevel level) {
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();
        LevelChunk chunk = level.getChunk(pos.x, pos.z);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    BlockPos blockPos = new BlockPos((pos.x << 4) + x, y, (pos.z << 4) + z);
                    BlockState state = chunk.getBlockState(blockPos);
                    if (!state.isAir()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static void requestChunkLoad(ServerLevel level, ChunkPos pos, Task task) {
        ChunkTaskScheduler.queue(() -> {
            ChunkAccess chunk = level.getChunk(pos.x, pos.z, ChunkStatus.FULL, false); // Try get without load
            task.loadChunk(pos);

            if (chunk != null) {
                // Chunk already loaded — process immediately
                LOGGER.debug("Chunk " + pos + " already loaded, processing immediately.");
                processChunk(chunk, level, task);
            } else {
                // Tell Minecraft to load the chunk, **on main thread**
                LOGGER.debug("Requesting chunk load for " + pos);

                level.getChunk(pos.x, pos.z, ChunkStatus.FULL, true);

                // Now wait for ChunkEvent.Load to fire, don't process here!
                // The event handler will pick it up.
            }
        });
    }

    public static void processChunk(ChunkAccess chunk, ServerLevel level, Task task) {
        new Thread(() -> {
            LOGGER.debug("Running processor for chunk " + chunk.getPos() + ".");
            List<BlockPos> nonAirBlocks = new ArrayList<>();

            int minY = level.getMinBuildHeight();
            int maxY = level.getMaxBuildHeight();

            for (int x = 0; x < CHUNK_SIZE; x++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    for (int y = minY; y < maxY; y++) {
                        BlockPos pos = new BlockPos(
                                x + chunk.getPos().getMinBlockX(),
                                y,
                                z + chunk.getPos().getMinBlockZ()
                        );
                        if (!chunk.getBlockState(pos).isAir()) {
                            nonAirBlocks.add(pos);
                        }
                    }
                }
            }

            if(nonAirBlocks.size() > 0) {
                LOGGER.debug("Added chunk " + chunk.getPos() + " to be processed.");
            }

            // Now schedule block removals in batches of 100
            int batchSize = Integer.parseInt(SkyblockAddonConfig.getForKey("purge.blocks"));
            for (int i = 0; i < nonAirBlocks.size(); i += batchSize) {
                final int start = i;
                final int end = Math.min(i + batchSize, nonAirBlocks.size());

                if (end < nonAirBlocks.size()) task.getBar().incrementTasks();
                ChunkTaskScheduler.queue(() -> {
                    for (int j = start; j < end; j++) {
                        BlockPos pos = nonAirBlocks.get(j);
                        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false); // silent
                    }

                    if (end < nonAirBlocks.size()) task.getBar().finishedOne();

                    // After last batch, send chunk update to players
                    if (end >= nonAirBlocks.size()) {
                        ((ServerChunkCache) level.getChunkSource()).chunkMap.getPlayers(chunk.getPos(), false)
                                .forEach(player -> ((ServerGamePacketListenerImpl) player.connection)
                                        .send(new ClientboundLevelChunkWithLightPacket((LevelChunk) chunk, level.getLightEngine(), null, null, false)));

                        task.onSubTaskCompleted();
                        task.markChunkHandled(chunk.getPos());
                        LOGGER.debug("Finished processing chunk " + chunk.getPos() + " for task " + task.getId());
                    }
                });
            }

            if (nonAirBlocks.isEmpty()) {
                // If there was nothing to process, we still need to mark the task done
                task.onSubTaskCompleted();
                task.markChunkHandled(chunk.getPos());
                LOGGER.debug("Finished processing (empty) chunk " + chunk.getPos() + " for task " + task.getId());
            }

        }, "AsyncChunkScan-" + chunk.getPos().x + "," + chunk.getPos().z).start();
    }

    public static class Task {
        private final UUID id = UUID.randomUUID();
        private final Island island;
        private final AtomicInteger remainingSubTasks = new AtomicInteger(1); // start at 1 for the root
        private final Set<ChunkPos> requestedChunks = ConcurrentHashMap.newKeySet();
        private final Set<ChunkPos> loadingChunks = ConcurrentHashMap.newKeySet();
        private final Consumer<Island> onFinished;
        private final Runnable onAllDone;
        private volatile boolean completed = false;
        private ProgressBar bar;

        private final Set<ChunkPos> handledChunks = ConcurrentHashMap.newKeySet();

        public void markChunkHandled(ChunkPos pos) {
            loadingChunks.remove(pos);
            bar.finishedOne();
        }

        public boolean hasChunkBeenHandeld(ChunkPos pos) {
            return handledChunks.contains(pos); // returns false if already handled
        }

        public ProgressBar getBar() { return this.bar; }

        public Task(Island island, Consumer<Island> onFinished, Runnable onAllDone, ProgressBar bar) {
            this.island = island;
            this.onFinished = onFinished;
            this.onAllDone = onAllDone;
            this.bar = bar;
        }

        public UUID getId() {
            return id;
        }

        public Island getIsland() {
            return island;
        }

        public void requestChunk(ChunkPos pos) {
            requestedChunks.add(pos);
        }

        public void loadChunk(ChunkPos pos) {
            loadingChunks.add(pos);
            bar.incrementTasks();
        }

        public boolean isChunkRequested(ChunkPos pos) {
            return requestedChunks.contains(pos);
        }

        public void incrementSubTaskCount() {
            remainingSubTasks.incrementAndGet();
        }

        public void onSubTaskCompleted() {
            int remaining = remainingSubTasks.decrementAndGet();
            if (remaining == 0 && !completed) {
                completed = true;
                onFinished.accept(island);
                ACTIVE_TASKS.remove(this);
                if (ACTIVE_TASKS.isEmpty() && onAllDone != null) {
                    onAllDone.run();
                }
            }
        }

        public boolean isLoadingChunks() {
            return loadingChunks.size() > 0;
        }
    }
}