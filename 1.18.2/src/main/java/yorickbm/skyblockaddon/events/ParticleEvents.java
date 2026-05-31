package yorickbm.skyblockaddon.events;

import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yorickbm.skyblockaddon.core.islands.Island;
import yorickbm.skyblockaddon.core.islands.IslandManager;
import yorickbm.skyblockaddon.util.ForgeConverter;
import yorickbm.skyblockaddon.util.IslandBorderCalculator;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParticleEvents {
    private final Map<UUID, Island> lastIslandOn = new HashMap<>();

    @SubscribeEvent
    public void onEntityUpdate(final LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntity() instanceof final ServerPlayer player) || event.getEntity() instanceof FakePlayer) return;
        if (player.getLevel().dimension() != Level.OVERWORLD) return;

        Island island = IslandManager.getInstance().getIslandByPos(ForgeConverter.ForgeToInternalVec3i(player.getOnPos()));
        if (island != null) {
            lastIslandOn.put(player.getUUID(), island);
        } else {
            island = lastIslandOn.get(player.getUUID());
        }
        if (island == null) return;

        final BoundingBox box = ForgeConverter.InternalToForgeBoundingBox(island.getIslandBoundingBox());
        final Vec3i playerPos = player.getOnPos().offset(0, 1, 0);
        final Vec3i maxCorner = new Vec3i(box.maxX(), playerPos.getY(), box.maxZ());
        final Vec3i minCorner = new Vec3i(box.minX(), playerPos.getY(), box.minZ());

        for (final Vec3i point : IslandBorderCalculator.getLocationsOnEdge(maxCorner, minCorner, playerPos)) {
            ServerHelper.showParticleToPlayer(player, point, ParticleTypes.CLOUD, 3);
        }
    }
}
