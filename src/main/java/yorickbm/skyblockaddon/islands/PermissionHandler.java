package yorickbm.skyblockaddon.islands;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.stream.Collectors;

public class PermissionHandler {

    private HashMap<Permission, PermissionState> permissions = new HashMap<>();

    /**
     * Load permission handler with permission states
     * @param data
     */
    public PermissionHandler(CompoundTag data) {
        initializeDefaults();

        //Put values from compound tag in instead of default
        for (String permission : data.getAllKeys().stream().filter(s -> {
            try {
                return Permission.valueOf(s) != null;
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }).collect(Collectors.toList())) {
            permissions.put(Permission.valueOf(permission), PermissionState.valueOf(data.getString(permission)));
        }
    }

    /**
     * Load default permission settings into class
     */
    private void initializeDefaults() {
        for(Permission permission : Permission.values()) {
            permissions.put(permission, PermissionState.MEMBERS);
        }
    }

    /**
     * Convert class into CompoundTag with data
     * @return CompoundTag
     */
    public CompoundTag serialize() {
        CompoundTag data = new CompoundTag();

        for(Permission permission : Permission.values()) {
            data.putString(permission.name(), permissions.get(permission).name());
        }

        return data;
    }

    /**
     * Get all registered permissions and their minimal state
     * @return HashMap of permissions
     */
    public HashMap<Permission, PermissionState> permissions() {
        return permissions;
    }

    /**
     * Determine if a state is higher or equal to the minimal required state for permission
     * @param permission Permission to check against
     * @param state State to check with
     * @return If state is allowed
     */
    public boolean isStateAllowed(Permission permission, PermissionState state) {
        if(!permissions.containsKey(permission)) return false; //Permission not set

        PermissionState allowedState = permissions.get(permission);
        return state.getValue() >= allowedState.getValue();
    }

}
