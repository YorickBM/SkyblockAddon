package yorickbm.skyblockaddon.islands;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public enum Permission {
    Teleport(Items.ENDER_EYE),
    Invite(Items.OAK_BOAT),

    PlaceBlocks(Items.OAK_LOG),
    BreakBlocks(Items.GOLDEN_PICKAXE),
    TrampleFarmland(Items.FARMLAND),
    OpenBlocks(Items.CHEST),

    EnderPearl(Items.ENDER_PEARL),
    ChorusFruit(Items.CHORUS_FRUIT),

    UseBucket(Items.BUCKET),
    UseBed(Items.RED_BED),
    UseBonemeal(Items.BONE_MEAL),

    InteractWithXP(Items.EXPERIENCE_BOTTLE),
    InteractWithGroundItems(Items.EMERALD);

    private final Item displayItem;

    Permission(Item type) {
        displayItem = type;
    }
    public Item getDisplayItem() { return displayItem; }



}
