package yorickbm.skyblockaddon.util.modintegration.providers;

import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.util.modintegration.PermissionProvider;

public class SpawnEggProvider implements PermissionProvider<Item> {
    @Override
    public Permissions getType() {
        return Permissions.InteractWithBlocks;
    }

    @Override
    public boolean validate(Item e) {
        return e instanceof SpawnEggItem
                || e instanceof EggItem;
    }
}
