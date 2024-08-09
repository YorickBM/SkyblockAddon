package yorickbm.skyblockaddon.gui.util;

import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface GuiContext {

    void teleportTo(Entity entity);
    boolean kickMember(Entity source, UUID entity);

    void setSpawnPoint(Vec3i point);
    void toggleVisibility();

    Component parseTextComponent(@NotNull Component original);

}
