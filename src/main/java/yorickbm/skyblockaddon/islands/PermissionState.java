package yorickbm.skyblockaddon.islands;

import java.util.Locale;

public enum PermissionState {
    EVERYONE(1),
    MEMBERS(2),
    OWNERS(3);

    private final int value;
    PermissionState(int i) {
        value = i;
    }

    public int getValue() { return value; }
    public String Camelcase() { return this.name().substring(0, 1).toUpperCase(Locale.ROOT) + this.name().substring(1).toLowerCase(Locale.ROOT); }

    public static PermissionState fromValue(int value) {
        switch(value) {
            case 2: return MEMBERS;
            case 1: return EVERYONE;
            default: return OWNERS;
        }
    }
}
