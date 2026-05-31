package yorickbm.skyblockaddon.util;

import net.minecraft.core.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class IslandBorderCalculator {

    /**
     * Returns the particle positions to render on the border edge nearest to the player.
     *
     * @param P1 - Max corner of the island bounding box at player Y
     * @param P2 - Min corner of the island bounding box at player Y
     * @param P3 - Player position
     */
    public static List<Vec3i> getLocationsOnEdge(final Vec3i P1, final Vec3i P2, final Vec3i P3) {
        final Vec3i AB = new Vec3i(P3.getX(), P3.getY(), P1.getZ());
        final Vec3i AD = new Vec3i(P1.getX(), P3.getY(), P3.getZ());
        final Vec3i CB = new Vec3i(P2.getX(), P3.getY(), P3.getZ());
        final Vec3i CD = new Vec3i(P3.getX(), P3.getY(), P2.getZ());

        final Vec3i closestPoint = minDistance(P3, AB, AD, CB, CD);
        final boolean isOnX = closestPoint == AB || closestPoint == CD;
        final boolean negativePassed = closestPoint == AB || closestPoint == AD;

        if (distanceBetween(closestPoint, P3) > 70) return new ArrayList<>();

        Vec3i cornerA = Vec3i.ZERO;
        Vec3i cornerB = Vec3i.ZERO;

        if (closestPoint == AB) {
            cornerA = P1;
            cornerB = new Vec3i(P2.getX(), P3.getY(), P1.getZ());
        } else if (closestPoint == CD) {
            cornerA = new Vec3i(P1.getX(), P3.getY(), P2.getZ());
            cornerB = P2;
        } else if (closestPoint == AD) {
            cornerB = new Vec3i(P1.getX(), P3.getY(), P2.getZ());
            cornerA = P1;
        } else if (closestPoint == CB) {
            cornerB = P2;
            cornerA = new Vec3i(P2.getX(), P3.getY(), P1.getZ());
        }

        final List<Vec3i> particlePoints = new ArrayList<>();
        getPoints(particlePoints, closestPoint, isOnX, false, cornerA, negativePassed);
        getPoints(particlePoints, closestPoint, isOnX, true, cornerB, negativePassed);
        return particlePoints;
    }

    private static void getPoints(final List<Vec3i> points, final Vec3i closestPoint, boolean isOnX,
                                  final boolean isNegative, final Vec3i corner, final boolean negativePassed) {
        boolean passedCorner = false;
        int passedOn = 0;

        for (int i = 0; i < 24; i++) {
            final Vec3i nextPoint;

            if (!passedCorner) {
                final int amountOffset = i * (isNegative ? -1 : 1);
                final Vec3i offset = isOnX ? new Vec3i(amountOffset, 0, 0) : new Vec3i(0, 0, amountOffset);
                nextPoint = closestPoint.offset(offset);

                if (nextPoint.closerThan(corner, 0.5)) {
                    passedCorner = true;
                    passedOn = i;
                    isOnX = !isOnX;
                }
            } else {
                final int amountOffset = (i - passedOn) * (negativePassed ? -1 : 1);
                final Vec3i offset = isOnX ? new Vec3i(amountOffset, 0, 0) : new Vec3i(0, 0, amountOffset);
                nextPoint = corner.offset(offset);
            }

            points.add(nextPoint);
        }
    }

    private static double distanceBetween(final Vec3i p1, final Vec3i p2) {
        final double dx = p2.getX() - p1.getX();
        final double dz = p2.getZ() - p1.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private static Vec3i minDistance(final Vec3i origin, final Vec3i... points) {
        double minDist = Double.MAX_VALUE;
        Vec3i nearest = origin;
        for (final Vec3i point : points) {
            final double d = distanceBetween(origin, point);
            if (d < minDist) {
                minDist = d;
                nearest = point;
            }
        }
        return nearest;
    }
}
