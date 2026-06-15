package yorickbm.skyblockaddon.core.events;

import yorickbm.skyblockaddon.core.islands.Island;

import java.util.UUID;

/**
 * Fired before the built-in GUI is opened for a player.
 * Cancelable — if cancelled the default GUILibrary GUI is suppressed,
 * allowing a third-party GUI mod to show its own screen instead.
 */
public class IslandGuiOpenEvent extends IslandEvent {

    public enum GuiType {
        OVERVIEW, PERMISSIONS, MEMBERS, SETTINGS, TRAVEL, BIOMES, GROUPS, ADMIN_OVERVIEW
    }

    private final UUID playerUUID;
    private final GuiType guiType;

    public IslandGuiOpenEvent(final Island island, final UUID playerUUID, final GuiType guiType) {
        super(island);
        this.playerUUID = playerUUID;
        this.guiType = guiType;
    }

    public UUID getPlayerUUID() { return playerUUID; }
    public GuiType getGuiType() { return guiType; }
}
