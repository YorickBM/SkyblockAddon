package yorickbm.skyblockaddon.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.gui.interfaces.SkyblockAddonMenuProvider;
import yorickbm.skyblockaddon.gui.json.GuiHolder;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.permissions.PermissionManager;
import yorickbm.skyblockaddon.permissions.util.Permission;
import yorickbm.skyblockaddon.util.JSON.JSONEncoder;

import java.util.*;
import java.util.stream.Collectors;

public class GUIManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private final HashMap<String, SkyblockAddonMenuProvider> guis;

    private static final GUIManager instance = new GUIManager();
    public static GUIManager getInstance() {
        return instance;
    }
    public GUIManager() {
        guis = new HashMap<>();
    }

    public void loadAllGUIS() {
        guis.clear(); //make sure its empty

        Collection<GuiHolder> guiHolders = JSONEncoder.loadFromFolder(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/guis/"), GuiHolder.class);
        guiHolders.forEach(gui -> guis.put(gui.getKey(), ServerGui.getProvider(gui)));

        LOGGER.info("Loaded {} gui(s).", guis.size());
    }

    /**
     * Open GUI for entity.
     *
     * @param key    - GUI key to open
     * @param entity - Entity to open GUI for.
     * @return Boolean - If GUI could be opened for entity.
     */
    public boolean openMenu(String key, Entity entity, Island context, @NotNull CompoundTag nbt) {
        if (!guis.containsKey(key)) return false; //GUI does not exist
        if (!(entity instanceof Player player)) return false; //Not valid entity

        if(!player.hasPermissions(Commands.LEVEL_ADMINS)) {
            List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onGui");
            Optional<IslandGroup> group = context.getGroupForEntity(player);

            if(group.isPresent() && !perms.isEmpty()) {
                for(Permission perm : perms) {
                    List<String> guis = perm.getData().getSkyblockaddonData().stream()
                            .filter(s -> s.startsWith("gui:"))
                            .map( s -> s.replace("gui:", "").toLowerCase())
                            .collect(Collectors.toCollection(ArrayList::new));

                    if(guis.contains(key.toLowerCase())) {
                        player.displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
                        if(!group.get().canDo(perm.getId())) return true; //Not allowed to open!!
                    }
                }
            }
        }

        SkyblockAddonMenuProvider provider = guis.get(key);
        if(context != null) provider.setContext(context);
        provider.setNBT(nbt);
        return player.openMenu(provider).isPresent();
    }

}
