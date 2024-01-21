package yorickbm.skyblockaddon.util.modintegration.providers;

import net.mehvahdjukaar.supplementaries.common.items.SlingshotItem;
import net.minecraft.world.item.Item;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.util.modintegration.PermissionProvider;

public class SlingshotProvider implements PermissionProvider<Item> {
    @Override
    public Permissions getType() {
        return Permissions.PlaceBlocks;
    }

    @Override
    public boolean validate(Item e) {
        return e instanceof SlingshotItem;
    }
}
