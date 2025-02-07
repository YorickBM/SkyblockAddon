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

    public static int formatBeta(Path filePath) {
        List<Island> legacyIslands = new ArrayList<>();

        for(CompoundTag tag : NBTEncoder.loadNBTFromFolder(filePath)) {
            Island island = new Island();
            island.deserializeNBT(tag);
            island.genBasicGroups();

            if(tag.contains("permissions")) {
                CompoundTag permissions = tag.getCompound("permissions");
                CompoundTag adminGroup = permissions.getCompound("Admin");
                CompoundTag membersGroup = permissions.getCompound("Members");

                for(CompoundTag group : new CompoundTag[]{adminGroup, membersGroup}) {
                    CompoundTag members = group.getCompound("members");
                    for(int i = 0; i < members.getInt("count"); i++) {
                        UUID member = members.getUUID("member-" + i);
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

    public static int formatLegacy(CompoundTag nbt, Path filePath) {
        List<Island> legacyIslands = new ArrayList<>();

        CompoundTag tagIslandIds = nbt.getCompound("islandIds");
        CompoundTag tagIslands = nbt.getCompound("islands");

        for(int i = 0; i < tagIslandIds.getInt("count"); ++i) {
            String islandId = tagIslandIds.getString(String.valueOf(i));

            CompoundTag islandTag = tagIslands.getCompound(islandId);
            CompoundTag centerTag = islandTag.contains("center") ? islandTag.getCompound("center") : islandTag.getCompound("spawn");

            Vec3i center = new Vec3i(centerTag.getInt("x"), centerTag.getInt("y"), centerTag.getInt("z"));
            Island island = new Island(UUID.fromString(islandId), center);

            if(islandTag.contains("biome")) island.setBiome(islandTag.getString("biome"));
            if(islandTag.contains("travelability")) island.setVisibility(islandTag.getBoolean("travelability"));
            if(islandTag.contains("owner")) island.setOwner(UUID.fromString(islandTag.getString("owner")));

            if(islandTag.contains("permissions")) {
                CompoundTag permissions = islandTag.getCompound("permissions");
                CompoundTag adminGroup = permissions.getCompound("Admin");
                CompoundTag membersGroup = permissions.getCompound("Members");

                for(CompoundTag group : new CompoundTag[]{adminGroup, membersGroup}) {
                    CompoundTag members = group.getCompound("members");
                    for(int z = 0; z < members.getInt("count"); z++) {
                        UUID member = members.getUUID("member-" + z);
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
