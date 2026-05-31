package yorickbm.skyblockaddon.events;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yorickbm.skyblockaddon.SkyBlockAddon;
import yorickbm.skyblockaddon.capabilities.SkyblockAddonWorldCapability;
import yorickbm.skyblockaddon.capabilities.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.core.JSON.PermissionDataJson;
import yorickbm.skyblockaddon.core.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.core.islands.Island;
import yorickbm.skyblockaddon.core.islands.IslandGroup;
import yorickbm.skyblockaddon.core.islands.IslandManager;
import yorickbm.skyblockaddon.core.islands.InteractionValidator;
import yorickbm.skyblockaddon.core.permissions.PermissionManager;
import yorickbm.skyblockaddon.islands.InteractionHandler;
import yorickbm.skyblockaddon.util.ForgeConverter;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class PermissionEvents {

    // --- Permission events ---

    @SubscribeEvent
    public void onTrample(final BlockEvent.FarmlandTrampleEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;
        denyIf(InteractionValidator.checkMatchPermission(group.get(),
                PermissionManager.getInstance().getPermissionsForTrigger("onTrample"),
                Objects.requireNonNull(EntityType.getKey(event.getEntity().getType())).toString(),
                PermissionDataJson::getEntitiesData), event, (ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onEnderPearl(final EntityTeleportEvent.EnderPearl event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;
        denyIf(InteractionValidator.checkMatchPermission(group.get(),
                PermissionManager.getInstance().getPermissionsForTrigger("onEnderPearl"),
                "minecraft:ender_pearl",
                PermissionDataJson::getItemsData), event, (ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onChorusFruit(final EntityTeleportEvent.ChorusFruit event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;
        denyIf(InteractionValidator.checkMatchPermission(group.get(),
                PermissionManager.getInstance().getPermissionsForTrigger("onChorusFruit"),
                "minecraft:chorus_fruit",
                PermissionDataJson::getItemsData), event, (ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onSleepInBed(final PlayerSleepInBedEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;
        final String bedBlock = Objects.requireNonNull(
                event.getEntity().getLevel().getBlockState(event.getPos()).getBlock().getRegistryName()).toString();
        denyIf(InteractionValidator.checkMatchPermission(group.get(),
                PermissionManager.getInstance().getPermissionsForTrigger("onSleepInBed"),
                bedBlock,
                PermissionDataJson::getBlocksData), event, (ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onXp(final PlayerXpEvent.PickupXp event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;
        denyIf(InteractionValidator.checkMatchPermission(group.get(),
                PermissionManager.getInstance().getPermissionsForTrigger("onXp"),
                Objects.requireNonNull(EntityType.getKey(event.getOrb().getType())).toString(),
                PermissionDataJson::getEntitiesData), event, (ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onBonemeal(final BonemealEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;
        denyIf(InteractionValidator.checkMatchPermission(group.get(),
                PermissionManager.getInstance().getPermissionsForTrigger("onBonemeal"),
                Objects.requireNonNull(event.getBlock().getBlock().getRegistryName()).toString(),
                PermissionDataJson::getBlocksData), event, (ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onPickup(final EntityItemPickupEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;
        denyIf(InteractionValidator.checkMatchPermission(group.get(), PermissionManager.getInstance().getPermissionsForTrigger("onPickup"),
                Objects.requireNonNull(event.getItem().getItem().getItem().getRegistryName()).toString(),
                PermissionDataJson::getItemsData), event, (ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onDrop(final ItemTossEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getPlayer(), event);
        if (group.isEmpty()) return;
        final boolean blocked = InteractionValidator.checkMatchPermission(group.get(), PermissionManager.getInstance().getPermissionsForTrigger("onDrop"),
                Objects.requireNonNull(event.getEntityItem().getItem().getItem().getRegistryName()).toString(),
                PermissionDataJson::getItemsData);
        if (blocked) {
            denyEvent(event, (ServerPlayer) event.getPlayer());
            event.getPlayer().addItem(event.getEntityItem().getItem());
            event.getPlayer().getInventory().setChanged();
        }
    }

    @SubscribeEvent
    public void onBucket(final FillBucketEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;
        final ItemStack bucket = event.getFilledBucket() == null ? event.getEmptyBucket() : event.getFilledBucket();
        denyIf(InteractionValidator.checkMatchPermission(group.get(), PermissionManager.getInstance().getPermissionsForTrigger("onBucket"),
                Objects.requireNonNull(bucket.getItem().getRegistryName()).toString(),
                PermissionDataJson::getItemsData), event, (ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onMount(final EntityMountEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;
        denyIf(InteractionValidator.checkMatchPermission(group.get(), PermissionManager.getInstance().getPermissionsForTrigger("onMount"),
                Objects.requireNonNull(EntityType.getKey(event.getEntityBeingMounted().getType())).toString(),
                PermissionDataJson::getEntitiesData), event, (ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onAttack(final AttackEntityEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getPlayer(), event);
        if (group.isEmpty()) return;
        denyIf(InteractionValidator.checkMatchPermission(group.get(), PermissionManager.getInstance().getPermissionsForTrigger("onAttack"),
                Objects.requireNonNull(EntityType.getKey(event.getTarget().getType())).toString(),
                PermissionDataJson::getEntitiesData), event, (ServerPlayer) event.getPlayer());
    }

    @SubscribeEvent
    public void onUse(final LivingEntityUseItemEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;
        denyIf(InteractionValidator.checkMatchPermission(group.get(), PermissionManager.getInstance().getPermissionsForTrigger("onUse"),
                Objects.requireNonNull(event.getItem().getItem().getRegistryName()).toString(),
                PermissionDataJson::getItemsData), event, (ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(final EntityTravelToDimensionEvent event) {
        final Optional<IslandGroup> group = verifyPermissionAndGroup(event.getEntity(), event);
        if (group.isEmpty()) return;
        final boolean blocked = InteractionValidator.checkMatchPermission(group.get(),
                PermissionManager.getInstance().getPermissionsForTrigger("onPlayerChangedDimension"),
                event.getDimension().location().toString(),
                data -> data.getSkyblockaddonData().stream()
                        .filter(s -> s.startsWith("dimension:"))
                        .map(s -> s.replace("dimension:", ""))
                        .collect(Collectors.toList()));
        if (blocked) {
            denyEvent(event, (ServerPlayer) event.getEntity());
            final ServerPlayer player = (ServerPlayer) event.getEntity();
            final double radianYaw = Math.toRadians(player.getYRot());
            final Vec3 direction = new Vec3(Math.sin(radianYaw), 0, -Math.cos(radianYaw));
            player.setDeltaMovement(new Vec3(direction.x * 0.4, 0.28, direction.z * 0.4));
            player.connection.send(new ClientboundSetEntityMotionPacket(player));
        }
    }

    // --- Interaction block/item events ---

    @SubscribeEvent
    public void onClickEntity(final PlayerInteractEvent.EntityInteract event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if (!InteractionHandler.verifyEntity(event.getEntity(), standingOn).asBoolean()) {
            if (InteractionHandler.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onRightClickEntity")) {
                denyEvent(event, (ServerPlayer) event.getPlayer());
            }
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if (!InteractionHandler.verifyEntity(event.getEntity(), standingOn).asBoolean()) {
            final String trigger = ServerHelper.isBlockInteractable(event.getWorld(), event.getPos(), event.getPlayer(), event.getHand(), event.getHitVec()) ? "onRightClickBlock" : "onPlaceBlock";
            if (InteractionHandler.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), trigger)) {
                denyEvent(event, (ServerPlayer) event.getPlayer());
            }
        }
        checkNetherPortalIgnition(event.getEntity(), standingOn, event.getPos(), event.getItemStack(), event);
    }

    @SubscribeEvent
    public void onRightClickEmpty(final PlayerInteractEvent.RightClickEmpty event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if (InteractionHandler.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;
        if (InteractionHandler.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onRightClickEmpty")) {
            denyEvent(event, (ServerPlayer) event.getPlayer());
        }
    }

    @SubscribeEvent
    public void onRightClickItem(final PlayerInteractEvent.RightClickItem event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if (InteractionHandler.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;
        if (InteractionHandler.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onRightClickItem")) {
            denyEvent(event, (ServerPlayer) event.getPlayer());
        }
    }

    @SubscribeEvent
    public void onEntityPlace(final BlockEvent.EntityPlaceEvent event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if (!InteractionHandler.verifyEntity(event.getEntity(), standingOn).asBoolean()) {
            if (InteractionHandler.checkPlayerInteraction(standingOn, (ServerPlayer) event.getEntity(), (ServerLevel) event.getWorld(), event.getPos(), ((ServerPlayer) event.getEntity()).getMainHandItem(), "onPlaceBlock")) {
                denyEvent(event, (ServerPlayer) event.getEntity());
            }
        }
        checkNetherPortalIgnition(event.getEntity(), standingOn, event.getPos(), ((ServerPlayer) Objects.requireNonNull(event.getEntity())).getMainHandItem(), event);
    }

    @SubscribeEvent
    public void onLeftClickBlock(final PlayerInteractEvent.LeftClickBlock event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if (InteractionHandler.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;
        if (InteractionHandler.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onLeftClickBlock")) {
            denyEvent(event, (ServerPlayer) event.getPlayer());
        }
    }

    @SubscribeEvent
    public void onLeftClickEmpty(final PlayerInteractEvent.LeftClickEmpty event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if (InteractionHandler.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;
        if (InteractionHandler.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onLeftClickEmpty")) {
            denyEvent(event, (ServerPlayer) event.getPlayer());
        }
    }

    @SubscribeEvent
    public void onMobSpawn(final LivingSpawnEvent.CheckSpawn event) {
        final Optional<SkyblockAddonWorldCapability> cap = event.getEntity().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).resolve();
        if (cap.isEmpty()) return;
        final Island island = IslandManager.getInstance().getIslandByPos(ForgeConverter.ForgeToInternalVec3i(event.getEntity().getOnPos()));
        if (island == null) return;
        final String trigger = event.getEntity().getType().getCategory() == MobCategory.MONSTER ? "OnHostileMobSpawn" : "OnPassiveMobSpawn";
        final boolean blocked = InteractionValidator.checkMatchPermission(
                island.getGroupForEntityUUID(event.getEntity().getUUID()).orElse(null),
                PermissionManager.getInstance().getPermissionsForTrigger(trigger),
                event.getEntity().getType().toString(),
                PermissionDataJson::getEntitiesData);
        if (blocked) {
            event.setResult(Event.Result.DENY);
            if (event.isCancelable()) event.setCanceled(true);
        }
    }

    // --- Helpers ---

    private Optional<IslandGroup> verifyPermissionAndGroup(final Entity entity, final Event event) {
        final AtomicReference<Island> standingOn = new AtomicReference<>();
        if (InteractionHandler.verifyEntity(entity, standingOn).asBoolean()) return Optional.empty();

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntityUUID(entity.getUUID());
        if (group.isEmpty()) {
            denyEvent(event, (ServerPlayer) entity);
            return Optional.empty();
        }
        return group;
    }

    private void denyIf(final boolean blocked, final Event event, final ServerPlayer player) {
        if (!blocked) return;
        denyEvent(event, player);
    }

    private void denyEvent(final Event event, final ServerPlayer player) {
        if (event.isCancelable()) event.setCanceled(true);
        else event.setResult(Event.Result.DENY);
        player.displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
    }

    private void checkNetherPortalIgnition(final Entity entity, final AtomicReference<Island> standingOn,
                                           final net.minecraft.core.BlockPos pos, final ItemStack item, final Event event) {
        if (!InteractionHandler.verifyNetherEntity(entity, standingOn, pos).asBoolean()) {
            if (!item.isEmpty() && item.getItem() == Items.FLINT_AND_STEEL) {
                final var state = ((ServerLevel) Objects.requireNonNull(entity.getLevel())).getBlockState(pos);
                if (!state.isAir() && state.getBlock().asItem() == Items.OBSIDIAN) {
                    if (InteractionHandler.checkPlayerInteraction(standingOn, (ServerPlayer) entity, (ServerLevel) entity.getLevel(), pos, item, "onPortalIgnition")) {
                        denyEvent(event, (ServerPlayer) entity);
                    }
                }
            }
        }
    }
}
