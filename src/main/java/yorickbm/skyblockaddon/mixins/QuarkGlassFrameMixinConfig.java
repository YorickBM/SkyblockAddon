package yorickbm.skyblockaddon.mixins;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.content.building.entity.GlassItemFrame;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.permissions.PermissionManager;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(value = {GlassItemFrame.class}, remap= false)
public abstract class QuarkGlassFrameMixinConfig {
    private static final Logger LOGGER = LogManager.getLogger();

    @Shadow public abstract BlockPos getBehindPos();

    @Inject(method = {"m_6096_"}, at = {@At("HEAD")}, cancellable = true)
    public void interact(final Player player, @Nonnull final InteractionHand hand, final CallbackInfoReturnable<InteractionResult> cir) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(player, standingOn).asBoolean()) return;

        final ItemStack item = ((GlassItemFrame)(Object)this).getItem();
        if (!player.isShiftKeyDown() && !item.isEmpty() && !(item.getItem() instanceof BannerItem)) {
            if(PermissionManager.checkPlayerInteraction(standingOn, (ServerPlayer) player, (ServerLevel) player.getLevel(), this.getBehindPos(), item,  "onRightClickBlock")) {
                player.displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
                cir.setReturnValue(InteractionResult.FAIL); //Force success
            }
        }
    }
}
