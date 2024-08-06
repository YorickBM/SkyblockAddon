package yorickbm.skyblockaddon.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.SkyblockAddon;

import java.io.File;
import java.io.IOException;

public class ResourceManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static CompoundTag IslandNBTData = null;

    /**
     * Load custom .NBT file for island structure
     * @param server
     * @return NBT data
     */
    public static CompoundTag getIslandNBT(MinecraftServer server) {
        if(IslandNBTData == null) {
            try {
                File islandFile = new File(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID) + "/island.nbt");
                IslandNBTData = NbtIo.readCompressed(islandFile);
            } catch (IOException e) {
                LOGGER.error("Could not load external island.nbt file, using mod's internal island.nbt file.");
                try {
                    Resource rs = server.getResourceManager().getResource(new ResourceLocation(SkyblockAddon.MOD_ID, "structures/island.nbt"));
                    IslandNBTData = NbtIo.readCompressed(rs.getInputStream());
                } catch (IOException ex) {
                    LOGGER.error("Could not load mod's internal island.nbt file!!!");
                }
            }
        }
        return IslandNBTData;
    }
}
