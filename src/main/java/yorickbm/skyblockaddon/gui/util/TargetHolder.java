package yorickbm.skyblockaddon.gui.util;

import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class TargetHolder {
    Entity entity;
    UUID uuid;

    public TargetHolder(Entity entity, UUID uuid) {
        this.entity = entity;
        this.uuid = uuid;
    }

    public Entity getEntity() {
        return entity;
    }

    public UUID getUuid() {
        return uuid;
    }
}
