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
                                        .executes((command) -> execute(command.getSource(), ResourceOrTagLocationArgument.getBiome(command, "biome"), (p_262543_) -> true)))));
    }

    private int execute(CommandSourceStack command, ResourceOrTagLocationArgument.Result<Biome> p_262612_, Predicate<Holder<Biome>> p_262697_) {

        if(!(command.getEntity() instanceof Player player)) { //Executed by non-player
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.nonplayer")));
            return Command.SINGLE_SUCCESS;
        }
        Holder<Biome> biome = ForgeRegistries.BIOMES.getHolder(p_262612_.unwrap().left().get().location()).get();

        if(player.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(LanguageFile.getForKey("commands.island.notoverworld")));
            return Command.SINGLE_SUCCESS;
        }

        player.getCapability(PlayerIslandProvider.PLAYER_ISLAND).ifPresent(island -> player.getLevel().getCapability(IslandGeneratorProvider.ISLAND_GENERATOR).ifPresent(generator -> {

            generator.getIslandById(island.getIslandId()).setBiome(command.getLevel(), biome, p_262612_.asPrintable());
            command.sendSuccess(new TextComponent(LanguageFile.getForKey("commands.island.biome.changed").formatted(p_262612_.asPrintable())).withStyle(ChatFormatting.GREEN), false);
        }));

        return Command.SINGLE_SUCCESS;
    }

}
