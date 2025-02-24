package yorickbm.skyblockaddon.util;

import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ServerHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final HashMap<UUID, UUID> spawnerTracker = new HashMap<>();
    private static final HashMap<UUID, UUID> terminatorTracker = new HashMap<>();

    public static void playSongToPlayer(final ServerPlayer player, final SoundEvent event, final float vol, final float pitch) {
        ServerHelper.SendPacket(player, new ClientboundSoundPacket(event, SoundSource.PLAYERS, player.position().x, player.position().y, player.position().z, vol, pitch));
    }

    public static void SendPacket(final ServerPlayer player, final Packet<?> packet) {
        player.connection.send(packet);
    }

    public static void showParticleToPlayer(final ServerPlayer player, final Vec3i location, final ParticleOptions particle, final int count) {
        ServerHelper.SendPacket(player, new ClientboundLevelParticlesPacket(particle, false, location.getX() + 0.5f, location.getY() + 0.5f, location.getZ() + 0.5f, 0.1f, 0f, 0.1f, 0f, count));
    }

    /**
     * Get item from ForgeRegistries.
     * If not found returns @param basic
     *
     * @return - Minecraft Registry Item
     */
    public static Item getItem(final String item, final Item basic) {
        try {
            final Item mcItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(item));
            return mcItem != null ? mcItem : basic;
        } catch (final Exception ex) {
            LOGGER.error("Failure to find item '{}';", item);
            LOGGER.error(ex);
            return basic;
        }
    }

    public static void registerIslandBorder(final ServerPlayer player, final List<Vec3i> points, final Vec3i location) {
        //Cleanup old threads
        if (spawnerTracker.containsKey(player.getUUID())) {
            final UUID oldSpawner = spawnerTracker.get(player.getUUID());
            final UUID oldTerminator = terminatorTracker.get(oldSpawner);

            ThreadManager.terminateThread(oldSpawner);
            ThreadManager.terminateThread(oldTerminator);
        }

        //Setup threads for spawner and tracker
        final UUID particleSpawner = ThreadManager.startLoopingThread((id) -> {
            ServerHelper.showParticleToPlayer(player, location, ParticleTypes.CLOUD, 3);
            for (final Vec3i pos : points) {
                ServerHelper.showParticleToPlayer(player, pos, ParticleTypes.CLOUD, 3);
            }
        }, 500);
        final UUID terminator = ThreadManager.startThread((id) -> {
            try {
                Thread.sleep(1000 * 60 * 5);

                //Terminate spawner thread
                ThreadManager.terminateThread(particleSpawner);

                //Cleanup trackers
                spawnerTracker.remove(player.getUUID());
                terminatorTracker.remove(id);
            } catch (final InterruptedException e) {
                //Nothing here
            }
        });

        //Register threads to trackers
        spawnerTracker.put(player.getUUID(), particleSpawner);
        terminatorTracker.put(particleSpawner, terminator);
    }
}