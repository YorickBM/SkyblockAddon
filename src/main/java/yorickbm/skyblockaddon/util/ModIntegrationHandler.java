package yorickbm.skyblockaddon.util;

import net.mehvahdjukaar.supplementaries.common.items.SlingshotItem;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.CapabilityItemHandler;
import org.cyclops.colossalchests.block.ChestWall;
import yorickbm.skyblockaddon.islands.Permissions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class ModIntegrationHandler {

    private static final Map<Predicate<BlockEntity>, Permissions> entityBuilder = new HashMap<>();
    private static final Map<Predicate<Block>, Permissions> blockBuilder = new HashMap<>();
    private static final Map<Predicate<Item>, Permissions> itemBuilder = new HashMap<>();

    public static Permissions getPermissionForBlockEntity(BlockEntity entity) {
        Optional<Permissions> found = entityBuilder.entrySet().stream().filter(e -> e.getKey().test(entity)).map(Map.Entry::getValue).findFirst();
        return found.orElse(null);

    }
    public static Permissions getPermissionForBlock(Block block) {
        Optional<Permissions> found = blockBuilder.entrySet().stream().filter(e -> e.getKey().test(block)).map(Map.Entry::getValue).findFirst();
        return found.orElse(null);

    }

    public static Permissions getPermissionForItem(Item item) {
        Optional<Permissions> found = itemBuilder.entrySet().stream().filter(e -> e.getKey().test(item)).map(Map.Entry::getValue).findFirst();
        return found.orElse(null);
    }

    static {
        entityBuilder.put(e -> (e instanceof Container)
                || (e instanceof WorldlyContainerHolder)
                || (e instanceof MenuProvider)
                || (e != null && (e.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()))
                || e instanceof EnchantmentTableBlockEntity
                || e instanceof SignBlockEntity
                , Permissions.InteractWithBlocks);

        blockBuilder.put(e -> e instanceof AnvilBlock
                || e instanceof RespawnAnchorBlock
                //|| e instanceof SmithingTableBlock
                || e instanceof GrindstoneBlock
                || e instanceof StonecutterBlock
                || e instanceof CartographyTableBlock
                || e instanceof CraftingTableBlock
                || e instanceof ComposterBlock
                || e instanceof TrapDoorBlock
                || e instanceof DoorBlock
                || e instanceof MenuProvider
                , Permissions.InteractWithBlocks);

        blockBuilder.put(e -> (e instanceof BasePressurePlateBlock
                        || e instanceof LeverBlock
                        || e instanceof ButtonBlock
                        || e instanceof RepeaterBlock
                        || e instanceof ComparatorBlock)
                , Permissions.InteractWithRedstoneItems);

        if(ModList.get().isLoaded("colossalchests")) {
            blockBuilder.put(e -> (
                    e instanceof ChestWall
            ), Permissions.InteractWithBlocks);
        }

        if(ModList.get().isLoaded("supplementaries")) {
            itemBuilder.put(e -> (
                    e instanceof SlingshotItem
            ), Permissions.PlaceBlocks);
        }

        itemBuilder.put(e -> (
                e instanceof SpawnEggItem
                || e instanceof EggItem
                ), Permissions.InteractWithBlocks);
    }

}
