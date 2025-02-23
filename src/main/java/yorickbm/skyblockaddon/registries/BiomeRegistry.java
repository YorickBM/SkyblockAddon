package yorickbm.skyblockaddon.registries;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.registries.interfaces.CustomItems;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;
import yorickbm.skyblockaddon.registries.json.BiomeRegistryJson;
import yorickbm.skyblockaddon.util.JSON.JSONEncoder;

import java.util.*;
import java.util.stream.Collectors;

public class BiomeRegistry extends SkyblockAddonRegistry implements CustomItems {
    private Map<String, Item> biomes;
    private List<Map.Entry<String, Item>> entries;

    public BiomeRegistry() {
        try {
            final BiomeRegistryJson data = JSONEncoder.loadFromFile(FMLPaths.CONFIGDIR.get().resolve(SkyblockAddon.MOD_ID + "/registries/BiomeRegistry.json"), BiomeRegistryJson.class);
            this.biomes = data.toMap();
            this.entries = new ArrayList<>(this.biomes.entrySet());
        } catch (final Exception e) {
            //On failure load Minecraft Biome Registry with DEAD_BUSH as item.
            this.biomes = new HashMap<>();
            ForgeRegistries.BIOMES.getValues().stream().filter(p -> Objects.requireNonNull(p.getRegistryName()).toString().startsWith("minecraft:")).forEach(s -> {
                this.biomes.put(s.getRegistryName().toString().toLowerCase(), Items.DEAD_BUSH);
            });
            this.entries = new ArrayList<>(this.biomes.entrySet());
        }
    }

    /**
     * Get item based on CompoundTag from registry
     *
     * @param tag - Data to determine item with
     * @return - Minecraft Registry Item
     */
    @Override
    public Item getItemFor(final CompoundTag tag) {
        return this.biomes.get(tag.getString("biome"));
    }

    /**
     * Load NBT data into tag for current index
     *
     * @param tag - CompoundTag to fill
     * @return - If there is another entry
     */
    @Override
    public void getNextData(final CompoundTag tag) {
        final Map.Entry<String, Item> entry = this.entries.get(this.index);

        tag.putString("biome", entry.getKey());
        tag.putString("name", Arrays.stream(
            entry.getKey()
                .split(":")[1]
                .replace("_", " ")
                .split(" ")
            )
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
            .collect(Collectors.joining(" ")));
        index++;
    }

    /**
     * Get size of registry
     * @return Amount of biomes
     */
    @Override
    public int getSize() {
        return this.entries.size();
    }
}
