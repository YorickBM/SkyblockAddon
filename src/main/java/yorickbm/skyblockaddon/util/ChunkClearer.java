package yorickbm.skyblockaddon.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.islands.Island;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

public class ChunkClearer {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int CHUNK_SIZE = 16; // Size of each chunk in blocks (16x16)
    private static final int BLOCKS_PER_BATCH = 1024; // Number of blocks to process per async batch (32x32 region)
    private static final int MAX_CONCURRENT_TASKS = 10; // Maximum number of concurrent tasks to avoid overloading the server
    private static final int DELAY_BETWEEN_CHUNKS_MS = 500; // Delay between processing chunks (in ms)

    private static final ForkJoinPool executorService = new ForkJoinPool(3); // Use ForkJoinPool for better parallel task scheduling

    private static final ScheduledExecutorService delayExecutorService = Executors.newSingleThreadScheduledExecutor(); // Delayed executor for throttling

    private final Semaphore semaphore = new Semaphore(MAX_CONCURRENT_TASKS); // Semaphore to limit concurrent tasks
    private final ConcurrentHashMap<ChunkPos, Boolean> chunkEmptyCache = new ConcurrentHashMap<>(); // Cache for chunk load status
    private final ConcurrentHashMap<ChunkPos, LevelChunk> chunkCache = new ConcurrentHashMap<>(); // Cache for chunk loading

    // Method to clear blocks asynchronously in multiple bounding boxes, but only one at a time
    public void clearBlocksInBoundingBoxes(final ServerLevel world, final List<Island> islands, final BiConsumer<Integer, Island> action, final Runnable end) {
        // Sequentially process each bounding box by using an executor with a single task queue
        CompletableFuture<Void> lastFuture = CompletableFuture.completedFuture(null);

        for (int index = 0; index < islands.size(); index++) {
            final Island island = islands.get(index);
            final BoundingBox box = island.getIslandBoundingBox();

            // Create a final variable to capture the current index
            final int currentIndex = index;

            // Chain the tasks in sequence, and add a delay between each chunk processing
            int finalIndex = index;
            lastFuture = lastFuture.thenRunAsync(() -> {
                        // Delay the task submission for chunk processing
                        delayExecutorService.schedule(() -> clearBlocksInBoundingBox(world, box), DELAY_BETWEEN_CHUNKS_MS * finalIndex, TimeUnit.MILLISECONDS);
                    }, executorService)
                    .thenRunAsync(() -> action.accept(currentIndex, island), executorService); // Use currentIndex

        }

        lastFuture.thenRunAsync(end);
    }

    // Main method for clearing a specific bounding box
    private void clearBlocksInBoundingBox(ServerLevel world, BoundingBox box) {
        int minX = box.minX();
        int minY = box.minY();
        int minZ = box.minZ();
        int maxX = box.maxX();
        int maxY = box.maxY();
        int maxZ = box.maxZ();

        // Loop over the bounding box in chunks
        for (int x = minX; x <= maxX; x += CHUNK_SIZE) {
            for (int z = minZ; z <= maxZ; z += CHUNK_SIZE) {
                ChunkPos chunkPos = world.getChunkAt(new BlockPos(x, 0, z)).getPos(); // Get the chunk coordinates

                // Only load the chunk if it contains blocks that need clearing
                if (!isChunkEmpty(world, chunkPos)) {
                    loadChunkAsync(world, chunkPos, () -> clearChunkBlocks(world, chunkPos, minY, maxY));
                }
            }
        }
    }

    // Asynchronously load the chunk and clear blocks in that chunk
    private void loadChunkAsync(ServerLevel level, ChunkPos pos, Runnable task) {
        boolean chunkLoaded = ForgeChunkManager.forceChunk(level, SkyblockAddon.MOD_ID, pos.getMiddleBlockPosition(0), pos.x, pos.z, true, true);

        // Submit the block clearing task once the chunk is loaded
        executorService.submit(() -> {
            if (chunkLoaded) {
                try {
                    // Acquire a permit to limit concurrent task execution
                    semaphore.acquire();

                    // Execute block clearing in the loaded chunk
                    task.run();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.error("Task was interrupted", e);
                } finally {
                    // Release the permit after task completion
                    semaphore.release();
                }

                unloadChunk(level, pos); // Unload chunk once the task is completed
            } else {
                LOGGER.warn("Failed to force load chunk at {} {}", pos.x, pos.z);
            }
        });
    }

    // Safely unload the chunk after clearing the blocks
    private void unloadChunk(ServerLevel level, ChunkPos pos) {
        ForgeChunkManager.forceChunk(level, SkyblockAddon.MOD_ID, pos.getMiddleBlockPosition(0), pos.x, pos.z, false, true);
    }

    // Check if a chunk contains non-air blocks (avoids unnecessary chunk loading)
    private boolean isChunkEmpty(ServerLevel world, ChunkPos chunkPos) {
        return chunkEmptyCache.computeIfAbsent(chunkPos, pos -> {
            LevelChunk chunk = loadChunkIfNotCached(world, pos);
            for (int x = 0; x < CHUNK_SIZE; x++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    for (int y = 0; y < chunk.getHeight(); y++) {
                        BlockPos blockPos = new BlockPos(x, y, z);
                        if (!chunk.getBlockState(blockPos).isAir()) {
                            return false; // Chunk contains non-air blocks
                        }
                    }
                }
            }
            return true; // Chunk is empty
        });
    }

    // Load the chunk if not already cached
    private LevelChunk loadChunkIfNotCached(ServerLevel world, ChunkPos chunkPos) {
        return chunkCache.computeIfAbsent(chunkPos, pos -> world.getChunk(pos.x, pos.z));
    }

    // Clears blocks inside a chunk asynchronously, using parallel processing for small regions within the chunk
    private void clearChunkBlocks(ServerLevel world, ChunkPos chunkPos, int minY, int maxY) {
        LevelChunk chunk = loadChunkIfNotCached(world, chunkPos); // Ensure chunk is loaded

        // Use batches of size BLOCKS_PER_BATCH for better parallelization and efficient chunk processing
        int batchSize = (int) Math.sqrt(BLOCKS_PER_BATCH);

        // Clear blocks in batches
        for (int x = 0; x < CHUNK_SIZE; x += batchSize) {
            for (int z = 0; z < CHUNK_SIZE; z += batchSize) {
                final int blockX = chunkPos.x * CHUNK_SIZE + x;
                final int blockZ = chunkPos.z * CHUNK_SIZE + z;
                executorService.submit(() -> clearBatchBlocks(world, blockX, blockZ, minY, maxY, batchSize));
            }
        }

        // Use CompletableFuture to handle task completion
        CompletableFuture.runAsync(() -> manageChunkMemory(world, chunkPos), executorService);
    }

    // Clears a batch of blocks (32x32 or other batch size) in a chunk asynchronously
    private void clearBatchBlocks(ServerLevel world, int startX, int startZ, int minY, int maxY, int batchSize) {
        for (int x = startX; x < startX + batchSize; x++) {
            for (int z = startZ; z < startZ + batchSize; z++) {
                for (int y = minY; y <= maxY; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!world.getBlockState(pos).isAir()) {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 18); // Clear the block with minimal updates
                    }
                }
            }
        }
    }

    // Manages chunk unloading and freeing up memory (after processing the area)
    private void manageChunkMemory(ServerLevel world, ChunkPos chunkPos) {
        // Asynchronously unload the chunk
        executorService.submit(() -> unloadChunk(world, chunkPos));
    }
}