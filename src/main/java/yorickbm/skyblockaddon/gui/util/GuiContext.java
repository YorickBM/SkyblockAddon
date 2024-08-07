package yorickbm.skyblockaddon.gui.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public interface GuiContext {

    void teleportTo(Entity entity);
    boolean kickMember(Entity source, UUID entity);

    Component parseTextComponent(Component original);

}
