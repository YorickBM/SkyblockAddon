package yorickbm.skyblockaddon.gui;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.gui.json.GuiHolder;
import yorickbm.skyblockaddon.gui.util.GuiContext;
import yorickbm.skyblockaddon.gui.util.SkyblockAddonMenuProvider;
import yorickbm.skyblockaddon.util.JSON.JSONEncoder;

import java.util.Collection;
import java.util.HashMap;

public class GUIManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private HashMap<String, SkyblockAddonMenuProvider> guis;

    private static GUIManager instance = new GUIManager();
    public static GUIManager getInstance() {
        return instance;
    }
    public GUIManager() {
        guis = new HashMap<>();
    }

    public void loadAllGUIS() {
        Collection<GuiHolder> guiHolders = JSONEncoder.loadFromFolder(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/guis/"), GuiHolder.class);
        guiHolders.forEach(gui -> guis.put(gui.getKey(), ServerGui.getProvider(gui)));

        LOGGER.info("Loaded: " + guis.size() + " gui(s).");
    }

    /**
     * Open GUI for entity.
     *
     * @param key    - GUI key to open
     * @param entity - Entity to open GUI for.
     * @return Boolean - If GUI could be opened for entity.
     */
    public boolean openMenu(String key, Entity entity, GuiContext context) {
        if (!guis.containsKey(key)) return false; //GUI does not exist
        if (!(entity instanceof Player player)) return false; //Not valid entity

        SkyblockAddonMenuProvider provider = guis.get(key);
        provider.setContext(context);
        return player.openMenu(provider).isPresent();
    }

}
