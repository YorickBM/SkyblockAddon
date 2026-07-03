package yorickbm.skyblockaddon.mixins;

import de.maxhenkel.easyvillagers.net.MessagePickUpVillager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yorickbm.skyblockaddon.core.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.core.islands.InteractionValidator;
import yorickbm.skyblockaddon.core.islands.Island;
import yorickbm.skyblockaddon.core.islands.IslandGroup;
import yorickbm.skyblockaddon.core.permissions.PermissionManager;
import yorickbm.skyblockaddon.islands.InteractionHandler;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(value = MessagePickUpVillager.class, remap = false)
public class EasyVillagersPickupMixinConfig {

    @Inject(method = "executeServerSide", at = @At("HEAD"), cancellable = true, remap = false)
    private void onExecuteServerSide(NetworkEvent.Context context, CallbackInfo ci) {
        final ServerPlayer player = context.getSender();
        if (player == null) return;

        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if (InteractionHandler.verifyEntity(player, standingOn).asBoolean()) return;

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntityUUID(player.getUUID());
        if (group.isEmpty()) {
            player.displayClientMessage(
                    new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere"))
                            .withStyle(ChatFormatting.DARK_RED), true);
            ci.cancel();
            return;
        }

        final boolean blocked = InteractionValidator.checkPermission(
                group.get(),
                PermissionManager.getInstance().getPermissionsForTrigger("onPickupVillager"),
                Map.of("entity", "minecraft:villager"));

        if (blocked) {
            player.displayClientMessage(
                    new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere"))
                            .withStyle(ChatFormatting.DARK_RED), true);
            ci.cancel();
        }
    }
}
