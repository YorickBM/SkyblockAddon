package yorickbm.skyblockaddon.events;

import net.minecraft.core.Vec3i;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.Main;
import yorickbm.skyblockaddon.capabilities.IslandGenerator;
import yorickbm.skyblockaddon.capabilities.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.PlayerIsland;
import yorickbm.skyblockaddon.capabilities.PlayerIslandProvider;
import yorickbm.skyblockaddon.commands.*;

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
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        event.getPlayer().getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> {
            if(i.hasLegacyData()) {
                event.getPlayer().getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(w -> w.registerIslandFromLegacy(i, event.getPlayer()));
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if(!event.isWasDeath()) return;
        event.getOriginal().reviveCaps();

        event.getOriginal().getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(oldStore -> event.getPlayer().getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(newStore -> newStore.copyFrom(oldStore)));

        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onDimensionChange (PlayerEvent.PlayerChangedDimensionEvent event) {
        event.getPlayer().reviveCaps();
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        event.getPlayer().reviveCaps();
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(
            event instanceof PlayerInteractEvent.LeftClickEmpty
            || event instanceof PlayerInteractEvent.RightClickEmpty
            || event.getPlayer().hasPermissions(3)
            || event.getPlayer().getLevel().dimension() != Level.OVERWORLD
        ) return;

        event.getPlayer().getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(i -> {
            if(!i.hasOne()) {
                event.setCanceled(true);
                return;
            }

            event.getPlayer().getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(g -> {
                Vec3i location = g.getIslandById(i.getIslandId()).getSpawn();
                if((new Vec3(location.getX(), 0, location.getZ())).distanceTo(new Vec3(event.getPos().getX(), 0, event.getPos().getZ())) > IslandGeneratorProvider.SIZE) {
                    event.setCanceled(true);
                }
            });
        });
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerIsland.class);
        event.register(IslandGenerator.class);
    }

}
