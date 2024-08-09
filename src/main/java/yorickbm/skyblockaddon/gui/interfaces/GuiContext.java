package yorickbm.skyblockaddon.gui.interfaces;

import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public interface GuiContext extends ContextParser {

    void teleportTo(Entity entity);
    boolean kickMember(Entity source, UUID entity);

    void setSpawnPoint(Vec3i point);
    void toggleVisibility();

    void updateBiome(String biome);
}
