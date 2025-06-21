package yorickbm.skyblockaddon.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.islands.Island;

public class ChunkEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void onServerTick(final TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ChunkTaskScheduler.forwardTicks();
        }
    }

    @SubscribeEvent
    public void onChunkLoad(final ChunkEvent.Load event) {
        if (!(event.getWorld() instanceof final ServerLevel serverLevel)) return;

        final ChunkAccess chunk = (ChunkAccess) event.getChunk();
        final ChunkPos pos = chunk.getPos();

        LOGGER.debug("ChunkEvent.Load Chunk {}", pos);
        serverLevel.getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            final Island island = cap.getIslandByPos(chunk.getPos().getMiddleBlockPosition(155));
            if(island == null) return;

            if(island.storeChunk(chunk)) LOGGER.debug("Chunk {} added to island {}", pos, island.getId());
        });

        for (final IslandChunkManager.Task task : IslandChunkManager.getActiveTasks()) {
            if (task.isChunkRequested(pos) && task.hasChunkBeenHandeld(pos)) {
                LOGGER.debug("Queued chunk {} from ChunkEvent.Load for task {}", pos, task.getId());
                IslandChunkManager.processChunk(chunk, serverLevel, task);
                break;
            }
        }
    }
}
