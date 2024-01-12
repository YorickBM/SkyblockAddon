package yorickbm.skyblockaddon.util.ModIntegration.Providers;

import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.util.ModIntegration.PermissionProvider;

public class VanillaEntitysProvider implements PermissionProvider<BlockEntity> {
    @Override
    public Permissions getType() {
        return Permissions.InteractWithBlocks;
    }

    @Override
    public boolean validate(BlockEntity e) {
        return (e instanceof Container)
                || (e instanceof WorldlyContainerHolder)
                || (e instanceof MenuProvider)
                || (e != null && (e.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()))
                || e instanceof EnchantmentTableBlockEntity
                || e instanceof SignBlockEntity;
    }
}
