package yorickbm.skyblockaddon.util.modintegration.providers;

import net.minecraft.world.level.block.*;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.util.modintegration.PermissionProvider;

public class RedstoneBlocksProvider implements PermissionProvider<Block> {
    @Override
    public Permissions getType() {
        return Permissions.InteractWithRedstoneItems;
    }

    @Override
    public boolean validate(Block e) {
        return e instanceof BasePressurePlateBlock
                || e instanceof LeverBlock
                || e instanceof ButtonBlock
                || e instanceof RepeaterBlock
                || e instanceof ComparatorBlock;
    }
}
