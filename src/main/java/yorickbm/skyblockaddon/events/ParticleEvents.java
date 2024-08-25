package yorickbm.skyblockaddon.events;

import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.*;

@Mod.EventBusSubscriber(modid = SkyblockAddon.MOD_ID)
public class ParticleEvents {
    private final Map<UUID, Island> LastIslandOn = new HashMap<>();

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if(!(event.getEntity() instanceof  ServerPlayer player) || event.getEntity() instanceof FakePlayer) return; //Ignore everything except server player
        if(player.getLevel().dimension() != Level.OVERWORLD) return; //Ignore none overworld events

        player.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandPlayerIsStandingOn(player);
            if(island != null) {
                LastIslandOn.put(player.getUUID(), island);
            } else {
                island = LastIslandOn.get(player.getUUID());
            }
            if(island == null) return;

            BoundingBox box = island.getIslandBoundingBox();
            Vec3i P3 = player.getOnPos().offset(0, 1, 0);
            Vec3i P1 = new Vec3i(box.maxX(), P3.getY(), box.maxZ());
            Vec3i P2 = new Vec3i(box.minX(), P3.getY(), box.minZ());

            for(Vec3i point : getLoationsOnEdge(P1, P2, P3)) {
                ServerHelper.showParticleToPlayer(player, point, ParticleTypes.CLOUD, 3);
            }
        });
    }

    private List<Vec3i> getLoationsOnEdge(Vec3i P1, Vec3i P2, Vec3i P3) {
        Vec3i AB = new Vec3i(P3.getX(), P3.getY(), P1.getZ());
        Vec3i AD = new Vec3i(P1.getX(), P3.getY(), P3.getZ());

        Vec3i CB = new Vec3i(P2.getX(), P3.getY(), P3.getZ());
        Vec3i CD = new Vec3i(P3.getX(), P3.getY(), P2.getZ());

        Vec3i closestPoint = minDistance(P3, AB, AD, CB, CD);
        boolean isOnX = closestPoint == AB || closestPoint == CD;
        boolean negativePassed = closestPoint == AB || closestPoint == AD;

        if(distanceBetween(closestPoint, P3) > 70) return new ArrayList<>();

        Vec3i cornerA = Vec3i.ZERO;
        Vec3i cornerB = Vec3i.ZERO;

        if(closestPoint == AB) {
            cornerA = P1;
            cornerB = new Vec3i(P2.getX(), P3.getY(), P1.getZ());
        } else if(closestPoint == CD) {
            cornerA = new Vec3i(P1.getX(), P3.getY(), P2.getZ());
            cornerB = P2;
        } else if(closestPoint == AD) {
            cornerB = new Vec3i(P1.getX(), P3.getY(), P2.getZ());
            cornerA = P1;
        } else if(closestPoint == CB) {
            cornerB = P2;
            cornerA = new Vec3i(P2.getX(), P3.getY(), P1.getZ());
        }

        List<Vec3i> particlePoints = new ArrayList<>();
        getPoints(particlePoints, closestPoint, isOnX, false, cornerA, negativePassed);
        getPoints(particlePoints, closestPoint, isOnX, true, cornerB, negativePassed);

        return particlePoints;
    }

    private void getPoints(List<Vec3i> points, Vec3i closestPoint, boolean isOnX, boolean isNegative, Vec3i corner, boolean negativePassed) {
        boolean passedCorner = false;
        int passedOn = 0;

        for(int i = 0; i < 24; i++) {
            Vec3i offset;
            Vec3i nextPoint;

            if(!passedCorner) {
                int amountOffset = i * (isNegative ? -1 : 1);
                offset = isOnX ? new Vec3i(amountOffset, 0, 0) : new Vec3i(0, 0, amountOffset);
                nextPoint = closestPoint.offset(offset);

                if(nextPoint.closerThan(corner, 0.5)) {
                    passedCorner = true;
                    passedOn = i;
                    isOnX = !isOnX; //Flip it since we go around a corner
                }
            } else {
                int amountOffset = (i-passedOn) * (negativePassed ? -1 : 1);
                offset = isOnX ? new Vec3i(amountOffset, 0, 0) : new Vec3i(0, 0, amountOffset);
                nextPoint = corner.offset(offset);
            }

            points.add(nextPoint);
        }
    }

    private double distanceBetween(Vec3i p1, Vec3i p2) {
        double deltaX = p2.getX() - p1.getX();
        double deltaZ = p2.getZ() - p1.getZ();

        return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }

    private Vec3i minDistance(Vec3i P3, Vec3i... points) {
        if(points == null || points.length == 0) {
            return P3;
        }

        double minDistance = Double.MAX_VALUE;
        Vec3i minPoint = P3;

        for(Vec3i point : points) {
            double distance = distanceBetween(P3, point);
            if(distance < minDistance) {
                minDistance = distance;
                minPoint = point;
            }
        }

        return minPoint;
    }
}
