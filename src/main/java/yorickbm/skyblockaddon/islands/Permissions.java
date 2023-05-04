package yorickbm.skyblockaddon.islands;

public enum Permissions {
    PlaceBlocks(0),
    DestroyBlocks(1),
    TrampleFarmland(2),

    InteractWithBlocks(3),
    InteractWithRedstoneItems(4),
    InteractWithGroundItems(5),

    UseChorusfruit(6),
    UseEnderpearl(7),
    UseBed(8),
    UseBucket(9),
    UseBonemeal(10),

    CollectXP(11);

    private final int order;
    int getOrder(){ return order; }

    Permissions(int order) {
        this.order = order;
    }
}
