package yorickbm.skyblockaddon.events;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yorickbm.skyblockaddon.Main;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.islands.Permission;

/**
 * Event Source: https://forge.gemwire.uk/wiki/Events
 */
public class PlayerEvents {
    @SubscribeEvent
    public void onEnderPearl(EntityTeleportEvent.EnderPearl event) {
        IslandData island = Main.CheckOnIsland(event.getPlayer());
        if(island == null) return; //We Shall do Nothing

        if(!island.hasPermission(Permission.EnderPearl, event.getPlayer())) event.setCanceled(true);
        //Has permission so event should not be canceled
    }
    @SubscribeEvent
    public void onChorusFruit(EntityTeleportEvent.ChorusFruit event) {
        if(!(event.getEntity() instanceof Player player)) return;

        IslandData island = Main.CheckOnIsland(player);
        if(island == null) return; //We Shall do Nothing

        if(!island.hasPermission(Permission.ChorusFruit, player)) event.setCanceled(true);
        //Has permission so event should not be canceled
    }

    @SubscribeEvent
    public void onContainerOpenEvent(PlayerContainerEvent.Open event) {
        if(Main.islandUIIds.contains(event.getContainer().containerId)) return; //Its an island GUI so we ignore event

        event.getEntity().sendMessage(new TextComponent("You triggered: onContainerOpenEvent"), event.getEntity().getUUID());
    }

    @SubscribeEvent
    public void onPlayerSleepInBed(PlayerSleepInBedEvent event) {
        event.getPlayer().sendMessage(new TextComponent("You triggered: onPlayerSleepInBed"), event.getPlayer().getUUID());

        IslandData island = Main.CheckOnIsland(event.getPlayer());
        if(island == null) return; //We Shall do Nothing

        if(!island.hasPermission(Permission.UseBed, event.getPlayer())) event.setCanceled(true);
        //Has permission so event should not be canceled
    }

    @SubscribeEvent
    public void onPlayerXP(PlayerXpEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        player.sendMessage(new TextComponent("You triggered: onPlayerXP"), player.getUUID());

        IslandData island = Main.CheckOnIsland(player);
        if(island == null) return; //We Shall do Nothing

        if(!island.hasPermission(Permission.InteractWithXP, player)) event.setCanceled(true);
        //Has permission so event should not be canceled
    }

    @SubscribeEvent
    public void onUseBucket(FillBucketEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        player.sendMessage(new TextComponent("You triggered: onUseBucket"), player.getUUID());
    }

    @SubscribeEvent
    public void onBonemeal(BonemealEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        player.sendMessage(new TextComponent("You triggered: onBonemeal"), player.getUUID());
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        player.sendMessage(new TextComponent("You triggered: onItemPickup"), player.getUUID());
    }
    @SubscribeEvent
    public void onItemDrop(LivingDropsEvent event) { //Not triggered for players
        if(!(event.getEntity() instanceof Player player)) return;
        player.sendMessage(new TextComponent("You triggered: onItemDrop"), player.getUUID());
    }

    @SubscribeEvent
    public void onItemDestroy(PlayerDestroyItemEvent event) { // What the hell is this then?
        if(!(event.getEntity() instanceof Player player)) return;
        player.sendMessage(new TextComponent("You triggered: onItemDestroy"), player.getUUID());
    }

    @SubscribeEvent
    public void onItemUse(LivingEntityUseItemEvent event) { // What the hell is this then?
        event.getEntity().sendMessage(new TextComponent("You triggered: onItemUse"), event.getEntity().getUUID());
    }
}
