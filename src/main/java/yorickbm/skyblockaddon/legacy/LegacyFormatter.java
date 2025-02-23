package yorickbm.skyblockaddon.legacy;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.util.NBT.NBTEncoder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LegacyFormatter {
    private static final Logger LOGGER = LogManager.getLogger();

    public static int formatBeta(final Path filePath) {
        final List<Island> legacyIslands = new ArrayList<>();

        for(final CompoundTag tag : NBTEncoder.loadNBTFromFolder(filePath)) {
            final Island island = new Island();
            island.deserializeNBT(tag);
            island.genBasicGroups();

            if(tag.contains("permissions")) {
                final CompoundTag permissions = tag.getCompound("permissions");
                final CompoundTag adminGroup = permissions.getCompound("Admin");
                final CompoundTag membersGroup = permissions.getCompound("Members");

                for(final CompoundTag group : new CompoundTag[]{adminGroup, membersGroup}) {
                    final CompoundTag members = group.getCompound("members");
                    for(int i = 0; i < members.getInt("count"); i++) {
                        final UUID member = members.getUUID("member-" + i);
                        if(!island.addMember(member, SkyblockAddon.MOD_UUID)) {
                            LOGGER.warn("Failure to added {} as a member to {}", member.toString(), island.getId().toString());
                        }
                    }
                }

            }

            legacyIslands.add(island);
        }

        NBTEncoder.saveToFile(legacyIslands, filePath);
        return legacyIslands.size();
    }

    public static int formatLegacy(final CompoundTag nbt, final Path filePath) {
        final List<Island> legacyIslands = new ArrayList<>();

        final CompoundTag tagIslandIds = nbt.getCompound("islandIds");
        final CompoundTag tagIslands = nbt.getCompound("islands");

        for(int i = 0; i < tagIslandIds.getInt("count"); ++i) {
            final String islandId = tagIslandIds.getString(String.valueOf(i));

            final CompoundTag islandTag = tagIslands.getCompound(islandId);
            final CompoundTag centerTag = islandTag.contains("center") ? islandTag.getCompound("center") : islandTag.getCompound("spawn");

            final Vec3i center = new Vec3i(centerTag.getInt("x"), centerTag.getInt("y"), centerTag.getInt("z"));
            final Island island = new Island(UUID.fromString(islandId), center);

            if(islandTag.contains("biome")) island.setBiome(islandTag.getString("biome"));
            if(islandTag.contains("travelability")) island.setVisibility(islandTag.getBoolean("travelability"));
            if(islandTag.contains("owner")) island.setOwner(UUID.fromString(islandTag.getString("owner")));

            if(islandTag.contains("permissions")) {
                final CompoundTag permissions = islandTag.getCompound("permissions");
                final CompoundTag adminGroup = permissions.getCompound("Admin");
                final CompoundTag membersGroup = permissions.getCompound("Members");

                for(final CompoundTag group : new CompoundTag[]{adminGroup, membersGroup}) {
                    final CompoundTag members = group.getCompound("members");
                    for(int z = 0; z < members.getInt("count"); z++) {
                        final UUID member = members.getUUID("member-" + z);
                        if(!island.addMember(member, SkyblockAddon.MOD_UUID)) {
                            LOGGER.warn("Failure to added {} as a member to {}", member.toString(), islandId);
                        }
                    }
                }

            }

            legacyIslands.add(island);
        }
        NBTEncoder.saveToFile(legacyIslands, filePath);
        return legacyIslands.size();
    }
}
