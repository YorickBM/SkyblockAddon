package yorickbm.skyblockaddon.mixins;

import com.hollingsworth.arsnouveau.common.block.tile.PortalTile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yorickbm.skyblockaddon.core.islands.Island;
import yorickbm.skyblockaddon.islands.InteractionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Ars Nouveau's Warp Portal (ars_nouveau:portal) works differently from
 * the Vault/Nether portals: PortalBlock#entityInside explicitly SKIPS
 * players ("if (entity instanceof Player) return;") and only handles
 * non-player entities. Players are instead teleported from
 * PortalTile#tick(), which every tick grabs every entity in the block's
 * bounding box via Level#getEntitiesOfClass(...) and teleports each one
 * directly, inline - there is no single, separate "warp a player" method
 * call we can @Inject into or @Redirect cleanly.
 *
 * Instead of touching the teleport logic itself, this mixin redirects the
 * ENTITY LOOKUP at the top of tick(): it calls the real
 * getEntitiesOfClass(...), then filters out any ServerPlayer who doesn't
 * have permission (shares the "onEnterPortal" trigger with
 * VaultPortalMixinConfig/NetherPortalMixinConfig, so the same kind of
 * permission entry works for all three). A denied player is simply never
 * in the list tick() iterates over, so they're never teleported - no
 * separate cancel/bounce-back needed, unlike the other two portals, since
 * there's no equivalent single interaction point to cancel here.
 */
@Mixin(value = PortalTile.class, remap = false)
public abstract class ArsNouveauPortalMixinConfig {

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_45976_(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;",
                    remap = false
            )
    )
    private List<Entity> skyblockaddon$filterDeniedPlayers(Level level, Class<Entity> entityClass, AABB box) {
        List<Entity> entities = level.getEntitiesOfClass(entityClass, box);

        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return entities;
        }

        PortalTile self = (PortalTile) (Object) this;
        BlockPos pos = self.getBlockPos();

        List<Entity> allowed = new ArrayList<>();
        for (Entity entity : entities) {
            if (!(entity instanceof ServerPlayer player)) {
                allowed.add(entity);
                continue;
            }

            AtomicReference<Island> standingOn = new AtomicReference<>();
            if (InteractionHandler.verifyEntity(player, standingOn).asBoolean()) {
                // Not on any known island - fall through to the mod's own behavior.
                allowed.add(entity);
                continue;
            }

            boolean denied = InteractionHandler.checkPlayerInteraction(
                    standingOn, player, serverLevel, pos, ItemStack.EMPTY, "onEnterPortal");

            if (!denied) {
                allowed.add(entity);
            }
            // else: denied - simply excluded, this player won't be warped this tick.
        }
        return allowed;
    }
}
