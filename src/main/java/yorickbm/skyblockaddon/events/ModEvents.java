package yorickbm.skyblockaddon.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.command.ConfigCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.SkyblockAddonWorldCapability;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.commands.*;
import yorickbm.skyblockaddon.commands.op.*;
import yorickbm.skyblockaddon.util.NBT.NBTEncoder;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.nio.file.Path;

public class ModEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void onCommandRegister(final RegisterCommandsEvent event) {
        //Basic commands
        new IslandCommand(event.getDispatcher());
        new IslandCreateCommand(event.getDispatcher());
        new IslandInviteCommand(event.getDispatcher());
        new IslandLeaveCommand(event.getDispatcher());
        new IslandRequestTeleportCommand(event.getDispatcher());
        new IslandTeleportCommand(event.getDispatcher());
        new IslandTravelCommand(event.getDispatcher());
        new IslandHubCommand(event.getDispatcher());
        new IslandAddGroupMemberCommand(event.getDispatcher());

        //Functional commands
        new FunctionRegistryCommand(event.getDispatcher());

        //Admin commands
        new AdminGetIdCommand(event.getDispatcher());
        new AdminMenuCommand(event.getDispatcher());
        new AdminTeleportCommand(event.getDispatcher());
        new ConfigReloadCommand(event.getDispatcher());
        new AdminAddMemberCommand(event.getDispatcher());
        new DebugCommand(event.getDispatcher());
        new AdminPurgeCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
        LOGGER.info("Registered commands for " + SkyblockAddon.MOD_ID);
    }

    @SubscribeEvent
    public void onWorldSave(final WorldEvent.Save event) {
        if (event.getWorld().isClientSide()) return;
        event.getWorld().getServer().getLevel(Level.OVERWORLD).getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            final Path worldPath = event.getWorld().getServer().getWorldPath(LevelResource.ROOT).normalize();
            final Path filePath = worldPath.resolve("islanddata");

            NBTEncoder.saveToFile(cap.getIslands(), filePath); //Save islands to drive on world save
        });
    }

    @SubscribeEvent
    public void onRegisterCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(SkyblockAddonWorldCapability.class);
    }

    @SubscribeEvent
    public void onAttachCapabilitiesWorld(final AttachCapabilitiesEvent<Level> event) {
        if (event.getObject().dimension() != Level.OVERWORLD) return;
        if (event.getObject().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).isPresent())
            return;

        if (event.getObject() instanceof final ServerLevel level) {
            event.addCapability(new ResourceLocation(SkyblockAddon.MOD_ID, "properties"), new SkyblockAddonWorldProvider(event.getObject().getServer()));
        }
    }

    @SubscribeEvent
    public void onPlayerJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        UsernameCache.onPlayerLogin(event.getPlayer()); //Register user into UsernameCache
        event.getPlayer().getLevel().getServer().getLevel(Level.OVERWORLD).getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            //Load user island into cache
            cap.loadIslandIntoReverseCache(event.getPlayer());
        });
    }
}
