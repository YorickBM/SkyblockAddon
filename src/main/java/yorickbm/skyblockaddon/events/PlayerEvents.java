package yorickbm.skyblockaddon.events;

import iskallia.vault.entity.entity.DollMiniMeEntity;
import iskallia.vault.entity.entity.SpiritEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.islands.Island;

@Mod.EventBusSubscriber(modid = SkyblockAddon.MOD_ID)
public class PlayerEvents {

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onVoidFall(LivingDamageEvent event) {
        if(event.getSource().equals(DamageSource.OUT_OF_WORLD)) {
            Entity entity = event.getEntity();
            if(entity instanceof ServerPlayer || entity instanceof DollMiniMeEntity || entity instanceof SpiritEntity) {
                if(entity.getLevel().dimension() != Level.OVERWORLD) return; //Ignore non overworld events

                entity.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
                    Island island = cap.getIslandPlayerIsStandingOn(entity);
                    if(island == null) return;

                    event.setCanceled(true); //Cancel damage

                    entity.resetFallDistance();
                    island.teleportTo(entity);
                    entity.resetFallDistance();

                });
            }
        }
    }

}
