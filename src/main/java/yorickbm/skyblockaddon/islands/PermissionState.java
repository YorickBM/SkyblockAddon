package yorickbm.skyblockaddon.islands;

public enum PermissionState {
    EVERYONE(1),
    MEMBERS(2),
    OWNER(3);

    private final int value;
    PermissionState(int i) {
        value = i;
    }

    public int getValue() { return value; }

    public static PermissionState fromValue(int value) {
        switch(value) {
            case 3: return OWNER;
            case 2: return MEMBERS;
            default: return EVERYONE;
        }
    }
}
