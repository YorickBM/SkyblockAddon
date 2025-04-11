package yorickbm.skyblockaddon.events;

import com.mojang.brigadier.Command;
import iskallia.vault.entity.entity.DollMiniMeEntity;
import iskallia.vault.entity.entity.SpiritEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;

public class PlayerEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onVoidFall(final LivingDamageEvent event) {
        if(event.getSource().equals(DamageSource.OUT_OF_WORLD)) {
            final Entity entity = event.getEntity();

            if(entity instanceof ServerPlayer || entity instanceof DollMiniMeEntity || entity instanceof SpiritEntity) {
                if(entity.getLevel().dimension() != Level.OVERWORLD) return; //Ignore non overworld events

                entity.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
                    final Island island = cap.getIslandPlayerIsStandingOn(entity);
                    if(island == null) return;
                    event.setCanceled(true); //Cancel damage

                    entity.resetFallDistance();
                    island.teleportTo(entity);
                    entity.resetFallDistance();

                });
            }
        }
    }

    @SubscribeEvent
    public void onJoin(final PlayerEvent.PlayerLoggedInEvent event) {
        if(!event.getPlayer().hasPermissions(Commands.LEVEL_GAMEMASTERS)) return; //Only run for Gamemasters+ since they may purge
        event.getPlayer().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            final int purgable = cap.getPurgableIslands().size();
            if(purgable < 12) return; //Don't bother with less than 12 to notify.

            event.getPlayer().sendMessage(
                    new TextComponent(String.format(SkyBlockAddonLanguage.getLocalizedString("admin.purge.data"), purgable)).withStyle(ChatFormatting.DARK_RED),
                    event.getPlayer().getUUID()
            );
        });
    }
}
