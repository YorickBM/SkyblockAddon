package yorickbm.skyblockaddon.events;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
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
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.permissions.MatchResult;
import yorickbm.skyblockaddon.permissions.PermissionManager;
import yorickbm.skyblockaddon.permissions.json.PermissionDataHolder;
import yorickbm.skyblockaddon.permissions.util.Permission;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PermissionEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void onTrample(final BlockEvent.FarmlandTrampleEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onTrample");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        final boolean runFail = !group.get().canDo(perms.get(0).getId());
        if (runFail) { //Group may not run this
            if (event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            sendFailureMessage((ServerPlayer) event.getEntity());
        }
    }
    @SubscribeEvent
    public void onEnderPearl(final EntityTeleportEvent.EnderPearl event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onEnderPearl");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        final boolean runFail = !group.get().canDo(perms.get(0).getId());
        if (runFail) { //Group may not run this
            if (event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            sendFailureMessage((ServerPlayer) event.getEntity());
        }
    }
    @SubscribeEvent
    public void onChorusFruit(final EntityTeleportEvent.ChorusFruit event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onChorusFruit");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        final boolean runFail = !group.get().canDo(perms.get(0).getId());
        if (runFail) { //Group may not run this
            if (event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            sendFailureMessage((ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public void onSleepInBed(final PlayerSleepInBedEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onSleepInBed");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        final boolean runFail = !group.get().canDo(perms.get(0).getId());
        if (runFail) { //Group may not run this
            if (event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            sendFailureMessage((ServerPlayer) event.getEntity());
        }
    }
    @SubscribeEvent
    public void onXp(final PlayerXpEvent.PickupXp event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onXp");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        final boolean runFail = !group.get().canDo(perms.get(0).getId());
        if (runFail) { //Group may not run this
            if (event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            sendFailureMessage((ServerPlayer) event.getEntity());
        }
    }
    @SubscribeEvent
    public void onBonemeal(final BonemealEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onBonemeal");
        if(perms.isEmpty()) {
            return; //No permission to protect against it
        }

        final boolean runFail = !group.get().canDo(perms.get(0).getId());
        if (runFail) { //Group may not run this
            if (event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            sendFailureMessage((ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public void onPickup(final EntityItemPickupEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;

        final boolean runFail = processPermissions(
                group, "onPickup",
                Objects.requireNonNull(event.getItem().getItem().getItem().getRegistryName()).toString(),
                PermissionDataHolder::getItemsData
        );

        if (runFail) {
            if (event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            sendFailureMessage((ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public void onDrop(final ItemTossEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getPlayer(), event);
        if (group.isEmpty()) return;

        final boolean runFail = processPermissions(
                group, "onDrop",
                Objects.requireNonNull(event.getEntityItem().getItem().getItem().getRegistryName()).toString(),
                PermissionDataHolder::getItemsData
        );

        if (runFail) {
            if (event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            sendFailureMessage((ServerPlayer) event.getPlayer());

            event.getPlayer().addItem(event.getEntityItem().getItem());
            event.getPlayer().getInventory().setChanged();
        }
    }

    @SubscribeEvent
    public void onBucket(final FillBucketEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;

        final boolean runFail = processPermissions(
                group, "onBucket",
                Objects.requireNonNull(event.getFilledBucket().getItem().getRegistryName()).toString(),
                PermissionDataHolder::getItemsData
        );

        if (runFail) {
            if (event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            sendFailureMessage((ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public void onMount(final EntityMountEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;

        final boolean runFail = processPermissions(
                group, "onMount",
                Objects.requireNonNull(EntityType.getKey(event.getEntityBeingMounted().getType())).toString(),
                PermissionDataHolder::getEntitiesData
        );

        if (runFail) {
            if (event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            sendFailureMessage((ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if(!PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) {
            String trigger = "onRightClickBlock";
            final BlockState state = event.getWorld().getBlockState(event.getPos());

            if(event.getEntity().isShiftKeyDown() && !event.getItemStack().isEmpty()) { //Item in hand forced shift, forces PLACE BLOCK
                trigger = "onPlaceBlock";
            } else {
                final FakePlayer p = FakePlayerFactory.getMinecraft((ServerLevel) event.getWorld());
                if(state.use(event.getWorld(), p, event.getHand(), event.getHitVec()) == InteractionResult.PASS) {
                    trigger = "onPlaceBlock";
                }
                p.closeContainer();
                p.kill();
            }

            if(PermissionManager.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(),  trigger)) {
                event.getPlayer().displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
                if(event.isCancelable()) event.setCanceled(true);
                else event.setResult(Event.Result.DENY);
            }
        }

        //Nether portal ignition check from nether to over-world
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
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getPlayer(), event);
        if (group.isEmpty()) return;

        final boolean runFail = processPermissions(
                group,
                "onAttack",
                Objects.requireNonNull(EntityType.getKey(event.getTarget().getType())).toString(),
                PermissionDataHolder::getEntitiesData
        );

        if (runFail) {
            if (event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            sendFailureMessage((ServerPlayer) event.getPlayer());
        }
    }

    @SubscribeEvent
    public void onUse(final LivingEntityUseItemEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;

        final boolean runFail = processPermissions(
                group,
                "onUse",
                Objects.requireNonNull(event.getItem().getItem().getRegistryName()).toString(),
                PermissionDataHolder::getItemsData
        );

        if (runFail) {
            if (event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            sendFailureMessage((ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(final EntityTravelToDimensionEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;

        LOGGER.info(event.getDimension().location().toString());

        final boolean runFail = processPermissions(
                group,
                "onPlayerChangedDimension",
                event.getDimension().location().toString(),
                data -> data.getSkyblockaddonData().stream()
                        .filter(s -> s.startsWith("dimension:"))
                        .map(s -> s.replace("dimension:", ""))
                        .collect(Collectors.toList())
        );

        if (runFail) {
            if (event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            sendFailureMessage((ServerPlayer) event.getEntity());

            final ServerPlayer player = (ServerPlayer) event.getEntity();
            final double radianYaw = Math.toRadians(player.getYRot());
            final Vec3 direction = new Vec3(Math.sin(radianYaw), 0, -Math.cos(radianYaw));
            player.setDeltaMovement(new Vec3(direction.x * 0.4, 0.28, direction.z * 0.4));
            player.connection.send(new ClientboundSetEntityMotionPacket(player));
        }
    }

    private Optional<IslandGroup> verifyPermissionAndGroup(final Entity entity, final Event event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if (PermissionManager.verifyEntity(entity, standingOn).asBoolean()) {
            return Optional.empty();
        }

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntity(entity);
        if (group.isEmpty()) {
            if (event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            sendFailureMessage((ServerPlayer) entity);
            return Optional.empty();
        }

        return group;
    }

    private boolean processPermissions(final Optional<IslandGroup> group, final String trigger, final String matchValue, final Function<PermissionDataHolder, List<String>> dataExtractor) {
        SkyblockAddon.CustomDebugMessages(LOGGER, trigger);

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger(trigger);
        if (perms.isEmpty()) {
            SkyblockAddon.CustomDebugMessages(LOGGER, "No permissions found for trigger (ALLOWED)");
            return false; // No permission to protect against
        }
        if(group.isEmpty()) return false; //No group present should have failed before

        boolean runFail = false;
        for (final Permission perm : perms) {
            if (group.get().canDo(perm.getId())) {
                SkyblockAddon.CustomDebugMessages(LOGGER, "action is ALLOWED on " + perm.getId() + " in group " + group.get().getItem().getDisplayName().getString().trim() + "");
                continue;
            }
            if (runFail) break;

            final List<String> data = dataExtractor.apply(perm.getData());
            if (data.isEmpty()) {
                SkyblockAddon.CustomDebugMessages(LOGGER, "action is BLOCKED on " + perm.getId() + " in group " + group.get().getItem().getDisplayName().getString().trim() + "");
                runFail = true;
            } else {
                final MatchResult rslt = PermissionManager.checkMatch(data, matchValue);
                SkyblockAddon.CustomDebugMessages(LOGGER, matchValue + " is " + rslt + " on " + perm.getId() + " in group " + group.get().getItem().getDisplayName().getString().trim());
                switch (rslt) {
                    case SKIP, ALLOW -> { continue; }
                    case BLOCK -> runFail = true;
                }
            }
        }
        return runFail;
    }

    private void sendFailureMessage(final ServerPlayer player) {
        player.displayClientMessage(
                new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true
        );
    }

}
