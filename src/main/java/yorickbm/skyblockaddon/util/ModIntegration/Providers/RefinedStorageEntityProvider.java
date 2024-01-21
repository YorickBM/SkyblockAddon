package yorickbm.skyblockaddon.util.ModIntegration.Providers;

import com.refinedmods.refinedstorage.blockentity.BaseBlockEntity;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import de.maxhenkel.easyvillagers.blocks.tileentity.AutoTraderTileentity;
import de.maxhenkel.easyvillagers.blocks.tileentity.TraderTileentity;
import net.minecraft.world.level.block.entity.BlockEntity;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.util.ModIntegration.PermissionProvider;

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
