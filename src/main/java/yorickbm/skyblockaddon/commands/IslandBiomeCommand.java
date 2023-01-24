package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.*;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.mutable.MutableInt;
import yorickbm.skyblockaddon.capabilities.IslandGeneratorProvider;
import yorickbm.skyblockaddon.capabilities.PlayerIslandProvider;
import yorickbm.skyblockaddon.util.LanguageFile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class IslandBiomeCommand {

    public IslandBiomeCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("island").then(
                        Commands.literal("biome").then(
                                Commands.argument("biome", ResourceOrTagLocationArgument.resourceOrTag(Registry.BIOME_REGISTRY))
                                        .executes((command) -> {
            return execute(command.getSource(), ResourceOrTagLocationArgument.getBiome(command, "biome"), (p_262543_) -> { return true; });
        }))));
    }

    private static int quantize(int p_261998_) {
        return QuartPos.toBlock(QuartPos.fromBlock(p_261998_));
    }
    private static BlockPos quantize(BlockPos p_262148_) {
        return new BlockPos(quantize(p_262148_.getX()), quantize(p_262148_.getY()), quantize(p_262148_.getZ()));
    }
    private static BiomeResolver makeResolver(MutableInt p_262615_, ChunkAccess p_262698_, BoundingBox p_262622_, Holder<Biome> p_262705_, Predicate<Holder<Biome>> p_262695_) {
        return (p_262550_, p_262551_, p_262552_, p_262553_) -> {
            int i = QuartPos.toBlock(p_262550_);
            int j = QuartPos.toBlock(p_262551_);
            int k = QuartPos.toBlock(p_262552_);
            Holder<Biome> holder = p_262698_.getNoiseBiome(p_262550_, p_262551_, p_262552_);
            if (p_262622_.isInside(new Vec3i(i, j, k)) && p_262695_.test(holder)) {
                p_262615_.increment();
                return p_262705_;
            } else {
                return holder;
            }
        };
    }

    private int execute(CommandSourceStack command, ResourceOrTagLocationArgument.Result<Biome> p_262612_, Predicate<Holder<Biome>> p_262697_) {

        if(!(command.getEntity() instanceof Player)) { //Executed by non-player
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.nonplayer")));
            return Command.SINGLE_SUCCESS;
        }
        Player player = (Player)command.getEntity();
        Holder<Biome> biome = ForgeRegistries.BIOMES.getHolder(p_262612_.unwrap().left().get().location()).get();

        if(player.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.notoverworld")));
            return Command.SINGLE_SUCCESS;
        }

        player.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(island -> {

            Vec3i center = island.getLocation();
            BlockPos blockpos = quantize(new BlockPos(center.getX() - IslandGeneratorProvider.SIZE,0,center.getZ() - IslandGeneratorProvider.SIZE));
            BlockPos blockpos1 = quantize(new BlockPos(center.getX() + IslandGeneratorProvider.SIZE,256,center.getZ() + IslandGeneratorProvider.SIZE));
            BoundingBox boundingbox = BoundingBox.fromCorners(blockpos, blockpos1);

            ServerLevel serverlevel = command.getLevel();
            List<ChunkAccess> list = new ArrayList<>();

            for(int j = SectionPos.blockToSectionCoord(boundingbox.minZ()); j <= SectionPos.blockToSectionCoord(boundingbox.maxZ()); ++j) {
                for(int k = SectionPos.blockToSectionCoord(boundingbox.minX()); k <= SectionPos.blockToSectionCoord(boundingbox.maxX()); ++k) {
                    ChunkAccess chunkaccess = serverlevel.getChunk(k, j, ChunkStatus.FULL, false);
                    if (chunkaccess == null) {
                        continue; //Skip unloaded chunks
                    }

                    list.add(chunkaccess);
                }
            }

            MutableInt mutableint = new MutableInt(0);

            for(ChunkAccess chunkaccess1 : list) {
                chunkaccess1.fillBiomesFromNoise(
                        makeResolver(mutableint, chunkaccess1, boundingbox, biome, p_262697_),
                        serverlevel.getChunkSource().getGenerator().climateSampler());
                chunkaccess1.setUnsaved(true);
                //serverlevel.getChunkSource().updateChunkForced(chunkaccess1.getPos(), true);
                //serverlevel.getChunkSource().
            }

            command.sendSuccess(new TextComponent(LanguageFile.getForKey("commands.island.biome.changed").formatted(p_262612_.asPrintable())).withStyle(ChatFormatting.GREEN), false);
            return;

        });

        return Command.SINGLE_SUCCESS;
    }

}
