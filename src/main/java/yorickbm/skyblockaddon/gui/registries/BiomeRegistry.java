package yorickbm.skyblockaddon.gui.registries;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.gui.interfaces.ContextParser;
import yorickbm.skyblockaddon.gui.interfaces.SkyblockAddonRegistry;

import java.util.List;
import java.util.Objects;

public class BiomeRegistry extends SkyblockAddonRegistry {
    protected final List<Biome> biomes;

    public BiomeRegistry() {
        this.biomes = ForgeRegistries.BIOMES.getValues().stream().filter(p -> Objects.requireNonNull(p.getRegistryName()).toString().startsWith("minecraft:")).toList();
    }

    @Override
    public boolean getNextData(CompoundTag tag) {
        if(this.index >= this.biomes.size()) return false;

        tag.putString("biome", Objects.requireNonNull(this.biomes.get(this.index).getRegistryName()).toString());
        tag.putString("name", Objects.requireNonNull(this.biomes.get(this.index).getRegistryName()).toString().split(":")[1]);
        index++;
        return true;
    }

    @Override
    public int getSize() {
        return biomes.size();
    }
}
