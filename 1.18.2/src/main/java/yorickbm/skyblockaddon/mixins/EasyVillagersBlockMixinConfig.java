package yorickbm.skyblockaddon.mixins;

import de.maxhenkel.easyvillagers.events.BlockEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yorickbm.skyblockaddon.core.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.core.islands.Island;
import yorickbm.skyblockaddon.islands.InteractionHandler;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(value = BlockEvents.class, remap = false)
public class EasyVillagersBlockMixinConfig {

    @Inject(method = "onBlockClick", at = @At("HEAD"), cancellable = true, remap = false)
    private void onBlockClick(PlayerInteractEvent.RightClickBlock event, CallbackInfo ci) {
        if (!(event.getPlayer() instanceof final ServerPlayer player)) return;

        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(
                event.getWorld().getBlockState(event.getPos()).getBlock());
        if (blockId == null || !blockId.getNamespace().equals("easy_villagers")) {
            return;
        }

        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if (InteractionHandler.verifyEntity(player, standingOn).asBoolean()) return;

        if (InteractionHandler.checkPlayerInteraction(standingOn, player,
                (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onEVBlockInteract")) {
            // Immediately resync block entity to client to undo client-side visual prediction
            final BlockEntity be = event.getWorld().getBlockEntity(event.getPos());
            if (be != null) {
                final var packet = be.getUpdatePacket();
                if (packet != null) player.connection.send(packet);
            }
            player.displayClientMessage(
                    new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere"))
                            .withStyle(ChatFormatting.DARK_RED), true);
            ci.cancel();
        }
    }
}
