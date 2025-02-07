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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PermissionEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void onTrample(BlockEvent.FarmlandTrampleEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onTrample");
        if(perms.isEmpty()) return; //No permission to protect against it

        if(!group.get().canDo(perms.get(0).getId())) { //Group may not run this
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onEnderPearl(EntityTeleportEvent.EnderPearl event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onEnderPearl");
        if(perms.isEmpty()) return; //No permission to protect against it

        if(!group.get().canDo(perms.get(0).getId())) { //Group may not run this
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onChorusFruit(EntityTeleportEvent.ChorusFruit event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onChorusFruit");
        if(perms.isEmpty()) return; //No permission to protect against it

        if(!group.get().canDo(perms.get(0).getId())) { //Group may not run this
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onSleepInBed(PlayerSleepInBedEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onSleepInBed");
        if(perms.isEmpty()) return; //No permission to protect against it

        if(!group.get().canDo(perms.get(0).getId())) { //Group may not run this
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onXp(PlayerXpEvent.PickupXp event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onXp");
        if(perms.isEmpty()) return; //No permission to protect against it

        if(!group.get().canDo(perms.get(0).getId())) { //Group may not run this
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onBonemeal(BonemealEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onBonemeal");
        if(perms.isEmpty()) return; //No permission to protect against it

        if(!group.get().canDo(perms.get(0).getId())) { //Group may not run this
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onPickup");
        if(perms.isEmpty()) return; //No permission to protect against it

        boolean runFail = false;
        for(Permission perm : perms) {
            if(group.get().canDo(perm.getId())) continue;
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            List<String> data = perm.getData().getItemsData();

            if(data.isEmpty()) runFail = true;
            else {
                String pickedupItem = Objects.requireNonNull(event.getItem().getItem().getItem().getRegistryName()).toString();
                boolean onlyNegate = true;

                for(String item : data) {
                    boolean isNegation = item.startsWith("!");
                    Pattern itemToCheck = isNegation ? Pattern.compile(item.substring(1), Pattern.CASE_INSENSITIVE) : Pattern.compile(item, Pattern.CASE_INSENSITIVE);

                    runFail = isNegation != itemToCheck.matcher(pickedupItem).matches();

                    if(!isNegation) onlyNegate = false;
                    if(runFail) break; //Failure reached
                }

                if(!runFail && onlyNegate) runFail = true;
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onDrop(ItemTossEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onDrop");
        if(perms.isEmpty()) return; //No permission to protect against it

        boolean runFail = false;
        for(Permission perm : perms) {
            if(group.get().canDo(perm.getId())) continue;
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            List<String> data = perm.getData().getItemsData();

            if(data.isEmpty()) runFail = true;
            else {
                String droppedItem = Objects.requireNonNull(event.getEntityItem().getItem().getItem().getRegistryName()).toString();
                boolean onlyNegate = true;

                for(String item : data) {
                    boolean isNegation = item.startsWith("!");
                    Pattern itemToCheck = isNegation ? Pattern.compile(item.substring(1), Pattern.CASE_INSENSITIVE) : Pattern.compile(item, Pattern.CASE_INSENSITIVE);

                    runFail = isNegation != itemToCheck.matcher(droppedItem).matches();

                    if(!isNegation) onlyNegate = false;
                    if(runFail) break; //Failure reached
                }

                if(!runFail && onlyNegate) runFail = true;
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onBucket(FillBucketEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onBucket");
        if(perms.isEmpty()) return; //No permission to protect against it

        boolean runFail = false;
        for(Permission perm : perms) {
            if(group.get().canDo(perm.getId())) continue;
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            List<String> data = perm.getData().getItemsData();

            if(data.isEmpty()) runFail = true;
            else {
                String filledBucket = Objects.requireNonNull(event.getFilledBucket().getItem().getRegistryName()).toString();
                boolean onlyNegate = true;

                for(String fluid : data) {
                    boolean isNegation = fluid.startsWith("!");
                    Pattern fluidToCheck = isNegation ? Pattern.compile(fluid.substring(1), Pattern.CASE_INSENSITIVE) : Pattern.compile(fluid, Pattern.CASE_INSENSITIVE);

                    runFail = isNegation != fluidToCheck.matcher(filledBucket).matches();

                    if(!isNegation) onlyNegate = false;
                    if(runFail) break; //Failure reached
                }

                if(!runFail && onlyNegate) runFail = true;
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onMount(EntityMountEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onMount");
        if(perms.isEmpty()) return; //No permission to protect against it

        boolean runFail = false;
        for(Permission perm : perms) {
            if(group.get().canDo(perm.getId())) continue;
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            List<String> data = perm.getData().getEntitiesData();

            if(data.isEmpty()) runFail = true;
            else {
                String mountedEntity = Objects.requireNonNull(EntityType.getKey(event.getEntityBeingMounted().getType())).toString();
                boolean onlyNegate = true;

                for(String entity : data) {
                    boolean isNegation = entity.startsWith("!");
                    Pattern entityToCheck = isNegation ? Pattern.compile(entity.substring(1), Pattern.CASE_INSENSITIVE) : Pattern.compile(entity, Pattern.CASE_INSENSITIVE);

                    runFail = isNegation != entityToCheck.matcher(mountedEntity).matches();

                    if(!isNegation) onlyNegate = false;
                    if(runFail) break; //Failure reached
                }

                if(!runFail && onlyNegate) runFail = true;
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(!PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) {
            if(PermissionManager.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onRightClickBlock")) {
                event.getPlayer().displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
                if(event.isCancelable()) event.setCanceled(true);
                else event.setResult(Event.Result.DENY);
            }
        }
        if(!PermissionManager.verifyNetherEntity(event.getEntity(), standingOn, event.getPos()).asBoolean()) {
            ItemStack item = event.getItemStack();
            if(!item.isEmpty() && item.getItem() == Items.FLINT_AND_STEEL) {
                BlockState state = event.getWorld().getBlockState(event.getPos());
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
    public void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        if(PermissionManager.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onRightClickEmpty")) {
            event.getPlayer().displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        if(PermissionManager.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onRightClickItem")) {
            event.getPlayer().displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        if(PermissionManager.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onLeftClickBlock")) {
            event.getPlayer().displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }
    @SubscribeEvent
    public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        if(PermissionManager.checkPlayerInteraction(standingOn, (ServerPlayer) event.getPlayer(), (ServerLevel) event.getWorld(), event.getPos(), event.getItemStack(), "onLeftClickEmpty")) {
            event.getPlayer().displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onAttack");
        if(perms.isEmpty()) return; //No permission to protect against it

        boolean runFail = false;
        for(Permission perm : perms) {
            if(group.get().canDo(perm.getId())) continue;
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            List<String> data = perm.getData().getEntitiesData();

            if(data.isEmpty()) runFail = true;
            else {
                String attackedEntity = Objects.requireNonNull(EntityType.getKey(event.getTarget().getType())).toString();
                boolean onlyNegate = true;

                for(String entity : data) {
                    boolean isNegation = entity.startsWith("!");
                    Pattern entityToCheck = isNegation ? Pattern.compile(entity.substring(1), Pattern.CASE_INSENSITIVE) : Pattern.compile(entity, Pattern.CASE_INSENSITIVE);

                    runFail = isNegation != entityToCheck.matcher(attackedEntity).matches();

                    if(!isNegation) onlyNegate = false;
                    if(runFail) break; //Failure reached
                }

                if(!runFail && onlyNegate) runFail = true;
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onUse(LivingEntityUseItemEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean()) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onUse");
        if(perms.isEmpty()) return; //No permission to protect against it

        boolean runFail = false;
        for(Permission perm : perms) {
            if(group.get().canDo(perm.getId())) continue;
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            List<String> data = perm.getData().getItemsData();

            if(data.isEmpty()) runFail = true;
            else {
                String usedItem = Objects.requireNonNull(event.getItem().getItem().getRegistryName()).toString();
                boolean onlyNegate = true;

                for(String item : data) {
                    boolean isNegation = item.startsWith("!");
                    Pattern itemToCheck = isNegation ? Pattern.compile(item.substring(1), Pattern.CASE_INSENSITIVE) : Pattern.compile(item, Pattern.CASE_INSENSITIVE);

                    runFail = isNegation != itemToCheck.matcher(usedItem).matches();

                    if(!isNegation) onlyNegate = false;
                    if(runFail) break; //Failure reached
                }

                if(!runFail && onlyNegate) runFail = true;
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(EntityTravelToDimensionEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(PermissionManager.verifyEntity(event.getEntity(), standingOn).asBoolean() &&
                PermissionManager.verifyNetherEntity(event.getEntity(), standingOn, event.getEntity().getOnPos()).asBoolean()) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onPlayerChangedDimension");
        if(perms.isEmpty()) return; //No permission to protect against it

        ResourceKey<Level> toDim = event.getDimension();

        boolean runFail = false;
        for(Permission perm : perms) {
            if (group.get().canDo(perm.getId())) continue;
            if (runFail) break; //Break loop if we determine failure
            boolean onlyNegate = true;

            // Get permission data
            List<String> data = perm.getData().getSkyblockaddonData().stream()
                    .filter(s -> s.startsWith("dimension:"))
                    .map( s -> s.replace("dimension:", ""))
                    .collect(Collectors.toCollection(ArrayList::new));

            if(data.isEmpty()) runFail = true;
            else {
                for(String dimension : data) {
                    boolean isNegation = dimension.startsWith("!");
                    Pattern dimensionToCheck = isNegation ? Pattern.compile(dimension.substring(1), Pattern.CASE_INSENSITIVE) : Pattern.compile(dimension, Pattern.CASE_INSENSITIVE);

                    runFail = isNegation != dimensionToCheck.matcher(toDim.getRegistryName().toString()).matches();

                    if(!isNegation) onlyNegate = false;
                    if(runFail) break;
                }

                if(!runFail && onlyNegate) runFail = true;
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            if(event.isCancelable()) event.setCanceled(true);
            else event.setResult(Event.Result.DENY);

            ServerPlayer player = (ServerPlayer) event.getEntity();

            // Convert yaw to radians and calculate direction vector
            double radianYaw = Math.toRadians(player.getYRot());
            Vec3 direction = new Vec3(Math.sin(radianYaw), 0, -Math.cos(radianYaw));

            // Invert the direction to get the opposite direction
            Vec3 oppositeDirection = direction.scale(-1);  // Keep the direction with the same magnitude

            // Send the new movement to player & client
            player.setDeltaMovement(new Vec3(direction.x * 0.4, 0.28, direction.z * 0.4));
            player.connection.send(new ClientboundSetEntityMotionPacket(player)); //Send motion Packet
        }
    }
}
