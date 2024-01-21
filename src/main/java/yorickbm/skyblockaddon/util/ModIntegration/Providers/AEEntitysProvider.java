package yorickbm.skyblockaddon.util.ModIntegration.Providers;

import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.grid.AENetworkBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.util.ModIntegration.PermissionProvider;

public class AEEntitysProvider implements PermissionProvider<BlockEntity> {
    @Override
    public Permissions getType() {
        return Permissions.InteractWithBlocks;
    }

    @Override
    public boolean validate(BlockEntity e) {
        return e instanceof AEBaseBlockEntity ||
                e instanceof AENetworkBlockEntity;
    }
}
