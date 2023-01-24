package yorickbm.skyblockaddon.events;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.capabilities.IslandGenerator;
import yorickbm.skyblockaddon.capabilities.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.PlayerIsland;
import yorickbm.skyblockaddon.capabilities.PlayerIslandProvider;
import yorickbm.skyblockaddon.commands.*;
import yorickbm.skyblockaddon.Main;
import yorickbm.skyblockaddon.util.LanguageFile;

import java.util.Timer;
import java.util.TimerTask;

@Mod.EventBusSubscriber(modid = Main.MOD_ID)
public class ModEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        new CreateIslandCommand(event.getDispatcher());
        new LeaveIslandCommand(event.getDispatcher());
        new InviteIslandCommand(event.getDispatcher());
        new AcceptIslandCommand(event.getDispatcher());
        new TeleportIslandCommand(event.getDispatcher());
        new IslandBiomeCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
        LOGGER.info("Registered commands for " + Main.MOD_ID);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if(!(event.getObject() instanceof Player)) return;
        if(event.getObject().getCapability(PlayerIslandProvider.PLAYER_ISLAND).isPresent()) return;

        event.addCapability(new ResourceLocation(Main.MOD_ID, "islanddata"), new PlayerIslandProvider());
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesWorld(AttachCapabilitiesEvent<Level> event) {
        if(event.getObject().dimension() != Level.OVERWORLD) return;
        if(event.getObject().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).isPresent()) return;

        event.addCapability(new ResourceLocation(Main.MOD_ID, "properties"), new IslandGeneratorProvider());
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if(!event.isWasDeath()) return;
        event.getOriginal().reviveCaps();

        event.getOriginal().getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(oldStore -> {
            event.getPlayer().getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(newStore -> {
                newStore.copyFrom(oldStore);
            });
        });

        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onDimensionChange (PlayerEvent.PlayerChangedDimensionEvent event) {
        event.getPlayer().reviveCaps();
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        event.getPlayer().reviveCaps();
//        event.getPlayer().getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> {
//            if(i.hasOne())
//                event.getPlayer().teleportTo(i.getLocation().getX(), i.getLocation().getY(), i.getLocation().getZ());
//        });
    }

//    @SubscribeEvent
//    public void onPlayerBreak(BlockEvent.BreakEvent event) {
//        if(event.getPlayer().hasPermissions(3) || event.getPlayer().getLevel().dimension() != Level.OVERWORLD) return;
//
//        event.getPlayer().getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> {
//            if(!i.hasOne() || (new Vec3(i.getLocation().getX(), 0, i.getLocation().getZ())).distanceTo(new Vec3(event.getPos().getX(), 0, event.getPos().getZ())) > IslandGeneratorProvider.SIZE) {
//                //event.getPlayer().sendMessage(new TextComponent(LanguageFile.getForKey("player.break.notpermitted")), event.getPlayer().getUUID());
//                event.setCanceled(true);
//                return;
//            }
//        });
//    }
//    @SubscribeEvent
//    public void onPlayerPlace(BlockEvent.EntityPlaceEvent event) {
//        if(event.getEntity().hasPermissions(3) || event.getEntity().getLevel().dimension() != Level.OVERWORLD) return;
//
//        event.getEntity().getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> {
//            if(!i.hasOne() || (new Vec3(i.getLocation().getX(), 0, i.getLocation().getZ())).distanceTo(new Vec3(event.getPos().getX(), 0, event.getPos().getZ())) > IslandGeneratorProvider.SIZE) {
//                //event.getEntity().sendMessage(new TextComponent(LanguageFile.getForKey("player.place.notpermitted")), event.getEntity().getUUID());
//                event.setCanceled(true);
//                return;
//            }
//        });
//    }
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getPlayer().hasPermissions(3) || event.getPlayer().getLevel().dimension() != Level.OVERWORLD) return;

        event.getPlayer().getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> {
            if(!i.hasOne() || (new Vec3(i.getLocation().getX(), 0, i.getLocation().getZ())).distanceTo(new Vec3(event.getPos().getX(), 0, event.getPos().getZ())) > IslandGeneratorProvider.SIZE) {
                //event.getPlayer().sendMessage(new TextComponent(LanguageFile.getForKey("player.interact.notpermitted")), event.getPlayer().getUUID());
                event.setCanceled(true);
                return;
            }
        });
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerIsland.class);
        event.register(IslandGenerator.class);
    }

}
