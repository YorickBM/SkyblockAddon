package yorickbm.skyblockaddon.util.ModIntegration.Providers;

import net.minecraft.world.level.block.Block;
import org.cyclops.colossalchests.block.ChestWall;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.util.ModIntegration.PermissionProvider;

public class ColossalChestsProvider implements PermissionProvider<Block> {
    @Override
    public Permissions getType() {
        return Permissions.InteractWithBlocks;
    }

    @Override
    public boolean validate(Block e) {
        return e instanceof ChestWall;
    }
}
