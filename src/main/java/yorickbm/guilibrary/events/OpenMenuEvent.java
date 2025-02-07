package yorickbm.guilibrary.events;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import yorickbm.guilibrary.GUILibraryRegistry;
import yorickbm.guilibrary.GUIType;
import yorickbm.guilibrary.interfaces.MenuProviderInterface;
import yorickbm.guilibrary.interfaces.ServerInterface;

@Cancelable
public class OpenMenuEvent extends Event {
    private static final Logger LOGGER = LogManager.getLogger();

    protected MenuProviderInterface provider;
    protected ServerPlayer target;

    public OpenMenuEvent(String id, ServerPlayer target, CompoundTag data) {
        GUIType guiStructure = GUILibraryRegistry.getValue(id);
        if(guiStructure == null) {
            setCanceled(true);
            LOGGER.error(String.format("Failed to open menu '%s'.", id));
            return;
        }

        this.provider = new MenuProviderInterface() {
            @Override
            public @NotNull Component getDisplayName() {
                return guiStructure.getTitle();
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
                this.container = new ServerInterface(i, inventory, player, guiStructure, data);
                return this.container;
            }
        };
        this.target = target;
    }

    public MenuProviderInterface getProvider() { return this.provider; }
    public ServerPlayer getTarget() { return this.target; }

}
