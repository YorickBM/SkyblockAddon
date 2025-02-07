package yorickbm.skyblockaddon.mixins;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.content.tools.item.PickarangItem;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.permissions.PermissionManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(value = {PickarangItem.class}, remap = false)
public abstract class QuarkPickarangMixinConfig {
    private static final Logger LOGGER = LogManager.getLogger();

    @Inject(method = {"m_7203_"}, at = {@At("HEAD")}, cancellable = true)
    private void use(Level worldIn, Player playerIn, @NotNull InteractionHand handIn, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {

        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(playerIn, standingOn).asBoolean()) return;

        ItemStack handItem = playerIn.getItemInHand(handIn);
        if(PermissionManager.checkPlayerInteraction(standingOn, (ServerPlayer) playerIn, (ServerLevel) worldIn, playerIn.getOnPos(), handItem, "onQuarkPickarangMixin")) {
            playerIn.displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            playerIn.containerMenu.broadcastChanges(); //Force sync
            cir.setReturnValue(InteractionResultHolder.success(handItem)); //Force success

            //Re-add item since it sometimes disappears.
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> {
                playerIn.containerMenu.broadcastChanges(); //Force sync
                playerIn.getInventory().placeItemBackInInventory(handItem);
            }, 45, TimeUnit.MILLISECONDS); // 1 ish tick delay

        }
    }
}
