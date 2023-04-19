package yorickbm.skyblockaddon.events;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yorickbm.skyblockaddon.Main;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.islands.Permission;
import yorickbm.skyblockaddon.util.ServerHelper;

/**
 * Event Source: https://forge.gemwire.uk/wiki/Events
 */
public class PlayerEvents {
    @SubscribeEvent
    public void onEnderPearl(EntityTeleportEvent.EnderPearl event) {
        IslandData island = Main.CheckOnIsland(event.getPlayer());
        if(island == null) return; //We Shall do Nothing

        if(!island.hasPermission(Permission.EnderPearl, event.getPlayer())) {
            event.getPlayer().displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }
    @SubscribeEvent
    public void onChorusFruit(EntityTeleportEvent.ChorusFruit event) {
        if(!(event.getEntity() instanceof Player player)) return;
        IslandData island = Main.CheckOnIsland(player);
        if(island == null) return; //We Shall do Nothing

        if(!island.hasPermission(Permission.ChorusFruit, player)) {
            player.displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }

    @SubscribeEvent
    public void onContainerOpenEvent(PlayerContainerEvent.Open event) {
        if(Main.islandUIIds.contains(event.getContainer().containerId)) return; //Its an island GUI so we ignore event

        if(!(event.getEntity() instanceof Player player)) return;
        IslandData island = Main.CheckOnIsland(player);
        if(island == null) return; //We Shall do Nothing

        if(!island.hasPermission(Permission.OpenBlocks, player)) {
            player.displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }

    @SubscribeEvent
    public void onPlayerSleepInBed(PlayerSleepInBedEvent event) {
        IslandData island = Main.CheckOnIsland(event.getPlayer());
        if(island == null) return; //We Shall do Nothing

        if(!island.hasPermission(Permission.UseBed, event.getPlayer())) {
            event.getPlayer().displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }

    @SubscribeEvent
    public void onPlayerXP(PlayerXpEvent.PickupXp event) {
        if(!(event.getEntity() instanceof Player player)) return;

        IslandData island = Main.CheckOnIsland(player);
        if(island == null) return; //We Shall do Nothing

        if(!island.hasPermission(Permission.InteractWithXP, player)) {
            player.displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }

    @SubscribeEvent
    public void onUseBucket(FillBucketEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        IslandData island = Main.CheckOnIsland(player);
        if(island == null) return; //We Shall do Nothing

        if(!island.hasPermission(Permission.UseBucket, player)) {
            player.displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }

    @SubscribeEvent
    public void onBonemeal(BonemealEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        IslandData island = Main.CheckOnIsland(player);
        if(island == null) return; //We Shall do Nothing

        if(!island.hasPermission(Permission.UseBonemeal, player)) {
            player.displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        IslandData island = Main.CheckOnIsland(player);
        if(island == null) return; //We Shall do Nothing

        if(!island.hasPermission(Permission.InteractWithGroundItems, player)) {
            player.displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }
    @SubscribeEvent
    public void onItemDrop(ItemTossEvent event) {
        if(!(event.getEntity() instanceof Player player)) return;
        IslandData island = Main.CheckOnIsland(player);
        if(island == null) return; //We Shall do Nothing

        if(!island.hasPermission(Permission.InteractWithGroundItems, player)) {
            player.displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
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
    public void onItemUse(LivingEntityUseItemEvent event) { // This is eating food, should always be allowed (Also maybe block destroyer?)
        event.getEntity().sendMessage(new TextComponent("You triggered: onItemUse"), event.getEntity().getUUID());
    }
}
