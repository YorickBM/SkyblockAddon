package yorickbm.skyblockaddon.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public interface GuiContext {

    void teleportTo(Entity entity);

    Component parseTextComponent(Component original);

}
