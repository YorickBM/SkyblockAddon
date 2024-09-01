package yorickbm.skyblockaddon.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.SkyblockAddonWorldCapability;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.islands.Island;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber(modid = SkyblockAddon.MOD_ID)
public class PermissionEvents {

    @SubscribeEvent
    public void onEnderPearl(EntityTeleportEvent.EnderPearl event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }
    @SubscribeEvent
    public void onChorusFruit(EntityTeleportEvent.ChorusFruit event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }

    @SubscribeEvent
    public void onSleepInBed(PlayerSleepInBedEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }
    @SubscribeEvent
    public void onXp(PlayerXpEvent.PickupXp event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }
    @SubscribeEvent
    public void onPickup(PlayerEvent.ItemPickupEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }
    @SubscribeEvent
    public void onDrop(ItemTossEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }
    @SubscribeEvent
    public void onBucket(FillBucketEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }
    @SubscribeEvent
    public void onBonemeal(BonemealEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }
    @SubscribeEvent
    public void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickEmpty event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }
    @SubscribeEvent
    public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }
    @SubscribeEvent
    public void onLeftClickItem(PlayerInteractEvent.LeftClickEmpty event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }

    @SubscribeEvent
    public void onUse(LivingEntityUseItemEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }

    /**
     * Run requirement check for entity. This check determines if it may override on the event.
     * Or if a permission check should be performed.
     *
     * @param entity - Triggering entity
     * @param standingOn - AtomicReference to get Island entity is standing on.
     * @return - True if entity may override permission check.
     */
    private boolean verifyEntity(Entity entity, AtomicReference<Island> standingOn) {
        if(!(entity instanceof ServerPlayer player) || entity instanceof FakePlayer) return true; //Allowed types
        if(player.getLevel().dimension() != Level.OVERWORLD) return true; //Is not in over-world
        if(player.hasPermissions(3)) return true; //Player is admin;

        Optional<SkyblockAddonWorldCapability> cap = player.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).resolve();
        if(cap.isEmpty()) return true; //Could not find Capability

        Island island = cap.get().getIslandPlayerIsStandingOn(player);
        if(island == null) return true; //Not within protection

        standingOn.set(island); //Set atomic reference
        return island.isOwner(player.getUUID()); //Owners may do anything
    }

}
