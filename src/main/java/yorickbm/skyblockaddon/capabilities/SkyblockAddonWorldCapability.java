package yorickbm.skyblockaddon.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.configs.SkyblockAddonConfig;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.util.BuildingBlock;
import yorickbm.skyblockaddon.util.NBTUtil;
import yorickbm.skyblockaddon.util.ResourceManager;
import yorickbm.skyblockaddon.util.exceptions.NBTNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SkyblockAddonWorldCapability {

    Vec3i lastLocation;
    HashMap<UUID, Island> islandsByUUID;

    public SkyblockAddonWorldCapability() {
        islandsByUUID = new HashMap<>();
        lastLocation = Vec3i.ZERO;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.put("lastIsland", NBTUtil.Vec3iToNBT(lastLocation));
    }

    public void loadNBTData(CompoundTag nbt) {
        lastLocation = NBTUtil.NBTToVec3i(nbt.getCompound("lastIsland"));
    }

    /**
     * Generate an island from NBT data file stored within resources of this plugin
     * Island is template given from Iskall85 Vaulthunters S3 modpack
     * @param worldServer World you wish island to be generated within
     * @return Spawn location of island
     */
    public Vec3i genIsland(ServerLevel worldServer) {
        CompoundTag nbt = ResourceManager.getIslandNBT(worldServer.getServer());

        ListTag paletteNbt = nbt.getList("palette", 10);
        ListTag blocksNbt = nbt.getList("blocks", 10);

        ArrayList<BuildingBlock> blocks = new ArrayList<>();
        ArrayList<BlockState> palette = new ArrayList<>();
        int bigestX = 0, bigestZ = 0;

        for(int i = 0; i < paletteNbt.size(); i++) palette.add(NbtUtils.readBlockState(paletteNbt.getCompound(i)));
        for(int i = 0; i < blocksNbt.size(); i++) {
            CompoundTag blockNbt = blocksNbt.getCompound(i);
            ListTag blockPosNbt = blockNbt.getList("pos", 3);

            if(blockPosNbt.getInt(0) > bigestX) bigestX = blockPosNbt.getInt(0);
            if(blockPosNbt.getInt(2) > bigestZ) bigestZ = blockPosNbt.getInt(2);

            blocks.add(new BuildingBlock(
                    new BlockPos(
                            blockPosNbt.getInt(0),
                            blockPosNbt.getInt(1),
                            blockPosNbt.getInt(2)
                    ),
                    palette.get(blockNbt.getInt("state"))
            ));
        }

        if(blocks.isEmpty()) {
            throw new NBTNotFoundException();
        }

        lastLocation = nextGridLocation(lastLocation);

        final int finalBigestX = bigestX, finalBigestZ = bigestZ;
        final int height = Integer.parseInt(SkyblockAddonConfig.getForKey("island.spawn.height"));
        blocks.forEach(block -> block.place(worldServer, lastLocation.offset(-(finalBigestX /2), height ,-(finalBigestZ /2))));

        ChunkAccess chunk = worldServer.getChunk(new BlockPos(lastLocation.getX(), height ,lastLocation.getZ()));
        int topHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, lastLocation.getX(), lastLocation.getZ()) +2;

        return new Vec3i(lastLocation.getX(), topHeight, lastLocation.getZ());
    }

    /**
     * Get next Vec3i for an island based on snake pattern island generation algorithm
     * @param location Last island location
     * @return Location for next island
     */
    private Vec3i nextGridLocation(final Vec3i location) {
        final int x = location.getX();
        final int z = location.getZ();
        final int d = SkyblockAddon.ISLAND_SIZE * 2 + SkyblockAddon.ISLAND_BUFFER;

        if(x < z) {
            if(-1 * x < z) return new Vec3i(x + d, 0, z);
            return new Vec3i(x, 0, z + d);
        }

        if(x > z) {
            if(-1 * x >= z)  return new Vec3i(x - d, 0, z);
            return new Vec3i(x, 0, z - d);
        }

        if( x <= 0) return new Vec3i(x, 0, z + d);
        return new Vec3i(x, 0, z - d);
    }
}
