package yorickbm.skyblockaddon.events;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.configs.SkyblockAddonConfig;
import yorickbm.skyblockaddon.configs.SkyblockAddonLanguageConfig;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.islands.permissions.Permission;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Event Source: https://forge.gemwire.uk/wiki/Events
 */
public class BlockEvents {

    @SubscribeEvent
    public void onBlockInteractRBlock(PlayerInteractEvent.RightClickBlock event) {
        if(!(event.getEntity() instanceof ServerPlayer player) || event.getEntity() instanceof FakePlayer)
            return; //Allow fake players

        if(player.hasPermissions(3))
            return; //OP Players are ignored

        if(player.isSecondaryUseActive() && !event.getItemStack().isEmpty())
            return; //Secondary use is allowed

        Block block = player.getLevel().getBlockState(event.getPos()).getBlock(); //Get block
        Item handItem = event.getItemStack().getItem();

        if(handItem == Items.ITEM_FRAME || handItem == Items.ARMOR_STAND)
            entityPlaceEvent(event, player);

        if(event.getWorld().dimension() != Level.NETHER)
            return; //Non-nether is handled differently

        if(block.asItem() == Items.OBSIDIAN && (handItem == Items.FLINT_AND_STEEL || handItem == Items.FIRE_CHARGE))
            netherPortalIgnition(event, player);
    }

    private void entityPlaceEvent(PlayerInteractEvent.RightClickBlock event, ServerPlayer player) {
        if(player.getLevel().dimension() != Level.OVERWORLD)
            return; //Ignore non overworld events

        IslandData island = SkyblockAddon.CheckOnIsland(player);
        if(island.isOwner(player.getUUID()) || island.getPermission(Permissions.PlaceBlocks, player.getUUID()).isAllowed())
            return; //Block placement is allowed

        event.getPlayer().displayClientMessage(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("toolbar.overlay.nothere"), ChatFormatting.DARK_RED), true);
        event.setCanceled(true);
    }

    private void netherPortalIgnition(PlayerInteractEvent.RightClickBlock event, ServerPlayer player) {
        int x = event.getPos().getX() * 8;
        int y = event.getPos().getY();
        int z = event.getPos().getZ() * 8;
        BlockPos location = new BlockPos(x, y, z);

        IslandData island = SkyblockAddon.GetIslandByBlockPos(location, player);
        if(island == null) { //Not on an island, so we do not allow action as we can not check permission level
            player.displayClientMessage(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("toolbar.overlay.nothere"), ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
            return;
        }

        if(island.hasMember(player.getUUID()) || island.isOwner(player.getUUID()))
            return;

        event.getPlayer().displayClientMessage(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("toolbar.overlay.nothere"), ChatFormatting.DARK_RED), true);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if(!(event.getPlayer() instanceof ServerPlayer player) || event.getPlayer() instanceof FakePlayer) return; //Allow fake players
        if(player.getLevel().dimension() != Level.OVERWORLD || player.hasPermissions(3)) return; //Non overworld events we ignore

        IslandData island = SkyblockAddon.CheckOnIsland(player);
        if(island == null) { //Not on an island, so we do not allow action as we can not check permission level
            player.displayClientMessage(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("toolbar.overlay.nothere"), ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
            return;
        }

        if(!island.isOwner(player.getUUID()) && !island.getPermission(Permissions.DestroyBlocks, player.getUUID()).isAllowed()) {
            event.getPlayer().displayClientMessage(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("toolbar.overlay.nothere"), ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player) || event.getEntity() instanceof FakePlayer) return; //Allow fake players
        if(player.getLevel().dimension() != Level.OVERWORLD || player.hasPermissions(3)) return; //Non overworld events we ignore

        IslandData island = SkyblockAddon.CheckOnIsland(player);
        if(island == null) { //Not on an island, so we do not allow action as we can not check permission level
            player.displayClientMessage(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("toolbar.overlay.nothere"), ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
            return;
        }

        Permission permission = island.getPermission(Permissions.PlaceBlocks, player.getUUID());
        if(!island.isOwner(player.getUUID()) && !permission.isAllowed()) {
            player.displayClientMessage(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("toolbar.overlay.nothere"), ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }
    @SubscribeEvent
    public void onTrampleEvent(BlockEvent.FarmlandTrampleEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player) || event.getEntity() instanceof FakePlayer) return; //Allow fake players
        if(player.getLevel().dimension() != Level.OVERWORLD || player.hasPermissions(3)) return; //Non overworld events we ignore

        IslandData island = SkyblockAddon.CheckOnIsland(player);
        if(island == null) { //Not on an island, so we do not allow action as we can not check permission level
            player.displayClientMessage(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("toolbar.overlay.nothere"), ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
            return;
        }

        if(!island.isOwner(player.getUUID()) && !island.getPermission(Permissions.TrampleFarmland, player.getUUID()).isAllowed()) {
            player.displayClientMessage(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("toolbar.overlay.nothere"), ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }
}
