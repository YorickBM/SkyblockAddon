package yorickbm.skyblockaddon.events;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.ItemFrameItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.islands.permissions.Permission;
import yorickbm.skyblockaddon.util.LanguageFile;
import yorickbm.skyblockaddon.util.ServerHelper;

/**
 * Event Source: https://forge.gemwire.uk/wiki/Events
 */
public class BlockEvents {

//    @SubscribeEvent
//    public void onPortalIgnition(BlockEvent.PortalSpawnEvent event) {
//        ServerLevel level = (ServerLevel) event.getWorld();
//        if(level.dimension() == Level.OVERWORLD) return;
//        ServerLevel overworld = event.getWorld().getServer().overworld();
//
//        int x = event.getPos().getX() * 8;
//        int y = event.getPos().getY();
//        int z = event.getPos().getZ() * 8;
//
//        BlockPos overWorldPos = new BlockPos(x, y, z);
//        Player player = level.getNearestPlayer(TargetingConditions.DEFAULT, event.getPos().getX(), y, event.getPos().getZ());
//        IslandData island = SkyblockAddon.PlayerPartOfIslandByPos(overWorldPos, overworld);
//
//        System.out.println(level.dimension());
//        System.out.println(x + ";" + y + ";" + z);
//        assert player != null;
//        System.out.println(player.getDisplayName());
//        System.out.println(island != null);
//        if(island != null) System.out.println(island.getGroupForPlayer(player.getUUID()).getName());
//
//        if(island == null) return; //No island found
//
//        if(!island.hasMember(player.getUUID()) && !island.isOwner(player.getUUID())) {
//            player.displayClientMessage(ServerHelper.formattedText(LanguageFile.getForKey("toolbar.overlay.nothere"), ChatFormatting.DARK_RED), true);
//            event.setCanceled(true);
//        }
//    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if(!(event.getPlayer() instanceof ServerPlayer player) || event.getPlayer() instanceof FakePlayer) return; //Allow fake players
        if(player.getLevel().dimension() != Level.OVERWORLD || player.hasPermissions(3)) return; //Non overworld events we ignore

        IslandData island = SkyblockAddon.CheckOnIsland(player);
        if(island == null) return; //We Shall do Nothing

        if(!island.isOwner(player.getUUID()) && !island.getPermission(Permissions.DestroyBlocks, player.getUUID()).isAllowed()) {
            event.getPlayer().displayClientMessage(ServerHelper.formattedText(LanguageFile.getForKey("toolbar.overlay.nothere"), ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player) || event.getEntity() instanceof FakePlayer) return; //Allow fake players
        if(player.getLevel().dimension() != Level.OVERWORLD || player.hasPermissions(3)) return; //Non overworld events we ignore

        IslandData island = SkyblockAddon.CheckOnIsland(player);
        if(island == null) {
            return; //We Shall do Nothing
        }

        Permission permission = island.getPermission(Permissions.PlaceBlocks, player.getUUID());
        if(!island.isOwner(player.getUUID()) && !permission.isAllowed()) {
            player.displayClientMessage(ServerHelper.formattedText(LanguageFile.getForKey("toolbar.overlay.nothere"), ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }
    @SubscribeEvent
    public void onTrampleEvent(BlockEvent.FarmlandTrampleEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player) || event.getEntity() instanceof FakePlayer) return; //Allow fake players
        if(player.getLevel().dimension() != Level.OVERWORLD || player.hasPermissions(3)) return; //Non overworld events we ignore

        IslandData island = SkyblockAddon.CheckOnIsland(player);
        if(island == null) return; //We Shall do Nothing

        if(!island.isOwner(player.getUUID()) && !island.getPermission(Permissions.TrampleFarmland, player.getUUID()).isAllowed()) {
            player.displayClientMessage(ServerHelper.formattedText(LanguageFile.getForKey("toolbar.overlay.nothere"), ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
        }
        //Has permission so event should not be canceled
    }
}
