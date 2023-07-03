package yorickbm.skyblockaddon.mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.base.handler.InventoryTransferHandler;
import yorickbm.skyblockaddon.SkyblockAddon;

@Mixin(value = {InventoryTransferHandler.class}, remap = false)
public class QuarkScreenMixinConfigMix {
    @Inject(method = {"transfer"}, at = {@At("HEAD")}, cancellable = true)
    private static void transfer(Player player, boolean isRestock, boolean smart, CallbackInfo ci) {
        if (SkyblockAddon.isScreenBlocked(player))
            ci.cancel();
    }
}
