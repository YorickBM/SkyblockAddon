package yorickbm.skyblockaddon.events;

import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import yorickbm.skyblockaddon.Main;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.islands.Permission;
import yorickbm.skyblockaddon.util.ServerHelper;

/**
 * Event Source: https://forge.gemwire.uk/wiki/Events
 *
 * Right CLick -> Use/Place Block
 * Left Click -> Attack/Destroy Block
 */
public class PlayerEvents {
    @SubscribeEvent
    public void onEnderPearl(EntityTeleportEvent.EnderPearl event) {
        IslandData island = Main.CheckOnIsland(event.getPlayer());
        if(island == null) return; //We Shall do Nothing

        if(!island.hasPermission(Permission.EnderPearl, event.getPlayer())) {
            event.getPlayer().displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.DARK_RED), true);
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
            player.displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }

    @SubscribeEvent
    public void onPlayerSleepInBed(PlayerSleepInBedEvent event) {
        IslandData island = Main.CheckOnIsland(event.getPlayer());
        if(island == null) return; //We Shall do Nothing

        if(!island.hasPermission(Permission.UseBed, event.getPlayer())) {
            event.getPlayer().displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.DARK_RED), true);
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
            player.displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.DARK_RED), true);
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
            player.displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.DARK_RED), true);
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
            player.displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.DARK_RED), true);
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
            player.displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.DARK_RED), true);
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
            player.displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }

    @SubscribeEvent
    public void onBlockInteractRBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getPlayer();
        Block block = player.getLevel().getBlockState(event.getPos()).getBlock();
        ItemStack handItem = player.getMainHandItem();

        event.setCanceled(HandleBlockClick(player, block.asItem(), handItem.getItem()));
    }
    @SubscribeEvent
    public void onBlockInteractREmpty(PlayerInteractEvent.RightClickEmpty event) {
        Player player = event.getPlayer();
        Block block = player.getLevel().getBlockState(event.getPos()).getBlock();

        event.setCanceled(HandleBlockClick(player, block.asItem(), Items.AIR));
    }

    private boolean HandleBlockClick(Player player, Item itemClickedOn, Item itemClickedWith) {
        if(Main.Allowed_Clickable_Items.contains(itemClickedWith) && itemClickedOn == Items.AIR) return false; //Clicking on item is allowed.
        if(Main.Allowed_Clickable_Blocks.contains(itemClickedOn)) return false; //Clicking on item is allowed.

        IslandData island = Main.CheckOnIsland(player);
        if(island == null) return false; //We Shall do Nothing

        if(!island.hasPermission(Permission.OpenBlocks, player)) {
            player.displayClientMessage(ServerHelper.formattedText("You cannot do this here.", ChatFormatting.DARK_RED), true);
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public void onContainerClose(PlayerContainerEvent.Close event) {
        if(!Main.islandUIIds.contains(event.getContainer().containerId)) return; //Its not an island GUI so we ignore event

        //Remove all items containing skyblockaddon tag
        event.getPlayer().inventoryMenu.slots.forEach(slot -> {
            if(slot.getItem().getTagElement(Main.MOD_ID) != null) event.getPlayer().inventoryMenu.setItem(slot.index, 0, ItemStack.EMPTY);
        });
    }


}
