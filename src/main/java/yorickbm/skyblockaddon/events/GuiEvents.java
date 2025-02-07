package yorickbm.skyblockaddon.events;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.system.CallbackI;
import yorickbm.guilibrary.events.GuiDrawItemEvent;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.IslandEvents;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.util.FunctionRegistry;
import yorickbm.skyblockaddon.util.ServerHelper;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class GuiEvents {

    @SubscribeEvent
    public void itemRenderer(GuiDrawItemEvent event) {
        if(event.isCanceled()) return; //Skip if canceled

        //Ignore condition filter if no island_id is set
        if(!event.getHolder().getData().contains("island_id")) { return; }

        event.getHolder().getOwner().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByUUID(event.getHolder().getData().getUUID("island_id"));

            //Ignore condition if not found
            if(island == null) { return; }

            if(event.getItemHolder().hasCondition("is_admin")) {
                //TODO: Add condition for admin permission
                event.setCanceled(!island.isOwner(event.getHolder().getOwner().getUUID()) && !event.getHolder().getOwner().hasPermissions(Commands.LEVEL_ADMINS));
            } else if(event.getItemHolder().hasCondition("!is_admin")) {
                event.setCanceled(island.isOwner(event.getHolder().getOwner().getUUID()) || event.getHolder().getOwner().hasPermissions(Commands.LEVEL_ADMINS));
            }

            if(event.getItemHolder().hasCondition("is_part")) {
                event.setCanceled(!island.isPartOf(event.getHolder().getOwner().getUUID()));
            } else if(event.getItemHolder().hasCondition("!is_part")) {
                event.setCanceled(island.isPartOf(event.getHolder().getOwner().getUUID()));
            }

        });
    }

    //TODO Add Dynamic Data Parser for Item Renderer

    //TODO Add registries on filler items

    @SubscribeEvent
    public void onTeleportToIslandEvent(IslandEvents.TeleportToIsland event) {
        if(event.isCanceled()) return; //Skip if canceled

        //Teleport target to island
        event.getIsland().teleportTo(event.getTarget());
        event.getHolder().close();

        //Send message of success
        event.getTarget().sendMessage(new TextComponent(
                SkyBlockAddonLanguage.getLocalizedString("island.travel")
        ).withStyle(ChatFormatting.GREEN),event.getTarget().getUUID());
    }

    @SubscribeEvent
    public void onLeaveIslandEvent(IslandEvents.LeaveIsland event) {
        //Run leave command
        Objects.requireNonNull(event.getTarget().getServer()).getCommands().performCommand(event.getTarget().createCommandSourceStack(), "/island leave");
    }

    @SubscribeEvent
    public void onSetSpawnPointEvent(IslandEvents.SetSpawnPoint event) {
        if(event.isCanceled()) return; //Skip if canceled

        if(!event.getIsland().getIslandBoundingBox().isInside(event.getTarget().getOnPos())) {
            event.getTarget().sendMessage(new TextComponent(
                    SkyBlockAddonLanguage.getLocalizedString("island.setspawn.outside")
            ).withStyle(ChatFormatting.RED),event.getTarget().getUUID());

            event.setCanceled(true);
            return;
        }

        event.getIsland().setSpawnPoint(event.getTarget().blockPosition());
        event.getHolder().update();
    }

    @SubscribeEvent
    public void onChangeIslandVisiblityEvent(IslandEvents.ChangeVisibility event) {
        event.getIsland().toggleVisibility();
        event.getHolder().update();
    }

    @SubscribeEvent
    public void onTravelToIslandEvent(IslandEvents.TravelToIsland event) {
        CompoundTag modData = event.getClickedItem().getOrCreateTagElement(SkyblockAddon.MOD_ID);
        if(!modData.contains("island_id")) {
            event.setCanceled(true);
            return;
        }

        event.getTarget().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByUUID(event.getHolder().getData().getUUID("island_id"));

            //Island is not found
            if(island == null) {
                event.setCanceled(true);
                return;
            }

            island.teleportTo(event.getTarget());
            event.getTarget().sendMessage(new TextComponent(
                    SkyBlockAddonLanguage.getLocalizedString("island.travel")
            ).withStyle(ChatFormatting.GREEN),event.getTarget().getUUID());

        });
    }

    @SubscribeEvent
    public void onInviteNewMemberEvent(IslandEvents.InviteNewMember event) {
        CompoundTag modData = event.getClickedItem().getOrCreateTagElement(SkyblockAddon.MOD_ID);
        if(!modData.contains("group_id")) {
            event.setCanceled(true);
            return;
        }

        event.getHolder().close();
        event.getTarget().sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.group.click.to.add"))
                        .withStyle(ChatFormatting.GREEN)
                        .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                "/island group "
                                        +  event.getIsland().getGroup(modData.getUUID("group_id")).getItem().getDisplayName().getString().trim()
                                        + " addMember "
                                        + event.getTarget().getDisplayName().getString().trim())))
                ,event.getTarget().getUUID());
    }

    @SubscribeEvent
    public void onCreateNewGroupEvent(IslandEvents.CreateNewGroup event) {
        event.getHolder().close();

        //Create clickable command
        UUID id = UUID.randomUUID();
        Function<ServerPlayer, Boolean> function = executor -> {
            if(executor.getMainHandItem().isEmpty()) {
                executor.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.group.failure").formatted(executor.getMainHandItem().getDisplayName().getString(), Objects.requireNonNull(executor.getMainHandItem().getItem().getRegistryName()).toString().split(":")[1]))
                                .withStyle(ChatFormatting.RED)
                        ,executor.getUUID());
                return false; //Keep hash function alive.
            }

            ServerHelper.playSongToPlayer(executor, SoundEvents.NOTE_BLOCK_CHIME, SkyblockAddon.UI_SUCCESS_VOL, 1f);
            event.getIsland().addGroup(new IslandGroup(UUID.randomUUID(), executor.getMainHandItem(), false));

            executor.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.group.created").formatted(executor.getMainHandItem().getDisplayName().getString().trim(), Objects.requireNonNull(executor.getMainHandItem().getItem().getRegistryName()).toString().split(":")[1].trim()))
                            .withStyle(ChatFormatting.GREEN)
                    ,executor.getUUID());

            return true;
        };
        FunctionRegistry.registerFunction(id, function, 15);

        event.getTarget().sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.group.create"))
                        .withStyle(ChatFormatting.GREEN)
                        .withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, FunctionRegistry.getCommand(id))))
                ,event.getTarget().getUUID());

    }

    @SubscribeEvent
    public void onUpdateBiomeEvent(IslandEvents.UpdateBiome event) {
        CompoundTag modData = event.getClickedItem().getOrCreateTagElement(SkyblockAddon.MOD_ID);
        if(!modData.contains("biome")) {
            event.setCanceled(true);
            return;
        }

        String biome = modData.getString("biome");
        event.getIsland().updateBiome(biome, event.getTarget().getLevel());
        event.getHolder().close();

        event.getTarget().sendMessage(
                new TextComponent(
                        String.format(SkyBlockAddonLanguage.getLocalizedString("island.biome"), biome))
                        .withStyle(ChatFormatting.GREEN),
                event.getTarget().getUUID());
    }

    @SubscribeEvent
    public void onKickMemberEvent(IslandEvents.KickMember event) {
        CompoundTag modData = event.getClickedItem().getOrCreateTagElement(SkyblockAddon.MOD_ID);
        if(!modData.contains("player_id")) {
            event.setCanceled(true);
            return;
        }

        UUID playerUUID = modData.getUUID("player_id");
        event.getIsland().kickMember(event.getTarget(), playerUUID);
        event.getHolder().close();

        event.getTarget().sendMessage(
                new TextComponent(
                        SkyBlockAddonLanguage.getLocalizedString("island.member.kicked")
                                .formatted( UsernameCache.getBlocking(playerUUID) )
                        ).withStyle(ChatFormatting.GREEN),
                event.getTarget().getUUID());
    }

    @SubscribeEvent
    public void onSetPlayerGroupEvent(IslandEvents.SetPlayerGroup event) {
        CompoundTag modData = event.getClickedItem().getOrCreateTagElement(SkyblockAddon.MOD_ID);
        CompoundTag guiData = event.getHolder().getData();

        if(!guiData.contains("player_id") || !modData.contains("group_id")) {
            event.setCanceled(true);
            return;
        }

        UUID groupUUID = modData.getUUID("group_id");
        UUID playerUUID = guiData.getUUID("player_id");

        event.getIsland().addMember(playerUUID, groupUUID);
        event.getHolder().close();

        event.getTarget().sendMessage(
                new TextComponent(
                    SkyBlockAddonLanguage.getLocalizedString("island.member.group.set")
                        .formatted(
                                UsernameCache.getBlocking(playerUUID),
                                event.getIsland().getGroup(groupUUID).getItem().getDisplayName().getString().trim()
                        )
                    ).withStyle(ChatFormatting.GREEN),
                event.getTarget().getUUID());
    }

    @SubscribeEvent
    public void onRemoveGroup(IslandEvents.RemoveGroup event) {
        CompoundTag guiData = event.getHolder().getData();
        if(!guiData.contains("group_id")) {
            event.setCanceled(true);
            return;
        }

        UUID groupUUID = guiData.getUUID("group_id");
        IslandGroup group = event.getIsland().getGroup(groupUUID);
        event.getHolder().close();

        if(event.getIsland().removeGroup(groupUUID)) {
            event.getTarget().sendMessage(new TextComponent(String.format(SkyBlockAddonLanguage.getLocalizedString("island.group.remove.success"),
                            group.getItem().getDisplayName().getString().trim()))
                            .withStyle(ChatFormatting.RED)
                    ,event.getTarget().getUUID());

            return;
        }

        event.getTarget().sendMessage(new TextComponent(String.format(SkyBlockAddonLanguage.getLocalizedString("island.group.remove.failed"),
                        group.getItem().getDisplayName().getString().trim()))
                        .withStyle(ChatFormatting.RED)
                ,event.getTarget().getUUID());
        event.setCanceled(true);

    }

    @SubscribeEvent
    public void onSetGroupPermissionEvent(IslandEvents.SetGroupPermission event) {
        CompoundTag modData = event.getClickedItem().getOrCreateTagElement(SkyblockAddon.MOD_ID);
        CompoundTag guiData = event.getHolder().getData();

        if(!guiData.contains("group_id") || !modData.contains("permission_id")) {
            event.setCanceled(true);
            return;
        }

        UUID groupUUID = guiData.getUUID("group_id");
        String permissionId = modData.getString("permission_id");

        event.getIsland().getGroup(groupUUID).inversePermission(permissionId);
        event.getHolder().update();
    }

}
