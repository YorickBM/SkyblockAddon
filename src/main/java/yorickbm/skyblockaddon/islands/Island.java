package yorickbm.skyblockaddon.islands;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.gui.util.GuiContext;
import yorickbm.skyblockaddon.islands.data.IslandData;
import yorickbm.skyblockaddon.util.NBT.IsUnique;
import yorickbm.skyblockaddon.util.NBT.NBTSerializable;
import yorickbm.skyblockaddon.util.ServerHelper;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.Objects;
import java.util.UUID;

public class Island extends IslandData implements IsUnique, NBTSerializable, GuiContext {
    private static final Logger LOGGER = LogManager.getLogger();
    CompoundTag legacyDataOnlyHereWhileTesting;

    public Island() {
    }

    /**
     * Teleport entity to spawn location of island.
     *
     * @param entity - Entity to teleport
     */
    public void teleportTo(Entity entity) {
        entity.teleportTo(getSpawn().getX(), getSpawn().getY() + 0.5, getSpawn().getZ());

        if (entity instanceof ServerPlayer player) {
            ServerHelper.playSongToPlayer(player, SoundEvents.ENDERMAN_TELEPORT, SkyblockAddon.EFFECT_SOUND_VOL, 1f);
        }
    }

    /**
     * Kick entity from island
     *
     * @param source - Whom is kicking the entity
     * @param entity - UUID of entity to remove
     */
    @Override
    public boolean kickMember(Entity source, UUID entity) {
        //TODO: Implement functionality
        LOGGER.warn("Attempted to execute empty function: 'kickMember'");

        //Teleport entity to world spawn
        BlockPos worldSpawn = Objects.requireNonNull(Objects.requireNonNull(source.getServer()).getLevel(Level.OVERWORLD)).getSharedSpawnPos();
        ServerPlayer player = source.getServer().getPlayerList().getPlayer(entity); //Get entity from online player list
        if(player != null //Check if we found entity as an online player
                && player.getLevel().dimension() == Level.OVERWORLD //Check player is found
                && getIslandBoundingBox().isInside(player.getOnPos()) //Determine if player is on the island
        ) {
            player.teleportTo(worldSpawn.getX(), worldSpawn.getY(), worldSpawn.getZ()); //Teleport player
        }
        return true;
    }

    /**
     * Alter islands spawn point used for TeleportTo
     * @param point - New spawn point location
     */
    @Override
    public void setSpawnPoint(Vec3i point) {

    }

    /**
     * Toggle the islands travel ability from public/private
     */
    @Override
    public void toggleTravelability() {

    }

    /**
     * Determine if entity is part of the island.
     *
     * @param player - Entity whom to check
     * @return - Boolean
     */
    public boolean isPartOf(UUID player) {
        return isOwner(player);
    }

    /**
     * Determine if entity is owner of the island.
     *
     * @param uuid - Entity whom to check
     * @return - Boolean
     */
    public boolean isOwner(UUID uuid) {
        return getOwner().equals(uuid);
    }

    /**
     * Save data into NBT.
     */
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.put("permissions", legacyDataOnlyHereWhileTesting);
        return tag;
    }

    /**
     * Load data from NBT.
     */
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        legacyDataOnlyHereWhileTesting = nbt.getCompound("permissions");
    }

    /**
     * Parse Component so variables are replaced with context data.
     *
     * @param original - Original Component containing variables
     * @return - Component where variables are filled with supplied context
     */
    @Override
    public Component parseTextComponent(@NotNull Component original) {
        return new TextComponent(original.getString()
                .replace("%owner%", UsernameCache.getBlocking(getOwner()))
                .replace("%x%", getSpawn().getX()+"")
                .replace("%y%", getSpawn().getY()+"")
                .replace("%z%", getSpawn().getZ()+"")
        ).withStyle(original.getStyle()); //TODO: Parse original.getString()
    }
}
