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

    public static int format(CompoundTag nbt, Path filePath) {
        List<Island> legacyIslands = new ArrayList<>();

        CompoundTag tagIslandIds = nbt.getCompound("islandIds");
        CompoundTag tagIslands = nbt.getCompound("islands");

        for(int i = 0; i < tagIslandIds.getInt("count"); ++i) {
            String islandId = tagIslandIds.getString(String.valueOf(i));

            CompoundTag islandTag = tagIslands.getCompound(islandId);
            CompoundTag centerTag = islandTag.contains("center") ? islandTag.getCompound("center") : islandTag.getCompound("spawn");
            CompoundTag groupDataTag = islandTag.getCompound("groups");
            CompoundTag permissionTag = islandTag.getCompound("permissions");

            Vec3i center = new Vec3i(centerTag.getInt("x"), centerTag.getInt("y"), centerTag.getInt("z"));
            Island island = new Island(UUID.fromString(islandId), center);

            if(islandTag.contains("biome")) island.setBiome(islandTag.getString("biome"));
            if(islandTag.contains("travelability")) island.setVisibility(islandTag.getBoolean("travelability"));
            if(islandTag.contains("owner")) island.setOwner(UUID.fromString(islandTag.getString("owner")));

            for(int x = 0; x < groupDataTag.getInt("count"); ++x) {
                String groupId = groupDataTag.getString("group-" + x);
                CompoundTag groupTag = permissionTag.getCompound(groupId);
                CompoundTag groupMembersTag = groupTag.getCompound("members");

                for(int z = 0; z < groupMembersTag.getInt("count"); ++z) {
                    if(!island.addMember(groupMembersTag.getUUID("member-" + z), SkyblockAddon.MOD_UUID)) {
                        LOGGER.warn("Failure to added {} as a member to {}", groupMembersTag.getUUID("member-" + z).toString(), islandId);
                    }
                }

            }

            legacyIslands.add(island);
        }

        NBTEncoder.saveToFile(legacyIslands, filePath);
        return legacyIslands.size();
    }

}
