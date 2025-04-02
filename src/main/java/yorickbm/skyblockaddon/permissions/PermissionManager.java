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
            LOGGER.error("Failed to load permission configuration.", ex);
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
     * Run filter logic
     */
    public static MatchResult checkMatch(final List<String> rules, final String item) {
        boolean foundNonNegatedMatch = false;
        boolean onlyNegations = true; // Track if all rules are negated

        for (final String rule : rules) {
            final boolean isNegation = rule.startsWith("!");
            final String patternString = isNegation ? rule.substring(1) : rule;
            final Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);

            if (pattern.matcher(item).matches()) {
                if (isNegation) {
                    return MatchResult.SKIP; // If a negated rule matches, return SKIP immediately
                } else {
                    foundNonNegatedMatch = true; // Found a non-negated match
                }
            }

            if (!isNegation) {
                onlyNegations = false; // Found a non-negated rule, so it's not "only negations"
            }
        }

        // If no non-negated matches were found, and all rules were negations, return BLOCK
        return foundNonNegatedMatch ? MatchResult.BLOCK : (onlyNegations ? MatchResult.BLOCK : MatchResult.SKIP);
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
        record Verifier(Entity entity, AtomicReference<Island> standingOn) {
            EntityVerification verify() {
                if (!(entity instanceof final ServerPlayer player) || entity instanceof FakePlayer)
                    return EntityVerification.NOT_A_PLAYER;
                if (player.getLevel().dimension() != Level.OVERWORLD)
                    return EntityVerification.NOT_IN_OVERWORLD;
                if (player.hasPermissions(Commands.LEVEL_ADMINS))
                    return EntityVerification.IS_ADMIN;

                final Optional<SkyblockAddonWorldCapability> cap =
                        player.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).resolve();
                if (cap.isEmpty())
                    return EntityVerification.CAP_NOT_FOUND;

                final Island island = cap.get().getIslandPlayerIsStandingOn(player);
                if (island == null)
                    return EntityVerification.NOT_ON_ISLAND;

                standingOn.set(island);
                return island.isOwner(player.getUUID()) ? EntityVerification.IS_ISLAND_OWNER : EntityVerification.IS_ISLAND_MEMBER;
            }
        }

        final EntityVerification result = new Verifier(entity, standingOn).verify();
        return result;
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
            if(group.get().canDo(perm.getId())) {
                LOGGER.info("action is allowed for " + perm.getId() + " in group " + group.get().getItem().getDisplayName().getString().trim());
                continue;
            }
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

                MatchResult rslt = PermissionManager.checkMatch(itemsData, item);
                LOGGER.info(item + " is " + rslt + " on " + perm.getId() + " in group " + group.get().getItem().getDisplayName().getString().trim());
                switch(rslt) {
                    case SKIP, ALLOW-> { }
                    case BLOCK ->  itemAllowed = false;
                }
            }

            final BlockState clickedState = world.getBlockState(position);
            if(!clickedState.isAir() && !blocksData.isEmpty()) {
                final Block clickedBlock = clickedState.getBlock();
                final String block = Objects.requireNonNull(clickedBlock.getRegistryName()).toString();

                MatchResult rslt = PermissionManager.checkMatch(blocksData, block);
                LOGGER.info(block + " is " + rslt + " on " + perm.getId() + " in group " + group.get().getItem().getDisplayName().getString().trim());
                switch(rslt) {
                    case SKIP, ALLOW-> { }
                    case BLOCK ->  blockAllowed = false;
                }
            }

            runFail = (!blockAllowed || !itemAllowed);
        }
        return runFail;
    }
}
