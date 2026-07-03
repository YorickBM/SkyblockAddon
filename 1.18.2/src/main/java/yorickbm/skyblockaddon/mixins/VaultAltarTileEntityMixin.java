package yorickbm.skyblockaddon.mixins;

import iskallia.vault.block.entity.VaultAltarTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VaultAltarTileEntity.class, remap = false)
public abstract class VaultAltarTileEntityMixin {

    /** Distinguishes our own confirmed re-invocation from the original call. */
    @Unique
    private boolean skyblockaddon$confirmedPower = false;

    @Inject(method = "onAltarPowered", at = @At("HEAD"), cancellable = true)
    private void skyblockaddon$debouncePower(CallbackInfo ci) {
        if (skyblockaddon$confirmedPower) {
            // This is our own re-invocation after the debounce window passed
            // and the signal was confirmed still present - let it run for real.
            skyblockaddon$confirmedPower = false;
            return;
        }

        VaultAltarTileEntity self = (VaultAltarTileEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null || level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        // Cancel the immediate, unconditional trigger.
        ci.cancel();

        BlockPos pos = self.getBlockPos();
        MinecraftServer server = serverLevel.getServer();
        int debounceTicks = 2;
        int targetTick = server.getTickCount() + debounceTicks;

        server.tell(new TickTask(targetTick, () -> {
            if (!level.isLoaded(pos) || !level.hasNeighborSignal(pos)) {
                // Signal is already gone again - it was spurious/denied, ignore it.
                return;
            }

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof VaultAltarTileEntity confirmedAltar) {
                ((VaultAltarTileEntityMixin) (Object) confirmedAltar).skyblockaddon$confirmedPower = true;
                confirmedAltar.onAltarPowered();
            }
        }));
    }
}
