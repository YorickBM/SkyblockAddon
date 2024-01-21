package yorickbm.skyblockaddon.util.modintegration.providers;

import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.*;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.util.modintegration.PermissionProvider;

public class VanillaBlocksProvider implements PermissionProvider<Block> {
    @Override
    public Permissions getType() {
        return Permissions.InteractWithBlocks;
    }

    @Override
    public boolean validate(Block e) {
        return e instanceof AnvilBlock
                || e instanceof RespawnAnchorBlock
                || e instanceof GrindstoneBlock
                || e instanceof StonecutterBlock
                || e instanceof CartographyTableBlock
                || e instanceof CraftingTableBlock
                || e instanceof ComposterBlock
                || e instanceof TrapDoorBlock
                || e instanceof DoorBlock
                || e instanceof MenuProvider;
    }
}
