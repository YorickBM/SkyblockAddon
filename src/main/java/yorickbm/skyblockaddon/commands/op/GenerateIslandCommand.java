package yorickbm.skyblockaddon.commands.op;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.core.jmx.Server;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.providers.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.providers.PlayerIslandProvider;
import yorickbm.skyblockaddon.configs.SkyblockAddonConfig;
import yorickbm.skyblockaddon.configs.SkyblockAddonLanguageConfig;
import yorickbm.skyblockaddon.islands.IslandData;
import yorickbm.skyblockaddon.util.BuildingBlock;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.ArrayList;
import java.util.Collection;

public class GenerateIslandCommand {

    public GenerateIslandCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("island")
                        .then(
                                Commands.literal("admin")
                                        .requires(p -> p.hasPermission(3))
                                        .then(Commands.literal("generate").then(Commands.argument("position", Vec3Argument.vec3())
                                                .executes( (command) -> execute(command.getSource(), Vec3Argument.getVec3(command, "position")))
                                        ))
                        )
        );
    }

    private int execute(CommandSourceStack command, Vec3 location) {
        if(!(command.getEntity() instanceof Player player)) { //Executed by non-player
            command.sendFailure(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.not.player")));
            return Command.SINGLE_SUCCESS;
        }

        if(player.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(SkyblockAddonLanguageConfig.getForKey("commands.not.overworld")));
            return Command.SINGLE_SUCCESS;
        }

        CompoundTag nbt = SkyblockAddon.getIslandNBT(command.getServer());

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
            command.sendFailure(ServerHelper.formattedText("Block pallet was empty, sorry!"));
            return Command.SINGLE_SUCCESS;
        }

        final int finalBigestX = bigestX, finalBigestZ = bigestZ;
        final int height = Integer.parseInt(SkyblockAddonConfig.getForKey("island.spawn.height"));
        final Vec3i flocation = new Vec3i(location.x, 0, location.z);
        blocks.forEach(block -> block.place(command.getLevel(), flocation.offset(-(finalBigestX /2), height ,-(finalBigestZ /2))));

        ChunkAccess chunk = command.getLevel().getChunk(new BlockPos(flocation.getX(), height ,flocation.getZ()));
        int topHeight = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, flocation.getX(), flocation.getZ()) +2;

        player.teleportTo(flocation.getX(), topHeight, flocation.getZ());
        command.sendSuccess(ServerHelper.formattedText(String.format("We have teleported you to %s;%s;%s.", flocation.getX(), topHeight, flocation.getZ()), ChatFormatting.GREEN), true);

        return Command.SINGLE_SUCCESS;
    }
}
