package yorickbm.skyblockaddon.util.modintegration.providers;

import com.refinedmods.refinedstorage.blockentity.BaseBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.util.modintegration.PermissionProvider;

public class RefinedStorageEntityProvider  implements PermissionProvider<BlockEntity> {
    @Override
    public Permissions getType() {
        return Permissions.InteractWithBlocks;
    }

    @Override
    public boolean validate(BlockEntity e) {
        return e instanceof BaseBlockEntity;
    }
}
