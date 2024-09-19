package yorickbm.skyblockaddon.permissions;

import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.permissions.json.PermissionHolder;
import yorickbm.skyblockaddon.permissions.util.Permission;
import yorickbm.skyblockaddon.util.JSON.JSONEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PermissionManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private List<Permission> permissions;

    private static final PermissionManager instance = new PermissionManager();
    public static PermissionManager getInstance() {
        return instance;
    }
    public PermissionManager() {
        this.permissions = new ArrayList<>();
    }

    public void loadPermissions() {
        try {
            permissions = JSONEncoder.loadFromFile(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/registries/PermissionRegistry.json"), PermissionHolder.class).permissions;
            LOGGER.info("Loaded {} island permission(s).", permissions.size());
        } catch (Exception ex) {
            LOGGER.error("Failed to load permission configuration.");
        }
    }

    public List<Permission> getPermissions() {
        return this.permissions;
    }
    public List<Permission> getPermissionsFor(String category) {
        return this.permissions.stream().filter(pm -> pm.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
    }

    public List<Permission> getPermissionsForTrigger(String trigger) {
        return this.permissions.stream().filter(pm -> pm.hasTrigger(trigger)).collect(Collectors.toList());
    }
}
