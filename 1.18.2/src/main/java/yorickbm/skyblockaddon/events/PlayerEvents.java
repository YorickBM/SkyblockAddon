package yorickbm.skyblockaddon.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import yorickbm.skyblockaddon.core.configs.VoidProtectionConfig;
import yorickbm.skyblockaddon.core.islands.IslandManager;
import yorickbm.skyblockaddon.islands.ForgeIsland;
import yorickbm.skyblockaddon.util.ForgeConverter;

public class PlayerEvents {

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onVoidFall(final LivingDamageEvent event) {
        if (!event.getSource().equals(DamageSource.OUT_OF_WORLD)) return;

        final Entity entity = event.getEntity();
        final ResourceLocation typeKey = ForgeRegistries.ENTITIES.getKey(entity.getType());
        if (typeKey == null) return;

        if (!VoidProtectionConfig.getInstance().shouldProtect(typeKey.toString())) return;
        if (entity.getLevel().dimension() != Level.OVERWORLD) return;

        final ForgeIsland island = (ForgeIsland) IslandManager.getInstance().getIslandByPos(ForgeConverter.ForgeToInternalVec3i(entity.getOnPos()));
        if (island == null) return;

        event.setCanceled(true);
        entity.resetFallDistance();
        island.teleportTo(entity);
        entity.resetFallDistance();
    }
}
