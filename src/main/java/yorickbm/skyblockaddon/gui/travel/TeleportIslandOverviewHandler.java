package yorickbm.skyblockaddon.gui.travel;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import yorickbm.skyblockaddon.gui.ServerOnlyHandler;
import yorickbm.skyblockaddon.islands.IslandData;

public class TeleportIslandOverviewHandler extends ServerOnlyHandler<IslandData> {
    protected TeleportIslandOverviewHandler(int syncId, Inventory playerInventory, int rows, IslandData data) {
        super(syncId, playerInventory, 5, data);
    }

    @Override
    protected boolean isRightSlot(int slot) {
        return false;
    }

    @Override
    protected void fillInventoryWith(Player player) {
        for(int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack item = null;

            if(i >= 10 && i <= 34 && i%9 != 0 && i%9 != 8) {
                //BIOME So keep empty
            } else {
                item = new ItemStack(Items.GRAY_STAINED_GLASS_PANE);
                item.setHoverName(new TextComponent(""));
            }

            if(item != null && item instanceof ItemStack) setItem(i, 0, item);
        }
    }

    @Override
    protected boolean handleSlotClicked(ServerPlayer player, int index, Slot slot, int clickType) {
        return false;
    }
}
