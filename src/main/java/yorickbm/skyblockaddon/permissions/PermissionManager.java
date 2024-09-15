package yorickbm.skyblockaddon.permissions;

import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.permissions.json.PermissionCategoryHolder;
import yorickbm.skyblockaddon.permissions.json.PermissionHolder;
import yorickbm.skyblockaddon.permissions.util.Permission;
import yorickbm.skyblockaddon.permissions.util.PermissionCategory;
import yorickbm.skyblockaddon.util.JSON.JSONEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PermissionManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private List<PermissionCategory> categories;
    private List<Permission> permissions;

    private static final PermissionManager instance = new PermissionManager();
    public static PermissionManager getInstance() {
        return instance;
    }
    public PermissionManager() {
        this.categories = new ArrayList<>();
        this.permissions = new ArrayList<>();
    }

    public void loadPermissions() {
        try {
            categories = JSONEncoder.loadFromFile(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/registries/PermissionCategoryRegistry.json"), PermissionCategoryHolder.class).categories;
            permissions = JSONEncoder.loadFromFile(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/registries/PermissionRegistry.json"), PermissionHolder.class).permissions;

        } catch (Exception ex) {
            LOGGER.error("Failed to load permission configuration.");
        }
    }

    public List<PermissionCategory> getCategories() {
        return this.categories;
    }
    public List<Permission> getPermissions() {
        return this.permissions;
    }
    public List<Permission> getPermissionsFor(String category) {
        return this.permissions.stream().filter(pm -> pm.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
    }
}
