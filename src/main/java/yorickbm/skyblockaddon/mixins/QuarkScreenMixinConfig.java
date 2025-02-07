package yorickbm.skyblockaddon.mixins;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.base.handler.InventoryTransferHandler;
import yorickbm.guilibrary.interfaces.ServerInterface;

@Mixin(value = {InventoryTransferHandler.class}, remap = false)
public class QuarkScreenMixinConfig {
    @Inject(method = {"transfer"}, at = {@At("HEAD")}, cancellable = true)
    private static void transfer(Player player, boolean isRestock, boolean smart, CallbackInfo ci) {

        if (player.containerMenu instanceof ServerInterface)
            ci.cancel();
    }
}