package yorickbm.skyblockaddon.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.Main;
import yorickbm.skyblockaddon.util.BuildingBlock;

import java.io.IOException;
import java.util.ArrayList;

public class IslandGenerator {
    private static final Logger LOGGER = LogManager.getLogger();

    Vec3i lastLocation = IslandGeneratorProvider.DEFAULT_SPAWN;

    public void loadStructure(MinecraftServer server) {
//        try {
//            Resource rs = server.getResourceManager().getResource(new ResourceLocation(Main.MOD_ID, "structures/island.nbt"));
//
//            if (rs == null) {
//                LOGGER.error("Island structure not found!!");
//                return;
//            }
//            CompoundTag nbt = NbtIo.readCompressed(rs.getInputStream());
//
//            ListTag paletteNbt = nbt.getList("palette", 10);
//            ListTag blocksNbt = nbt.getList("blocks", 10);
//
//            for(int i = 0; i < paletteNbt.size(); i++) palette.add(NbtUtils.readBlockState(paletteNbt.getCompound(i)));
//            for(int i = 0; i < blocksNbt.size(); i++) {
//                CompoundTag blockNbt = blocksNbt.getCompound(i);
//                ListTag blockPosNbt = blockNbt.getList("pos", 3);
//
//                if(blockPosNbt.getInt(0) > bigestX) bigestX = blockPosNbt.getInt(0);
//                if(blockPosNbt.getInt(2) > bigestZ) bigestZ = blockPosNbt.getInt(2);
//
//                blocks.add(new BuildingBlock(
//                        new BlockPos(
//                                blockPosNbt.getInt(0),
//                                blockPosNbt.getInt(1),
//                                blockPosNbt.getInt(2)
//                        ),
//                        palette.get(blockNbt.getInt("state"))
//                ));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void copyFrom(IslandGenerator source) {
        lastLocation = source.lastLocation;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("loc-x", lastLocation.getX());
        nbt.putInt("loc-y", lastLocation.getY());
        nbt.putInt("loc-z", lastLocation.getZ());
    }

    public void loadNBTData(CompoundTag nbt) {
        if(nbt.contains("loc-x"))
            lastLocation = new Vec3i(nbt.getInt("loc-x"), nbt.getInt("loc-y"), nbt.getInt("loc-z"));
        else LOGGER.warn("COULD NOT GET LOCATION!");
    }

    public void destroyIsland(Level world, Vec3i center) {
        Thread asyncDestroy = new Thread() {
            @Override
            public void run() {
                for(int x = center.getX()-IslandGeneratorProvider.SIZE; x <= center.getX()+IslandGeneratorProvider.SIZE; x++) {
                    for(int z = center.getZ()-IslandGeneratorProvider.SIZE; z <= center.getZ()+IslandGeneratorProvider.SIZE; z++) {
                        for(int y = world.getMinBuildHeight(); z <= world.getMaxBuildHeight(); y++) {
                            if(!world.getBlockState(new BlockPos(x, y, z)).isAir())
                                world.setBlockAndUpdate(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState());
                        }
                    }
                }
            }
        };
        asyncDestroy.start();
    }

    public Vec3 genIsland(ServerLevel worldServer) throws IOException {

        MinecraftServer server = worldServer.getServer();
        Resource rs = server.getResourceManager().getResource(new ResourceLocation(Main.MOD_ID, "structures/island.nbt"));
        CompoundTag nbt = NbtIo.readCompressed(rs.getInputStream());

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
            return new Vec3(IslandGeneratorProvider.DEFAULT_SPAWN.getX(), IslandGeneratorProvider.DEFAULT_SPAWN.getY(), IslandGeneratorProvider.DEFAULT_SPAWN.getZ());
        }

        lastLocation = nextGridLocation(lastLocation);

        final int finalBigestX = bigestX, finalBigestZ = bigestZ;
        blocks.forEach(block -> {
            block.place(worldServer, lastLocation.offset(-(finalBigestX /2),IslandGeneratorProvider.MIN_HEIGHT,-(finalBigestZ /2)));
        });

//        int highestY = lastLocation.getY();
//        for(int i = lastLocation.getY(); i < worldServer.getMaxBuildHeight(); i++) {
//            BlockState bs = worldServer.getBlockState(new BlockPos(lastLocation.getX(), i, lastLocation.getZ()));
//            BlockState bs_next = worldServer.getBlockState(new BlockPos(lastLocation.getX(), i+1, lastLocation.getZ()));
//            BlockState bs_prev = worldServer.getBlockState(new BlockPos(lastLocation.getX(), i-1, lastLocation.getZ()));
//
//            if(bs.isAir() && bs_next.isAir() && !bs_prev.isAir())
//                return new Vec3(lastLocation.getX(), highestY, lastLocation.getZ());
//        }

        return new Vec3(lastLocation.getX(), 121, lastLocation.getZ());
    }

    private Vec3i nextGridLocation(final Vec3i location) {
        final int x = location.getX();
        final int z = location.getZ();
        final int d = IslandGeneratorProvider.SIZE *2 + IslandGeneratorProvider.BUFFER;

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
