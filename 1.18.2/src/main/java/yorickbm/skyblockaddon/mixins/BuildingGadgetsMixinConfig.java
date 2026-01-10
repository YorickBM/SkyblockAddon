package yorickbm.skyblockaddon.mixins;

import com.direwolf20.buildinggadgets.common.items.AbstractGadget;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yorickbm.skyblockaddon.core.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.core.islands.Island;
import yorickbm.skyblockaddon.islands.InteractionHandler;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(value = AbstractGadget.class, remap = false)
public class BuildingGadgetsMixinConfig {
    @Inject(
            method = "canUse",
            at = @At("HEAD"),
            cancellable = true
    )
    private void blockGadgetUsage(
            ItemStack tool,
            Player player,
            CallbackInfoReturnable<Boolean> cir
    ) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(InteractionHandler.verifyEntity(player, standingOn).asBoolean()) return;

        if(
                InteractionHandler.checkPlayerInteraction(standingOn, (ServerPlayer) player, (ServerLevel) player.getLevel(), player.getOnPos(), player.getMainHandItem(),  "onPlaceBlock")
                ||
                InteractionHandler.checkPlayerInteraction(standingOn, (ServerPlayer) player, (ServerLevel) player.getLevel(), player.getOnPos(), player.getMainHandItem(),  "onBreakBlock")
        ) {
            player.displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            cir.setReturnValue(false); //Force success
        }
    }
}
