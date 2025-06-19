package yorickbm.skyblockaddon.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.islands.Island;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class IslandChunkManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int CHUNK_SIZE = 16; // Size of each chunk in blocks (16x16)
    private static final int BLOCKS_PER_BATCH = 1024; // Number of blocks to process per async batch (32x32 region)

    private final ExecutorService executor;
    private final Consumer<Island> onIslandFinished;
    private final Runnable onDone;

    private final Set<Task> activeTasks = ConcurrentHashMap.newKeySet();

    public IslandChunkManager(int threadCount, Consumer<Island> onIslandFinished, Runnable onDone) {
        this.executor = LowPriorityExecutor.createLowPriorityExecutor(threadCount);
        this.onIslandFinished = onIslandFinished;
        this.onDone = onDone;
    }

    private final ConcurrentHashMap<ChunkPos, LevelChunk> chunkCache = new ConcurrentHashMap<>(); // Cache for chunk loading
    private LevelChunk loadChunkIfNotCached(ServerLevel world, ChunkPos chunkPos) {
        return chunkCache.computeIfAbsent(chunkPos, pos -> world.getChunk(pos.x, pos.z));
    }

    // The createDummies function creates Tasks and delegates Subtask creation to the Task itself
    public void createDummies(List<Island> islands, ServerLevel level, ServerPlayer actor, ProgressBar bar) {
        Random random = new Random();
        FakePlayer fplayer = FakePlayerFactory.getMinecraft(level);

        for (Island island : islands) {
            createTask(island, (t, i) -> {
                //TODO: Get all the chunks
                BoundingBox box = i.getIslandBoundingBox();
                for (int x = box.minX(); x <= box.maxX(); x += CHUNK_SIZE) {
                    for (int z = box.minZ(); z <= box.maxZ(); z += CHUNK_SIZE) {

                        t.incrementSubTaskCount(); // Increment Subtask count before submitting
                        bar.incrementTasks();

                        final int finalX = x;
                        final int finalZ = z;
                        submitTask(t, (p) -> {
                            // Only load the chunk if it contains blocks that need clearing
                            final LevelChunk chunk = loadChunkIfNotCached(level, new ChunkPos(finalX, finalZ));
                            t.addChunk(chunk);

                            bar.finishedOne();
                        }, 800);

                    }
                }

                submitTask(t, (u) -> {
                    //TODO: Create SubTask for each non empty chunk
                    LOGGER.info("Generating Chunk Handlers.... ("+u.getId()+")");
                    for(LevelChunk chunk : t.getChunks()) {
                        //TODO: Create SubTask Removing Blocks (Non Air)
                        t.incrementSubTaskCount(); // Increment Subtask count before submitting
                        bar.incrementTasks();

                        BlockPos cntr = chunk.getPos().getMiddleBlockPosition(0);

                        // Submit each Subtask using submitTask to delegate the logic
                        submitTask(t, (p) -> {
                            LOGGER.info("Running subtask for island "+i.getName()+" ("+t.getId()+")");
                            try {
                                actor.teleportTo(level, cntr.getX(), cntr.getY(), cntr.getZ(), 0, 0);

                                for (int x = 0; x < CHUNK_SIZE; x++) {
                                    for (int z = 0; z < CHUNK_SIZE; z++) {
                                        for (int y = level.getMinBuildHeight(); y < level.getMaxBuildHeight(); y++) {
                                            BlockPos blockPos = new BlockPos(x, y, z);
                                            if (!chunk.getBlockState(blockPos).isAir()) {
                                                //TODO: Set block to air
                                            }
                                        }
                                    }
                                }

                                int seconds = 1 + random.nextInt(3); // Random subtask delay (1-5 seconds)
                                System.out.println("SubTask for " + p.getIsland() + " sleeping for " + seconds + "s");
                                Thread.sleep(seconds * 1000L);

                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            bar.finishedOne();
                        }, 500);
                    }
                }, 300);
            });

            // Task will manage its own subtasks internally
        }
    }

    public Task createTask(Island island, BiConsumer<Task, Island> taskLogic) {
        Task task = new Task(island, taskLogic);
        activeTasks.add(task);
        return task;
    }

    public void submitTask(Task task, Consumer<Task> logic, int yieldTime) {
        SubTask subTask = new SubTask(task, logic, yieldTime);
        executor.submit(subTask);
    }

    private void onTaskCompleted(Task task) {
        activeTasks.remove(task);
        if (activeTasks.isEmpty()) {
            shutdown();
        }
    }

    private void shutdown() {
        executor.shutdown();
        if (onDone != null) {
            onDone.run();
        }
    }

    // ========== INNER CLASSES ==========

    public class Task {
        private final UUID id = UUID.randomUUID();
        private final Island island;
        private final AtomicInteger remainingSubTasks = new AtomicInteger(1); // Starts with 1 for task logic
        private final Object lock = new Object();
        private boolean completed = false;

        public Task(Island island, BiConsumer<Task, Island> taskLogic) {
            this.island = island;

            // Execute main task logic asynchronously
            executor.submit(() -> {
                try {
                    taskLogic.accept(this, island); // Execute task logic and pass the task to itself
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Thread.yield();
                } finally {
                    onSubTaskCompleted(); // After task logic is completed, count it as a subtask
                }
            });
        }

        public UUID getId() {
            return id;
        }

        public Island getIsland() {
            return island;
        }

        public void incrementSubTaskCount() {
            remainingSubTasks.incrementAndGet();
        }

        private void onSubTaskCompleted() {
            if (remainingSubTasks.decrementAndGet() == 0) {
                synchronized (lock) {
                    if (!completed) {
                        completed = true;
                        onIslandFinished.accept(island); // Notify when all tasks are completed
                        onTaskCompleted(this); // Notify the manager that this task is complete
                    }
                }
            }
        }

        //Overhead logic
        private List<LevelChunk> chunks = new ArrayList<>();
        public void addChunk(LevelChunk chunk) {
            this.chunks.add(chunk);
        }
        public void removeChunk(LevelChunk chunk) {
            this.chunks.remove(chunk);
        }
        public List<LevelChunk> getChunks() { return this.chunks; }

    }

    private class SubTask implements Runnable {
        private final Task parentTask;
        private final Consumer<Task> logic;
        private final int yieldTime;

        public SubTask(Task parentTask, Consumer<Task> logic, int yieldTime) {
            this.parentTask = parentTask;
            this.logic = logic;
            this.parentTask.incrementSubTaskCount();
            this.yieldTime = yieldTime;
        }

        @Override
        public void run() {
            try {
                logic.accept(parentTask); // Run the task's specific logic
                try {
                    Thread.sleep(this.yieldTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Thread.yield();
            } finally {
                parentTask.onSubTaskCompleted(); // Mark the subtask as complete
            }
        }
    }

    private class LowPriorityExecutor {

        public static ExecutorService createLowPriorityExecutor(int threadCount) {
            ThreadFactory lowPriorityThreadFactory = new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setPriority(Thread.MIN_PRIORITY);  // Set to lowest priority
                    return thread;
                }
            };

            return new ThreadPoolExecutor(
                    threadCount,
                    threadCount,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(),
                    lowPriorityThreadFactory
            );
        }
    }
}