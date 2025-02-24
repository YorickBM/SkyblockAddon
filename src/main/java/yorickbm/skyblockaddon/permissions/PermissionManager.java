package yorickbm.skyblockaddon.permissions;

import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.SkyblockAddonWorldCapability;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.permissions.json.PermissionHolder;
import yorickbm.skyblockaddon.permissions.util.EntityVerification;
import yorickbm.skyblockaddon.permissions.util.Permission;
import yorickbm.skyblockaddon.util.JSON.JSONEncoder;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PermissionManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private List<Permission> permissions;

    private static final PermissionManager instance = new PermissionManager();
    public static PermissionManager getInstance() {
        return instance;
    }
    public PermissionManager() {
        this.permissions = new ArrayList<>();
    }

    public void loadPermissions() {
        try {
            permissions = JSONEncoder.loadFromFile(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/registries/PermissionRegistry.json"), PermissionHolder.class).permissions;
            LOGGER.info("Loaded {} island permission(s).", permissions.size());
        } catch (final Exception ex) {
            LOGGER.error("Failed to load permission configuration.");
        }
    }

    public List<Permission> getPermissions() {
        return this.permissions;
    }
    public List<Permission> getPermissionsFor(final String category) {
        return this.permissions.stream().filter(pm -> pm.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
    }

    public List<Permission> getPermissionsForTrigger(final String trigger) {
        return this.permissions.stream().filter(pm -> pm.hasTrigger(trigger)).collect(Collectors.toList());
    }

    /**
     * Run requirement check for entity. This check determines if it may override on the event.
     * Or if a permission check should be performed.
     *
     * @param entity - Triggering entity
     * @param standingOn - AtomicReference to get Island entity is standing on.
     * @return - True if entity may override permission check.
     */
    public static EntityVerification verifyEntity(final Entity entity, final AtomicReference<Island> standingOn) {
        if(!(entity instanceof final ServerPlayer player) || entity instanceof FakePlayer) return EntityVerification.NOT_A_PLAYER; //Allowed types
        if(player.getLevel().dimension() != Level.OVERWORLD) return EntityVerification.NOT_IN_OVERWORLD; //Is not in over-world
        if(player.hasPermissions(Commands.LEVEL_ADMINS)) return EntityVerification.IS_ADMIN; //Player is admin;

        final Optional<SkyblockAddonWorldCapability> cap = player.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).resolve();
        if(cap.isEmpty()) return EntityVerification.CAP_NOT_FOUND; //Could not find Capability

        final Island island = cap.get().getIslandPlayerIsStandingOn(player);
        if(island == null) return EntityVerification.NOT_ON_ISLAND; //Not within protection

        standingOn.set(island); //Set atomic reference
        return island.isOwner(player.getUUID()) ? EntityVerification.IS_ISLAND_OWNER : EntityVerification.IS_ISLAND_MEMBER; //Owners may do anything
    }

    /**
     * Run requirement check for entity. This check determines if it may override on the event.
     * Or if a permission check should be performed.
     *
     * @param entity - Triggering entity
     * @param standingOn - AtomicReference to get Island entity is standing on.
     * @return - True if entity may override permission check.
     */
    public static EntityVerification verifyNetherEntity(final Entity entity, final AtomicReference<Island> standingOn, final BlockPos triggerPoint) {
        if(!(entity instanceof final ServerPlayer player) || entity instanceof FakePlayer) return EntityVerification.NOT_A_PLAYER; //Allowed types
        if(player.getLevel().dimension() != Level.NETHER) return EntityVerification.NOT_IN_NETHER; //Is not in nether
        if(player.hasPermissions(Commands.LEVEL_ADMINS)) return EntityVerification.IS_ADMIN; //Player is admin;

        final Optional<SkyblockAddonWorldCapability> cap = Objects.requireNonNull(Objects.requireNonNull(player.getServer()).getLevel(Level.OVERWORLD)).getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).resolve();
        if(cap.isEmpty()) return EntityVerification.CAP_NOT_FOUND; //Could not find Capability


        final Island island = cap.get().getIslandByPos(new BlockPos(triggerPoint.getX() * 8, triggerPoint.getY(), triggerPoint.getZ() * 8));
        if(island == null) return EntityVerification.NOT_ON_ISLAND; //Not within protection

        standingOn.set(island); //Set atomic reference
        return island.isOwner(player.getUUID()) ? EntityVerification.IS_ISLAND_OWNER : EntityVerification.IS_ISLAND_MEMBER; //Owners may do anything
    }

    /**
     * Run player interaction handler to determine if event is to be canceld or not.
     *
     * @param standingOn - Island player is standing on
     * @param trigger - Trigger type to use
     */
    public static boolean checkPlayerInteraction(final AtomicReference<Island> standingOn, final ServerPlayer player, final ServerLevel world, final BlockPos position, final ItemStack handItem, final String trigger) {
        //Update doors
        if (player.getLevel().getBlockState(position).getBlock() instanceof DoorBlock) {
            final DoubleBlockHalf half = (player).getLevel().getBlockState(position).getValue(DoorBlock.HALF);
            if (half == DoubleBlockHalf.LOWER) {
                final BlockState other = (player).getLevel().getBlockState(position.above());
                ServerHelper.SendPacket(player, new ClientboundBlockUpdatePacket(position.above(), other));
            } else {
                final BlockState other = (player).getLevel().getBlockState(position.below());
                ServerHelper.SendPacket(player, new ClientboundBlockUpdatePacket(position.below(), other));
            }
        }

        final Optional<IslandGroup> group = standingOn.get().getGroupForEntity(player);
        if(group.isEmpty()) {
            return true;
        }

        final List<Permission> perms = PermissionManager.getInstance().getPermissionsForTrigger(trigger);
        if(perms.isEmpty()) return false; //No permission to protect against it

        boolean runFail = false;
        for(final Permission perm : perms) {
            if(group.get().canDo(perm.getId())) continue;
            if(runFail) break; //Break loop if we determine failure

            // Get permission item data and check for empty
            final List<String> itemsData = perm.getData().getItemsData();
            final List<String> blocksData = perm.getData().getBlocksData();

            // Determine default
            if(itemsData.isEmpty() && blocksData.isEmpty() && !group.get().canDo(perm.getId())) {
                runFail = true;
                break;
            }

            boolean itemAllowed = true;
            boolean blockAllowed = true;

            if(!handItem.isEmpty() && !itemsData.isEmpty()) {
                final String item = Objects.requireNonNull(handItem.getItem().getRegistryName()).toString();
                boolean onlyNegate = true;

                for(final String itemInData : itemsData) {
                    final boolean isNegation = itemInData.startsWith("!");
                    final Pattern itemToCheck = isNegation ? Pattern.compile(itemInData.substring(1), Pattern.CASE_INSENSITIVE) : Pattern.compile(itemInData, Pattern.CASE_INSENSITIVE);

                    itemAllowed = isNegation == itemToCheck.matcher(item).matches();

                    if(!isNegation) onlyNegate = false;
                    if(!itemAllowed) break; //Failure reached
                }

                if(itemAllowed && onlyNegate) itemAllowed = false;
            }

            final BlockState clickedState = world.getBlockState(position);
            if(!clickedState.isAir() && !blocksData.isEmpty()) {
                final Block clickedBlock = clickedState.getBlock();
                final String block = Objects.requireNonNull(clickedBlock.getRegistryName()).toString();
                boolean onlyNegate = true;

                for(final String blockInData : blocksData) {
                    final boolean isNegation = blockInData.startsWith("!");
                    final Pattern blockToCheck = isNegation ? Pattern.compile(blockInData.substring(1), Pattern.CASE_INSENSITIVE) : Pattern.compile(blockInData, Pattern.CASE_INSENSITIVE);

                    blockAllowed = isNegation == blockToCheck.matcher(block).matches();

                    if(!isNegation) onlyNegate = false;
                    if(!blockAllowed) break; //Failure reached
                }

                if(blockAllowed && onlyNegate) blockAllowed = false;
            }

            runFail = (!blockAllowed || !itemAllowed);
        }

        return runFail;
    }
}
