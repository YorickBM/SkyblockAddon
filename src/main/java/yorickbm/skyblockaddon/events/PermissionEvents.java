package yorickbm.skyblockaddon.events;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.permissions.PermissionManager;
import yorickbm.skyblockaddon.permissions.util.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class PermissionEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void onTrample(final BlockEvent.FarmlandTrampleEvent event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onTrample");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        final boolean runFail = !group.get().canDo(perms.get(0).getId());
        if (runFail) { //Group may not run this
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onEnderPearl(final EntityTeleportEvent.EnderPearl event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }
        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onEnderPearl");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        final boolean runFail = !group.get().canDo(perms.get(0).getId());
        if (runFail) { //Group may not run this
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onChorusFruit(final EntityTeleportEvent.ChorusFruit event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }
        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onChorusFruit");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        final boolean runFail = !group.get().canDo(perms.get(0).getId());
        if (runFail) { //Group may not run this
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onSleepInBed(final PlayerSleepInBedEvent event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onSleepInBed");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        final boolean runFail = !group.get().canDo(perms.get(0).getId());
        if (runFail) { //Group may not run this
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onXp(final PlayerXpEvent.PickupXp event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onXp");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        final boolean runFail = !group.get().canDo(perms.get(0).getId());
        if (runFail) { //Group may not run this
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onBonemeal(final BonemealEvent event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onBonemeal");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        final boolean runFail = !group.get().canDo(perms.get(0).getId());
        if (runFail) { //Group may not run this
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onPickup(final EntityItemPickupEvent event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onPickup");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        boolean runFail = false;
        for(final Permission perm : perms) {
            if(group.get().canDo(perm.getId())) continue;
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            final List<String> data = perm.getData().getItemsData();

            if(data.isEmpty()) {
                runFail = true;
            }
            else {
                final String pickedupItem = Objects.requireNonNull(event.getItem().getItem().getItem().getRegistryName()).toString();
                switch(PermissionManager.checkMatch(data, pickedupItem)) {
                    case SKIP, ALLOW-> { continue; }
                    case BLOCK ->  runFail = true;
                }
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onDrop(final ItemTossEvent event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onDrop");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        boolean runFail = false;
        for(final Permission perm : perms) {
            if(group.get().canDo(perm.getId())) continue;
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            final List<String> data = perm.getData().getItemsData();

            if(data.isEmpty()) {
                runFail = true;
            }
            else {
                final String droppedItem = Objects.requireNonNull(event.getEntityItem().getItem().getItem().getRegistryName()).toString();
                switch(PermissionManager.checkMatch(data, droppedItem)) {
                    case SKIP, ALLOW-> { continue; }
                    case BLOCK ->  runFail = true;
                }
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onBucket(final FillBucketEvent event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onBucket");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        boolean runFail = false;
        for(final Permission perm : perms) {
            if(group.get().canDo(perm.getId())) continue;
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            final List<String> data = perm.getData().getItemsData();

            if(data.isEmpty()) {
                runFail = true;
            }
            else {
                final String filledBucket = Objects.requireNonNull(event.getFilledBucket().getItem().getRegistryName()).toString();
                switch(PermissionManager.checkMatch(data, filledBucket)) {
                    case SKIP, ALLOW-> { continue; }
                    case BLOCK ->  runFail = true;
                }
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onMount(final EntityMountEvent event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onMount");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        boolean runFail = false;
        for(final Permission perm : perms) {
            if(group.get().canDo(perm.getId())) continue;
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            final List<String> data = perm.getData().getEntitiesData();

            if(data.isEmpty()) {
                runFail = true;
            }
            else {
                final String mountedEntity = Objects.requireNonNull(EntityType.getKey(event.getEntityBeingMounted().getType())).toString();
                switch(PermissionManager.checkMatch(data, mountedEntity)) {
                    case SKIP, ALLOW-> { continue; }
                    case BLOCK ->  runFail = true;
                }
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(!PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) {
            if(PermissionManager.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onRightClickBlock")) {
                event.getPlayer().displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
                if(event.isCancelable()) event.setCanceled(true);
                else event.setResult(Event.Result.DENY);
            }
        }
        if(!PermissionManager.verifyNetherEntity(event.getEntity(), standingOn, event.getPos()).asBoolean()) {
            final ItemStack item = event.getItemStack();
            if(!item.isEmpty() && item.getItem() == Items.FLINT_AND_STEEL) {
                final BlockState state = event.getWorld().getBlockState(event.getPos());
                if(!state.isAir() && state.getBlock().asItem() == Items.OBSIDIAN) {
                    if(PermissionManager.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onPortalIgnition")) {
                        event.getPlayer().displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
                        if(event.isCancelable()) event.setCanceled(true);
                        else event.setResult(Event.Result.DENY);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public void onRightClickEmpty(final PlayerInteractEvent.RightClickEmpty event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        if(PermissionManager.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onRightClickEmpty")) {
            event.getPlayer().displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onRightClickItem(final PlayerInteractEvent.RightClickItem event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        if(PermissionManager.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onRightClickItem")) {
            event.getPlayer().displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onLeftClickBlock(final PlayerInteractEvent.LeftClickBlock event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        if(PermissionManager.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onLeftClickBlock")) {
            event.getPlayer().displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onLeftClickEmpty(final PlayerInteractEvent.LeftClickEmpty event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        if(PermissionManager.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onLeftClickEmpty")) {
            event.getPlayer().displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onAttack(final AttackEntityEvent event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onAttack");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        boolean runFail = false;
        for(final Permission perm : perms) {
            if(group.get().canDo(perm.getId())) continue;
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            final List<String> data = perm.getData().getEntitiesData();

            if(data.isEmpty()) {
                runFail = true;
            }
            else {
                final String attackedEntity = Objects.requireNonNull(EntityType.getKey(event.getTarget().getType())).toString();
                switch(PermissionManager.checkMatch(data, attackedEntity)) {
                    case SKIP, ALLOW-> { continue; }
                    case BLOCK ->  runFail = true;
                }
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onUse(final LivingEntityUseItemEvent event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onUse");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        boolean runFail = false;
        for(final Permission perm : perms) {
            if(group.get().canDo(perm.getId())) continue;
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            final List<String> data = perm.getData().getItemsData();

            if(data.isEmpty()) {
                runFail = true;
            }
            else {
                final String usedItem = Objects.requireNonNull(event.getItem().getItem().getRegistryName()).toString();
                switch(PermissionManager.checkMatch(data, usedItem)) {
                    case SKIP, ALLOW-> { continue; }
                    case BLOCK ->  runFail = true;
                }
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(final EntityTravelToDimensionEvent event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean() &&
                PermissionManager.verifyNetherEntity(event.getEntity(), standingOn, event.getEntity().getOnPos()).asBoolean()) return;

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onPlayerChangedDimension");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        final ResourceKey<Level> toDim = event.getDimension();

        boolean runFail = false;
        for(final Permission perm : perms) {
            if (group.get().canDo(perm.getId())) continue;
            if (runFail) break; //Break loop if we determine failure
            boolean onlyNegate = true;

            // Get permission data
            final List<String> data = perm.getData().getSkyblockaddonData().stream()
                    .filter(s -> s.startsWith("dimension:"))
                    .map( s -> s.replace("dimension:", ""))
                    .collect(Collectors.toCollection(ArrayList::new));

            if(data.isEmpty()) {
                runFail = true;
            }
            else {
                switch(PermissionManager.checkMatch(data, toDim.getRegistryName().toString())) {
                    case SKIP, ALLOW-> { continue; }
                    case BLOCK ->  runFail = true;
                }
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);

            final ServerPlayer player = (ServerPlayer) event.getEntity();

            // Convert yaw to radians and calculate direction vector
            final double radianYaw = Math.toRadians(player.getYRot());
            final Vec3 direction = new Vec3(Math.sin(radianYaw), 0, -Math.cos(radianYaw));


            // Send the new movement to player & client
            player.setDeltaMovement(new Vec3(direction.x * 0.4, 0.28, direction.z * 0.4));
            player.connection.send(new ClientboundSetEntityMotionPacket(player)); //Send motion Packet
        }
    }
}
