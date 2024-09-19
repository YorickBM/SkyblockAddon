package yorickbm.skyblockaddon.events;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.SkyblockAddonWorldCapability;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.permissions.PermissionManager;
import yorickbm.skyblockaddon.permissions.util.Permission;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber(modid = SkyblockAddon.MOD_ID)
public class PermissionEvents {

    @SubscribeEvent
    public void onEnderPearl(EntityTeleportEvent.EnderPearl event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;

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
        if(verifyEntity(event.getEntity(), standingOn)) return;

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
        if(verifyEntity(event.getEntity(), standingOn)) return;

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
        if(verifyEntity(event.getEntity(), standingOn)) return;

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
        if(verifyEntity(event.getEntity(), standingOn)) return;

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
        if(verifyEntity(event.getEntity(), standingOn)) return;

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
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            List<String> itemsData = perm.getData().getItemsData();

            if(itemsData.isEmpty()) runFail = !group.get().canDo(perm.getId());
            else {
                String pickupItem = Objects.requireNonNull(event.getItem().getItem().getItem().getRegistryName()).toString();
                for(String item : itemsData) {
                    boolean isNegation = item.startsWith("!");
                    String itemToCheck = isNegation ? item.substring(1) : item;

                    runFail = isNegation != pickupItem.equalsIgnoreCase(itemToCheck);
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
    public void onDrop(ItemTossEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;

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
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            List<String> itemsData = perm.getData().getItemsData();

            if(itemsData.isEmpty()) runFail = !group.get().canDo(perm.getId());
            else {
                String pickupItem = Objects.requireNonNull(event.getEntityItem().getItem().getItem().getRegistryName()).toString();
                for(String item : itemsData) {
                    boolean isNegation = item.startsWith("!");
                    String itemToCheck = isNegation ? item.substring(1) : item;

                    runFail = isNegation != pickupItem.equalsIgnoreCase(itemToCheck);
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
    public void onBucket(FillBucketEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            event.setCanceled(true);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onBucket");
        if(perms.isEmpty()) return; //No permission to protect against it

        boolean runFail = false;
        for(Permission perm : perms) {
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            List<String> itemsData = perm.getData().getItemsData();

            if(itemsData.isEmpty()) runFail = !group.get().canDo(perm.getId());
            else {
                String pickupItem = Objects.requireNonNull(event.getFilledBucket().getItem().getRegistryName()).toString();
                for(String item : itemsData) {
                    boolean isNegation = item.startsWith("!");
                    String itemToCheck = isNegation ? item.substring(1) : item;

                    runFail = isNegation != pickupItem.equalsIgnoreCase(itemToCheck);
                }
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }
    @SubscribeEvent
    public void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickEmpty event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }
    @SubscribeEvent
    public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }
    @SubscribeEvent
    public void onLeftClickItem(PlayerInteractEvent.LeftClickEmpty event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }

    @SubscribeEvent
    public void onUse(LivingEntityUseItemEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }

    /**
     * Run requirement check for entity. This check determines if it may override on the event.
     * Or if a permission check should be performed.
     *
     * @param entity - Triggering entity
     * @param standingOn - AtomicReference to get Island entity is standing on.
     * @return - True if entity may override permission check.
     */
    private boolean verifyEntity(Entity entity, AtomicReference<Island> standingOn) {
        if(!(entity instanceof ServerPlayer player) || entity instanceof FakePlayer) return true; //Allowed types
        if(player.getLevel().dimension() != Level.OVERWORLD) return true; //Is not in over-world
        if(player.hasPermissions(3)) return true; //Player is admin;

        Optional<SkyblockAddonWorldCapability> cap = player.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).resolve();
        if(cap.isEmpty()) return true; //Could not find Capability

        Island island = cap.get().getIslandPlayerIsStandingOn(player);
        if(island == null) return true; //Not within protection

        standingOn.set(island); //Set atomic reference
        return island.isOwner(player.getUUID()); //Owners may do anything
    }

}
