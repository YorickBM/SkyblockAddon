package yorickbm.skyblockaddon.islands;

import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.mutable.MutableInt;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.data.IslandData;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.util.BiomeUtil;
import yorickbm.skyblockaddon.util.NBT.IsUnique;
import yorickbm.skyblockaddon.util.NBT.NBTSerializable;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Island extends IslandData implements IsUnique, NBTSerializable {

    public Island() {
    }
    public Island(final UUID uuid, final Vec3i vec) {
        super.setId(UUID.randomUUID());
        super.setOwner(uuid);
        super.setSpawn(vec);
        super.setCenter(vec);
        super.setVisibility(false);

        genBasicGroups();
    }

    public void genBasicGroups() {
        //Add default members group
        final ItemStack item = new ItemStack(Items.RED_MUSHROOM);
        item.setHoverName(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("gui.group.default.name")).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
        final ItemStack item2 = new ItemStack(Items.BROWN_MUSHROOM);
        item2.setHoverName(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("gui.group.nonmember.name")).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));


        super.addGroup(new IslandGroup(SkyblockAddon.MOD_UUID, item, true));
        super.addGroup(new IslandGroup(SkyblockAddon.MOD_UUID2, item2, false));
    }

    /**
     * Teleport entity to spawn location of island.
     *
     * @param entity - Entity to teleport
     */
    public void teleportTo(final Entity entity) {
        entity.teleportTo(getSpawn().getX(), getSpawn().getY() + 0.5, getSpawn().getZ());

        if (entity instanceof final ServerPlayer player) {
            ServerHelper.playSongToPlayer(player, SoundEvents.ENDERMAN_TELEPORT, SkyblockAddon.EFFECT_SOUND_VOL, 1f);
        }
    }

    /**
     * Kick entity from island
     *
     * @param source - Who is kicking the entity
     * @param entity - UUID of entity to remove
     */
    public void kickMember(final Entity source, final UUID entity) {
        if(isOwner(entity)) {
            if(!getMembers().isEmpty()) super.setOwner(getMembers().get(0));
            else {
                super.setOwner(SkyblockAddon.MOD_UUID);
                super.setVisibility(false); //Close island if we are the last one
            }
        } else {
            final Optional<IslandGroup> group = getGroupForEntityUUID(entity);
            group.ifPresentOrElse(
                    g -> super.removeMember(entity, g.getId()),
                    () -> super.removeMember(entity, SkyblockAddon.MOD_UUID)
            );
        }

        source.getServer().getLevel(Level.OVERWORLD).getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            cap.clearCacheForPlayer(entity);
        });

        //Teleport entity to world spawn
        final BlockPos worldSpawn = Objects.requireNonNull(Objects.requireNonNull(source.getServer()).getLevel(Level.OVERWORLD)).getSharedSpawnPos();
        final ServerPlayer player = source.getServer().getPlayerList().getPlayer(entity); //Get entity from online player list
        if(player != null //Check if we found entity as an online player
                && player.getLevel().dimension() == Level.OVERWORLD //Check player is found
                && getIslandBoundingBox().isInside(player.getOnPos()) //Determine if player is on the island
        ) {
            player.teleportTo(worldSpawn.getX(), worldSpawn.getY(), worldSpawn.getZ()); //Teleport player
        }
    }

    /**
     * Add entity as member to island
     *
     * @param source - Who is adding the entity
     * @param entity - UUID of entity to add
     */
    public boolean addMember(final Entity source, final UUID entity) {
        if(!super.addMember(entity, SkyblockAddon.MOD_UUID)) return false;

        //Teleport entity to island
        final ServerPlayer player = Objects.requireNonNull(source.getServer()).getPlayerList().getPlayer(entity); //Get entity from online player list
        if(player != null) this.teleportTo(player);

        return true;
    }

    /**
     * Alter islands spawn point used for TeleportTo
     * @param point - New spawn point location
     */
    public void setSpawnPoint(final Vec3i point) {
        super.setSpawn(point.offset(0, 0.5, 0)); //Set it 0.5 blocks higher.
    }

    /**
     * Toggle the islands visibility from public/private
     */
    public void toggleVisibility() {
        setVisibility(!isVisible()); //Set it to inverse of its current.
    }

    /**
     * Set the biome for all loaded chunks of island
     * @param biome - Biome resource location
     * @param serverlevel - Over-world of server
     */
    public void updateBiome(final String biome, final ServerLevel serverlevel) {
        setBiome(biome.split(":")[1]); //Set biome for configuration to part without mod id

        final BoundingBox boundingbox = getIslandBoundingBox();
        final MutableInt mutableint = new MutableInt(0);
        final Holder<Biome> holder = serverlevel.registryAccess()
                .registryOrThrow(Registry.BIOME_REGISTRY)
                .getOrCreateHolder(ResourceKey.create(ForgeRegistries.BIOMES.getRegistryKey(), new ResourceLocation(biome)));

        for(int j = SectionPos.blockToSectionCoord(boundingbox.minZ()); j <= SectionPos.blockToSectionCoord(boundingbox.maxZ()); ++j) {
            for(int k = SectionPos.blockToSectionCoord(boundingbox.minX()); k <= SectionPos.blockToSectionCoord(boundingbox.maxX()); ++k) {
                final ChunkAccess chunkaccess = serverlevel.getChunk(k, j, ChunkStatus.FULL, false);
                if (chunkaccess == null) {
                    continue; //Skip unloaded chunks
                }
                chunkaccess.fillBiomesFromNoise(
                        BiomeUtil.makeResolver(mutableint, chunkaccess, boundingbox, holder, (p_262543_) -> true),
                        serverlevel.getChunkSource().getGenerator().climateSampler());
                BiomeUtil.updateChunk(serverlevel.getChunk(k, j), serverlevel);
            }
        }
    }

    /**
     * Determine if entity is part of the island.
     *
     * @param player - Entity whom to check
     * @return - Boolean
     */
    public boolean isPartOf(final UUID player) {
        return isOwner(player) || super.getMembers().contains(player);
    }

    /**
     * Determine if entity is owner of the island.
     *
     * @param uuid - Entity whom to check
     * @return - Boolean
     */
    public boolean isOwner(final UUID uuid) {
        return super.getOwner().equals(uuid);
    }

    /**
     * Save data into NBT.
     */
    @Override
    public CompoundTag serializeNBT() {
        final CompoundTag tag = super.serializeNBT();
        return tag;
    }

    /**
     * Load data from NBT.
     */
    @Override
    public void deserializeNBT(final CompoundTag nbt) {
        super.deserializeNBT(nbt);
    }
}
