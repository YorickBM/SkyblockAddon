package yorickbm.skyblockaddon.mixins;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yorickbm.skyblockaddon.core.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.core.islands.Island;
import yorickbm.skyblockaddon.core.permissions.EntityVerification;
import yorickbm.skyblockaddon.islands.InteractionHandler;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(value = NetherPortalBlock.class, remap = false)
public class NetherPortalMixinConfig {

    private static final Map<UUID, Long> lastDenyTime = new ConcurrentHashMap<>();
    private static final long DENY_COOLDOWN_MS = 2000L;

    @Inject(method = "m_7892_", at = @At("HEAD"), cancellable = true)
    private void onEntityInside(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (level.isClientSide || !(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;

        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel) level;

        ServerPlayer serverPlayer = (ServerPlayer) player;
        AtomicReference<Island> standingOn = new AtomicReference<>();

        // Nether portals can be entered from either side. verifyEntity() does a
        // direct island-by-position lookup, which only works for Overworld
        // coordinates. From the Nether side, coordinates are on the 1:8 scale,
        // so we need verifyNetherEntity() instead, which scales the given
        // BlockPos back up to find the corresponding Overworld island.
        // verifyNetherEntity() itself returns NOT_IN_NETHER if called while not
        // actually in the Nether, so this has to be a conditional choice, not a
        // straight replacement.
        boolean inNether = serverLevel.dimension() == Level.NETHER;
        EntityVerification verification = inNether
                ? InteractionHandler.verifyNetherEntity(player, standingOn, pos)
                : InteractionHandler.verifyEntity(player, standingOn);
        if (verification.asBoolean()) {
            return;
        }

        if (InteractionHandler.checkPlayerInteraction(
                standingOn,
                serverPlayer,
                serverLevel,
                pos,
                ItemStack.EMPTY,
                "onEnterPortal")) {

            UUID uuid = player.getUUID();
            long now = System.currentTimeMillis();

            if (now - lastDenyTime.getOrDefault(uuid, 0L) >= DENY_COOLDOWN_MS) {
                lastDenyTime.put(uuid, now);
                serverPlayer.displayClientMessage(
                        new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere"))
                                .withStyle(ChatFormatting.DARK_RED),
                        true);
            }

            double yaw = Math.toRadians(player.getYRot());
            player.setDeltaMovement(
                    Math.sin(yaw) * 0.4,
                    0.28,
                    -Math.cos(yaw) * 0.4);
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(player));
            player.resetFallDistance();

            ci.cancel();
        }
    }
}
