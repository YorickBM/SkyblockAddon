package yorickbm.skyblockaddon.util;

public class TerralithFoundException extends RuntimeException {
    public TerralithFoundException() {
        super("Terralith mod has been detected, please remove this mod to use SkyblockAddon mod!");
    }
}
