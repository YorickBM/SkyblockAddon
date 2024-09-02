package yorickbm.skyblockaddon.islands;

import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.data.IslandData;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.util.BiomeUtil;
import yorickbm.skyblockaddon.util.NBT.IsUnique;
import yorickbm.skyblockaddon.util.NBT.NBTSerializable;
import yorickbm.skyblockaddon.util.ServerHelper;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.Objects;
import java.util.UUID;

public class Island extends IslandData implements IsUnique, NBTSerializable {
    private static final Logger LOGGER = LogManager.getLogger();
    CompoundTag legacyDataOnlyHereWhileTesting;

    public Island() {
    }

    public Island(UUID uuid, Vec3i vec) {
        super.setId(UUID.randomUUID());
        super.setOwner(uuid);
        super.setSpawn(vec);
        super.setCenter(vec);
        super.setVisibility(false);

        //Add default members group
        ItemStack item = new ItemStack(Items.RED_MUSHROOM);
        item.setHoverName(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("gui.group.default.name")).withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.BLUE));
        super.addGroup(new IslandGroup(SkyblockAddon.MOD_UUID, item, true));
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
     * @param source - Who is kicking the entity
     * @param entity - UUID of entity to remove
     */
    public boolean kickMember(Entity source, UUID entity) {
        if(isOwner(entity)) {
            if(!getMembers().isEmpty()) super.setOwner(getMembers().get(0));
            else {
                super.setOwner(SkyblockAddon.MOD_UUID);
                super.setVisibility(false); //Close island if we are the last one
            }
        } else {
            super.removeMember(entity, SkyblockAddon.MOD_UUID);
        }

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
     * Add entity as member to island
     *
     * @param source - Who is adding the entity
     * @param entity - UUID of entity to add
     */
    public boolean addMember(Entity source, UUID entity) {
        super.addMember(entity, SkyblockAddon.MOD_UUID);

        //Teleport entity to island
        ServerPlayer player = Objects.requireNonNull(source.getServer()).getPlayerList().getPlayer(entity); //Get entity from online player list
        if(player != null) this.teleportTo(player);

        return true;
    }

    /**
     * Alter islands spawn point used for TeleportTo
     * @param point - New spawn point location
     */
    public void setSpawnPoint(Vec3i point) {
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
    public void updateBiome(String biome, ServerLevel serverlevel) {
        setBiome(biome.split(":")[1]); //Set biome for configuration to part without mod id

        BoundingBox boundingbox = getIslandBoundingBox();
        MutableInt mutableint = new MutableInt(0);
        Holder<Biome> holder = serverlevel.registryAccess()
                .registryOrThrow(Registry.BIOME_REGISTRY)
                .getOrCreateHolder(ResourceKey.create(ForgeRegistries.BIOMES.getRegistryKey(), new ResourceLocation(biome)));

        for(int j = SectionPos.blockToSectionCoord(boundingbox.minZ()); j <= SectionPos.blockToSectionCoord(boundingbox.maxZ()); ++j) {
            for(int k = SectionPos.blockToSectionCoord(boundingbox.minX()); k <= SectionPos.blockToSectionCoord(boundingbox.maxX()); ++k) {
                ChunkAccess chunkaccess = serverlevel.getChunk(k, j, ChunkStatus.FULL, false);
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
    public boolean isPartOf(UUID player) {
        return isOwner(player) || super.getMembers().contains(player);
    }

    /**
     * Determine if entity is owner of the island.
     *
     * @param uuid - Entity whom to check
     * @return - Boolean
     */
    public boolean isOwner(UUID uuid) {
        return super.getOwner().equals(uuid);
    }

    /**
     * Save data into NBT.
     */
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        if(legacyDataOnlyHereWhileTesting != null) tag.put("permissions", legacyDataOnlyHereWhileTesting);
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
    public @NotNull Component parseTextComponent(@NotNull Component original) {
        return new TextComponent(original.getString()
                .replace("%owner%", UsernameCache.getBlocking(getOwner()))
                .replace("%x%", getSpawn().getX()+"")
                .replace("%y%", getSpawn().getY()+"")
                .replace("%z%", getSpawn().getZ()+"")
                .replace("%biome%", getBiome())
                .replace("%visibility%", this.isVisible() ? SkyBlockAddonLanguage.getLocalizedString("island.public") : SkyBlockAddonLanguage.getLocalizedString("island.private"))
        ).withStyle(original.getStyle());
    }
}
