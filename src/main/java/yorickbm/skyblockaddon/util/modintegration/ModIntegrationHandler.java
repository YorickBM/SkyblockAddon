package yorickbm.skyblockaddon.util.modintegration;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.util.modintegration.providers.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class ModIntegrationHandler {

    private static final Logger LOGGER = LogManager.getLogger();

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

    public static void setup() {
        new VanillaEntitysProvider().register(entityBuilder);
        new VanillaBlocksProvider().register(blockBuilder);
        new RedstoneBlocksProvider().register(blockBuilder);
        new SpawnEggProvider().register(itemBuilder);

        if(ModList.get().isLoaded("colossalchests")) {
            LOGGER.info("Colossal Chests - Has been integrated into SkyblockAddon.");
            new ColossalChestsProvider().register(blockBuilder);
        }

        if(ModList.get().isLoaded("supplementaries")) {
            LOGGER.info("Supplementaries - Has been integrated into SkyblockAddon.");
            new SlingshotProvider().register(itemBuilder);
        }

        if(ModList.get().isLoaded("the_vault")) {
            LOGGER.info("The Vault - Has been integrated into SkyblockAddon.");
            new VaultEntitysProvider().register(entityBuilder);
        }

        if(ModList.get().isLoaded("ae2")) {
            LOGGER.info("Applied Energistics 2 - Has been integrated into SkyblockAddon.");
            new AEEntitysProvider().register(entityBuilder);
        }

        if(ModList.get().isLoaded("refinedstorage")) {
            LOGGER.info("Refined Storage - Has been integrated into SkyblockAddon.");
            new RefinedStorageEntityProvider().register(entityBuilder);
        }

        if(ModList.get().isLoaded("easy_villagers")) {
            LOGGER.info("Easy Villagers - Has been integrated into SkyblockAddon.");
            new EasyVillagersEntityProvider().register(entityBuilder);
        }
    }

}
