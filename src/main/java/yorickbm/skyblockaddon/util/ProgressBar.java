package yorickbm.skyblockaddon.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProgressBar {
    private final ServerPlayer player;
    private final String prefix;
    private final int totalSize = 16;
    private int completedTasks = 0;
    private ScheduledExecutorService scheduler;
    private boolean isCompleted = false;
    private int totalTasks;

    // Constructor accepts total number of tasks to calculate progress
    public ProgressBar(ServerPlayer player, String prefix, int totalTasks) {
        this.player = player;
        this.prefix = prefix;
        this.totalTasks = totalTasks;  // This will be the number of tasks to process (e.g., bounding boxes)
        this.scheduler = Executors.newSingleThreadScheduledExecutor(); // Executor to schedule regular updates
    }

    // Start the progress bar, called once at the beginning
    public void start() {
        updateProgress(0);  // Start with 0% progress

        // Schedule periodic updates every second (1000ms)
        scheduler.scheduleAtFixedRate(() -> {
            if (!isCompleted) {
                updateProgress(completedTasks); // Send the current progress every second
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    // Update the progress bar asynchronously based on the index
    public void update(int index) {
        // Update the completed tasks count
        completedTasks = index + 1; // Since index starts at 0, we add 1 for progress calculation
    }

    // Kill the progress bar message when done
    public void kill() {
        // Stop the periodic updates and clear the progress message
        isCompleted = true;
        scheduler.shutdownNow(); // Stop the scheduler
    }

    private String formatNumber(int number) {
        if (number < 1000) {
            return String.valueOf(number); // No formatting for numbers less than 1000
        }

        // Suffixes for K (thousands), M (millions), B (billions)
        String[] suffixes = {"", "K", "M", "B"};
        int magnitude = (int) Math.floor(Math.log10(number) / 3); // Get the magnitude of the number (thousands, millions, etc.)
        double scaledNumber = number / Math.pow(1000, magnitude); // Scale the number down by 1000^magnitude

        // Format the number with one decimal place if necessary
        return String.format("%.1f%s", scaledNumber, suffixes[magnitude]);
    }

    // Helper method to construct the progress bar and update the message
    private void updateProgress(int completed) {
        // Create the base progress bar component
        MutableComponent progressBar = new TextComponent(formatNumber(this.completedTasks) + "/" + formatNumber(this.totalTasks) + " [").withStyle(ChatFormatting.DARK_GRAY);

        // Calculate how many blocks should be filled based on the percentage of completed tasks
        double progressPercentage = (double) completed / totalTasks;  // Calculate the percentage of tasks completed
        int filledBlocks = (int) (progressPercentage * totalSize);  // Scale that percentage to 18 blocks

        // Construct the progress bar with the filled blocks
        for (int i = 0; i < totalSize; i++) {
            if (i < filledBlocks) {
                // Add green block for completed tasks
                progressBar.append(new TextComponent("\u2588").withStyle(ChatFormatting.GREEN));
            } else {
                // Add red block for remaining tasks
                progressBar.append(new TextComponent("\u2588").withStyle(ChatFormatting.RED));
            }
        }

        // Add closing brackets with gray color
        progressBar.append(new TextComponent("]").withStyle(ChatFormatting.DARK_GRAY));

        player.displayClientMessage(progressBar, true);
    }

    public void finishedOne() {
        this.completedTasks += 1;
    }

    public void incrementTasks() {
        this.totalTasks += 1;
    }
}