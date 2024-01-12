package yorickbm.skyblockaddon.util.ModIntegration.Providers;

import de.maxhenkel.easyvillagers.blocks.AutoTraderBlock;
import de.maxhenkel.easyvillagers.blocks.TraderBlock;
import net.minecraft.world.level.block.Block;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.util.ModIntegration.PermissionProvider;

public class EasyVillagersBlocksProvider implements PermissionProvider<Block> {
    @Override
    public Permissions getType() {
        return Permissions.InteractWithBlocks;
    }

    @Override
    public boolean validate(Block e) {
        return e instanceof TraderBlock
                || e instanceof AutoTraderBlock;
    }
}
