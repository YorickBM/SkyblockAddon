package yorickbm.skyblockaddon.util.ModIntegration;

import yorickbm.skyblockaddon.islands.Permissions;

import java.util.Map;
import java.util.function.Predicate;

public interface PermissionProvider<T> {
    Permissions getType();
    boolean validate(T e);
    default void register(Map<Predicate<T>, Permissions> builder) {
        builder.put(this::validate, getType());
    }
}
