package yorickbm.skyblockaddon.events;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
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
