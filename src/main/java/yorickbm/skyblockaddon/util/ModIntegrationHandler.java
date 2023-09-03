package yorickbm.skyblockaddon.util;

import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import org.cyclops.colossalchests.block.ChestWall;
import yorickbm.skyblockaddon.islands.Permissions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class ModIntegrationHandler {

    private static Map<Predicate<BlockEntity>, Permissions> entityBuilder = new HashMap<>();
    private static Map<Predicate<Block>, Permissions> blockBuilder = new HashMap<>();

    public static Permissions getPermissionForBlockEntity(BlockEntity entity) {
        Optional<Permissions> found = entityBuilder.entrySet().stream().filter(e -> e.getKey().test(entity)).map(Map.Entry::getValue).findFirst();
        if(found.isEmpty()) return null;

        return found.get();
    }
    public static Permissions getPermissionForBlock(Block block) {
        Optional<Permissions> found = blockBuilder.entrySet().stream().filter(e -> e.getKey().test(block)).map(Map.Entry::getValue).findFirst();
        if(found.isEmpty()) return null;

        return found.get();
    }

    static {
        entityBuilder.put(e -> (e instanceof Container)
                || (e instanceof WorldlyContainerHolder)
                || (e instanceof MenuProvider)
                || (e != null && (e.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()))
                || e instanceof EnchantmentTableBlockEntity
                , Permissions.InteractWithBlocks);

        blockBuilder.put(e -> e instanceof AnvilBlock
                || e instanceof RespawnAnchorBlock
                || e instanceof SmithingTableBlock
                || e instanceof GrindstoneBlock
                || e instanceof StonecutterBlock
                || e instanceof CartographyTableBlock
                || e instanceof CraftingTableBlock
                || e instanceof ComposterBlock
                || e instanceof TrapDoorBlock
                || e instanceof DoorBlock
                || e instanceof MenuProvider
                , Permissions.InteractWithBlocks);

        blockBuilder.put(e -> e != null &&
                (e instanceof BasePressurePlateBlock
                || e instanceof LeverBlock
                || e instanceof ButtonBlock
                || e instanceof RepeaterBlock
                || e instanceof ComparatorBlock)
                , Permissions.InteractWithRedstoneItems);

        blockBuilder.put(e -> e != null && (
                e instanceof ChestWall
                ), Permissions.InteractWithBlocks);
    }

}
