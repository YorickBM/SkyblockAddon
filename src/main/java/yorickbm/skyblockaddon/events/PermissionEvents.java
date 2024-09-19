package yorickbm.skyblockaddon.events;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.regex.Pattern;

@Mod.EventBusSubscriber(modid = SkyblockAddon.MOD_ID)
public class PermissionEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void onTrample(BlockEvent.FarmlandTrampleEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;

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
            List<String> data = perm.getData().getItemsData();

            if(data.isEmpty()) runFail = !group.get().canDo(perm.getId());
            else {
                String pickedupItem = Objects.requireNonNull(event.getItem().getItem().getItem().getRegistryName()).toString();
                boolean onlyNegate = true;

                for(String item : data) {
                    boolean isNegation = item.startsWith("!");
                    String itemToCheck = isNegation ? item.substring(1) : item;

                    if(pickedupItem.equalsIgnoreCase(itemToCheck)) {
                        runFail = !isNegation;
                    }

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
            List<String> data = perm.getData().getItemsData();

            if(data.isEmpty()) runFail = !group.get().canDo(perm.getId());
            else {
                String droppedItem = Objects.requireNonNull(event.getEntityItem().getItem().getItem().getRegistryName()).toString();
                boolean onlyNegate = true;

                for(String item : data) {
                    boolean isNegation = item.startsWith("!");
                    String itemToCheck = isNegation ? item.substring(1) : item;

                    if(droppedItem.equalsIgnoreCase(itemToCheck)) {
                        runFail = !isNegation;
                    }

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
            List<String> data = perm.getData().getItemsData();

            if(data.isEmpty()) runFail = !group.get().canDo(perm.getId());
            else {
                String filledBucket = Objects.requireNonNull(event.getFilledBucket().getItem().getRegistryName()).toString();
                boolean onlyNegate = true;

                for(String fluid : data) {
                    boolean isNegation = fluid.startsWith("!");
                    String fluidToCheck = isNegation ? fluid.substring(1) : fluid;

                    if(filledBucket.equalsIgnoreCase(fluidToCheck)) {
                        runFail = !isNegation;
                    }

                    if(!isNegation) onlyNegate = false;
                    if(runFail) break; //Failure reached
                }

                if(!runFail && onlyNegate) runFail = true;
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onMount(EntityMountEvent event) {
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;

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
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            List<String> data = perm.getData().getEntitiesData();

            if(data.isEmpty()) runFail = !group.get().canDo(perm.getId());
            else {
                String mountedEntity = Objects.requireNonNull(EntityType.getKey(event.getEntityBeingMounted().getType())).toString();
                boolean onlyNegate = true;

                for(String entity : data) {
                    boolean isNegation = entity.startsWith("!");
                    String entityToCheck = isNegation ? entity.substring(1) : entity;

                    if(mountedEntity.equalsIgnoreCase(entityToCheck)) {
                        runFail = !isNegation;
                    }

                    if(!isNegation) onlyNegate = false;
                    if(runFail) break; //Failure reached
                }

                if(!runFail && onlyNegate) runFail = true;
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        LOGGER.info("onRightClickBlock");
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            event.setCanceled(true);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onRightClickBlock");
        if(perms.isEmpty()) return; //No permission to protect against it

        boolean runFail = false;
        for(Permission perm : perms) {
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            List<String> itemsData = perm.getData().getItemsData();
            List<String> blocksData = perm.getData().getBlocksData();

            // Determine default
            if(itemsData.isEmpty() && blocksData.isEmpty() && !group.get().canDo(perm.getId())) {
                runFail = true;
                break;
            }

            boolean itemAllowed = true;
            boolean blockAllowed = true;

            ItemStack handItem = event.getItemStack();
            if(!handItem.isEmpty() && !itemsData.isEmpty()) {
                String item = Objects.requireNonNull(handItem.getItem().getRegistryName()).toString();
                boolean onlyNegate = true;

                for(String itemInData : itemsData) {
                    boolean isNegation = itemInData.startsWith("!");
                    Pattern itemToCheck = isNegation ? Pattern.compile(itemInData.substring(1).replace("*", ".*"), Pattern.CASE_INSENSITIVE) : Pattern.compile(itemInData.replace("*", ".*"), Pattern.CASE_INSENSITIVE);

                    if(itemToCheck.matcher(item).matches()) {
                        itemAllowed = isNegation;
                    }

                    if(!isNegation) onlyNegate = false;
                    if(!itemAllowed) break; //Failure reached
                }

                if(itemAllowed && onlyNegate) itemAllowed = false;
            }

            BlockState clickedState = event.getWorld().getBlockState(event.getPos());
            if(!clickedState.isAir() && !blocksData.isEmpty()) {
                Block clickedBlock = clickedState.getBlock();
                String block = Objects.requireNonNull(clickedBlock.getRegistryName()).toString();
                boolean onlyNegate = true;

                for(String blockInData : blocksData) {
                    boolean isNegation = blockInData.startsWith("!");
                    Pattern blockToCheck = isNegation ? Pattern.compile(blockInData.substring(1).replace("*", ".*"), Pattern.CASE_INSENSITIVE) : Pattern.compile(blockInData.replace("*", ".*"), Pattern.CASE_INSENSITIVE);

                    if(blockToCheck.matcher(block).matches()) {
                        blockAllowed = isNegation;
                    }

                    if(!isNegation) onlyNegate = false;
                    if(!blockAllowed) break; //Failure reached
                }

                if(blockAllowed && onlyNegate) blockAllowed = false;
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        LOGGER.info("onRightClickEmpty");
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        LOGGER.info("onLeftClickBlock");
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            event.setCanceled(true);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onLeftClickBlock");
        if(perms.isEmpty()) return; //No permission to protect against it

        boolean runFail = false;
        for(Permission perm : perms) {
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            List<String> itemsData = perm.getData().getItemsData();
            List<String> blocksData = perm.getData().getBlocksData();

            // Determine default
            if(itemsData.isEmpty() && blocksData.isEmpty() && !group.get().canDo(perm.getId())) {
                runFail = true;
                break;
            }

            boolean itemAllowed = true;
            boolean blockAllowed = true;

            ItemStack handItem = event.getItemStack();
            if(!handItem.isEmpty() && !itemsData.isEmpty()) {
                String item = Objects.requireNonNull(handItem.getItem().getRegistryName()).toString();
                boolean onlyNegate = true;

                for(String itemInData : itemsData) {
                    boolean isNegation = itemInData.startsWith("!");
                    String itemToCheck = isNegation ? itemInData.substring(1) : itemInData;

                    if(itemToCheck.equalsIgnoreCase(item)) {
                        itemAllowed = isNegation;
                    }

                    if(!isNegation) onlyNegate = false;
                    if(!itemAllowed) break; //Failure reached
                }

                if(itemAllowed && onlyNegate) itemAllowed = false;
            }

            BlockState clickedState = event.getWorld().getBlockState(event.getPos());
            if(!clickedState.isAir() && !blocksData.isEmpty()) {
                Block clickedBlock = clickedState.getBlock();
                String block = Objects.requireNonNull(clickedBlock.getRegistryName()).toString();
                boolean onlyNegate = true;

                for(String blockInData : blocksData) {
                    boolean isNegation = blockInData.startsWith("!");
                    String blockToCheck = isNegation ? blockInData.substring(1) : blockInData;

                    if(blockToCheck.equalsIgnoreCase(block)) {
                        blockAllowed = isNegation;
                    }

                    if(!isNegation) onlyNegate = false;
                    if(!blockAllowed) break; //Failure reached
                }

                if(blockAllowed && onlyNegate) blockAllowed = false;
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        LOGGER.info("onLeftClickEmpty");
        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        LOGGER.info("onAttack");

        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            event.setCanceled(true);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onAttack");
        if(perms.isEmpty()) return; //No permission to protect against it

        boolean runFail = false;
        for(Permission perm : perms) {
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            List<String> data = perm.getData().getEntitiesData();

            if(data.isEmpty()) runFail = !group.get().canDo(perm.getId());
            else {
                String attackTarget = Objects.requireNonNull(EntityType.getKey(event.getTarget().getType())).toString();
                boolean onlyNegate = true;

                for(String entity : data) {
                    boolean isNegation = entity.startsWith("!");
                    String entityToCheck = isNegation ? entity.substring(1) : entity;

                    if(attackTarget.equalsIgnoreCase(entityToCheck)) {
                        runFail = !isNegation;
                    }

                    if(!isNegation) onlyNegate = false;
                    if(runFail) break; //Failure reached
                }

                if(!runFail && onlyNegate) runFail = true;
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onUse(LivingEntityUseItemEvent event) {
        LOGGER.info("onUse");

        AtomicReference<Island> standingOn = new AtomicReference<>();
        if(verifyEntity(event.getEntity(), standingOn)) return;

        Optional<IslandGroup> group = standingOn.get().getGroupForEntity(event.getEntity());
        if(group.isEmpty()) {
            event.setCanceled(true);
            return; //Not part of any group so not allowed!!
        }

        List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger("onUse");
        if(perms.isEmpty()) return; //No permission to protect against it

        boolean runFail = false;
        for(Permission perm : perms) {
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            List<String> data = perm.getData().getItemsData();

            if(data.isEmpty()) runFail = !group.get().canDo(perm.getId());
            else {
                String usedItem = Objects.requireNonNull(event.getItem().getItem().getRegistryName()).toString();
                boolean onlyNegate = true;

                for(String item : data) {
                    boolean isNegation = item.startsWith("!");
                    String itemToCheck = isNegation ? item.substring(1) : item;

                    if(usedItem.equalsIgnoreCase(itemToCheck)) {
                        runFail = !isNegation;
                    }

                    if(!isNegation) onlyNegate = false;
                    if(runFail) break; //Failure reached
                }

                if(!runFail && onlyNegate) runFail = true;
            }
        }

        if(runFail) {
            ((ServerPlayer) event.getEntity()).displayClientMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("toolbar.overlay.nothere")).withStyle(ChatFormatting.DARK_RED), true);
            event.setCanceled(true);
        }
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
