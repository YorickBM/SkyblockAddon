package yorickbm.skyblockaddon.mixins;

import iskallia.vault.block.VaultPortalBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yorickbm.skyblockaddon.core.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.core.islands.Island;
import yorickbm.skyblockaddon.islands.InteractionHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(value = VaultPortalBlock.class, remap = false)
public class VaultPortalMixinConfig {

    private static final Map<UUID, Long> lastDenyTime = new ConcurrentHashMap<>();
    private static final long DENY_COOLDOWN_MS = 2000;

    @Inject(method = "entityInside", at = @At("HEAD"), cancellable = true)
    private void onEntityInside(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (level.isClientSide || !(entity instanceof Player player)) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        final ServerPlayer serverPlayer = (ServerPlayer) player;
        final AtomicReference<Island> standingOn = new AtomicReference<>();

        // verifyEntity returns true if the player owns/is exempt — no restriction needed
        if (InteractionHandler.verifyEntity(player, standingOn).asBoolean()) return;

        if (InteractionHandler.checkPlayerInteraction(standingOn, serverPlayer, serverLevel, pos,
                net.minecraft.world.item.ItemStack.EMPTY, "onEnterVaultPortal")) {

            final UUID uuid = player.getUUID();
            final long now = System.currentTimeMillis();
            if (now - lastDenyTime.getOrDefault(uuid, 0L) >= DENY_COOLDOWN_MS) {
                lastDenyTime.put(uuid, now);
                serverPlayer.displayClientMessage(
                        new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere"))
                                .withStyle(ChatFormatting.DARK_RED), true);
            }

            // Bounce the player back in the direction they came from
            final double yaw = Math.toRadians(player.getYRot());
            player.setDeltaMovement(Math.sin(yaw) * 0.4, 0.28, -Math.cos(yaw) * 0.4);
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(player));

            player.setPortalCooldown();
            ci.cancel();
        }
    }
}
