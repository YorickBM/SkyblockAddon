package yorickbm.skyblockaddon.mixins;

import com.direwolf20.buildinggadgets.common.items.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yorickbm.skyblockaddon.core.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.core.islands.Island;
import yorickbm.skyblockaddon.core.permissions.EntityVerification;
import yorickbm.skyblockaddon.islands.InteractionHandler;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(value = {GadgetBuilding.class, GadgetDestruction.class, GadgetExchanger.class, GadgetCopyPaste.class}, remap = false)
public class BuildingGadgetsMixinConfig {

    @Inject(method = {"m_7203_"}, at = @At("HEAD"), cancellable = true)
    private void onUse(ServerLevel world, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack stack = player.getItemInHand(hand);

        String trigger;
        if ((Object) this instanceof GadgetDestruction) {
            trigger = "onBreakBlock";
        } else if ((Object) this instanceof GadgetExchanger) {
            if (!canUseGadget(player, "onPlaceBlock") || !canUseGadget(player, "onBreakBlock")) {
                player.displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
                player.containerMenu.broadcastChanges();
                cir.setReturnValue(InteractionResultHolder.success(stack));
                return;
            }
            return;
        } else {
            trigger = "onPlaceBlock";
        }

        if (!canUseGadget(player, trigger)) {
            player.displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            player.containerMenu.broadcastChanges();
            cir.setReturnValue(InteractionResultHolder.success(stack));
        }
    }

    private static boolean canUseGadget(Player player, String trigger) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        final EntityVerification verification = InteractionHandler.verifyEntity(player, standingOn);

        if (verification == EntityVerification.NOT_A_PLAYER) return true;
        if (verification == EntityVerification.NOT_IN_OVERWORLD) return true;
        if (verification == EntityVerification.IS_ADMIN) return true;
        if (verification == EntityVerification.CAP_NOT_FOUND) return true;
        if (verification == EntityVerification.IS_ISLAND_OWNER) return true;
        if (verification == EntityVerification.NOT_ON_ISLAND) return true;

        return !InteractionHandler.checkPlayerInteraction(
                standingOn,
                (ServerPlayer) player,
                (ServerLevel) player.getLevel(),
                player.getOnPos(),
                player.getMainHandItem(),
                trigger
        );
    }
}
