package yorickbm.skyblockaddon.util.ModIntegration.Providers;

import de.maxhenkel.easyvillagers.blocks.AutoTraderBlock;
import de.maxhenkel.easyvillagers.blocks.TraderBlock;
import de.maxhenkel.easyvillagers.blocks.tileentity.AutoTraderTileentity;
import de.maxhenkel.easyvillagers.blocks.tileentity.TraderTileentity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.util.ModIntegration.PermissionProvider;

public class EasyVillagersEntityProvider implements PermissionProvider<BlockEntity> {
    @Override
    public Permissions getType() {
        return Permissions.InteractWithBlocks;
    }

    @Override
    public boolean validate(BlockEntity e) {
        return e instanceof TraderTileentity
                || e instanceof AutoTraderTileentity;
    }
}
