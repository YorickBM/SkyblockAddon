package yorickbm.skyblockaddon.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ThreadManager {
    private static final Map<UUID, Thread> activeThreads = new HashMap<>();

    @SuppressWarnings("BusyWait")
    public static UUID startLoopingThread(RunnableWithParams task, int delay) {
        UUID threadId = getNextThreadId();
        Thread thread = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    task.run(threadId); // Execute logic
                    Thread.sleep(delay);  // Sleep for delay
                }
            } catch (InterruptedException e) {
                // Handle interruption if needed
            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
        activeThreads.put(threadId, thread);
        return threadId;
    }

    public static UUID startThread(RunnableWithParams task) {
        UUID threadId = getNextThreadId();
        Thread thread = new Thread(() -> task.run(threadId));
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
        activeThreads.put(threadId, thread);
        return threadId;
    }

    public static void terminateThread(UUID threadId) {
        Thread thread = activeThreads.get(threadId);
        if (thread != null) {
            thread.interrupt();
            activeThreads.remove(threadId);
        }
    }

    public static void terminateAllThreads() {
        for(UUID thread : activeThreads.keySet()) {
            try {
                terminateThread(thread);
            } catch (Exception ex) {
                //Too late to do anything about it.
            }
        }
    }

    private static synchronized UUID getNextThreadId() {
        return UUID.randomUUID();
    }

    public interface RunnableWithParams {
        void run(UUID threadId);
    }
}