package yorickbm.skyblockaddon.util.modintegration.providers;

import net.minecraft.world.level.block.Block;
import org.cyclops.colossalchests.block.ChestWall;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.util.modintegration.PermissionProvider;

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
