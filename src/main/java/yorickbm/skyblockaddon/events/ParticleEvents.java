package yorickbm.skyblockaddon.events;

import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ParticleEvents {
    final HashMap<UUID, IslandData> LastIslandOn = new HashMap<>();
    @SubscribeEvent
    public void onPlayerMove(LivingEvent.LivingUpdateEvent event) {
        if(!(event.getEntity() instanceof  ServerPlayer player) || event.getEntity() instanceof FakePlayer) return; //Ignore everything except server player
        if(player.getLevel().dimension() != Level.OVERWORLD) return; //Ignore none overworld events

        IslandData island = SkyblockAddon.CheckOnIsland(player);
        if(island != null) {
            LastIslandOn.put(player.getUUID(), island);
        } else {
            island = LastIslandOn.get(player.getUUID());
        }
        if(island == null) return;

        Vec3i edgePosition = island.getLocationOnEdge(event.getEntity().blockPosition());
        List<Vec3i> blocks = island.getBoundingBoxHelper().calculatePoints(edgePosition, 30);

        int distanceToEdge = ServerHelper.calculateDistance(new Vec3i(player.getBlockX(), player.getBlockY(), player.getBlockZ()), edgePosition);
        if(distanceToEdge < 30) {
            ServerHelper.registerIslandBorder(player, blocks, edgePosition);
        }
    }
}
