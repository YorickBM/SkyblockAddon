package yorickbm.skyblockaddon.util.modintegration.providers;

import iskallia.vault.block.entity.WardrobeTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.util.modintegration.PermissionProvider;

public class VaultEntitysProvider implements PermissionProvider<BlockEntity> {
    @Override
    public Permissions getType() {
        return Permissions.InteractWithBlocks;
    }

    @Override
    public boolean validate(BlockEntity e) {
        return e instanceof WardrobeTileEntity;
    }
}
