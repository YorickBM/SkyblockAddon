package yorickbm.skyblockaddon.mixins;

import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.configs.SkyblockAddonLanguageConfig;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.islands.Permissions;
import yorickbm.skyblockaddon.util.ServerHelper;

@Mixin(ArmorStand.class)
public class ArmorstandPreventionMixin {

    @Inject(at = @At("HEAD"), method = "interactAt(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", cancellable = true)
    private void interactAt(Player p_31594_, Vec3 p_31595_, InteractionHand p_31596_, CallbackInfoReturnable<InteractionResult> cir) {

        //Ignore non overworld interactions & OP Players
        if(p_31594_.getLevel().dimension() != Level.OVERWORLD || p_31594_.hasPermissions(3)) return;

        IslandData island = SkyblockAddon.CheckOnIsland(p_31594_); //Get island player is actively residing on
        if(island == null) return; //Not an island so we do nothing

        //Determine if player has interact permission for item armostand
        if(island.getPermission(Permissions.InteractWithBlocks, p_31594_.getUUID()).isAllowed(Items.ARMOR_STAND)) return;

        //Player is not allowed so interact is FAIL result
        cir.setReturnValue(InteractionResult.FAIL);
        p_31594_.displayClientMessage(ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("toolbar.overlay.nothere"), ChatFormatting.DARK_RED), true);
    }
}
