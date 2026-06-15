package yorickbm.skyblockaddon.util;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.registries.ForgeRegistries;
import yorickbm.guilibrary.GUIItemStackHolder;
import yorickbm.skyblockaddon.core.JSON.ItemStackJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ForgeConverter {

    public static yorickbm.skyblockaddon.core.util.geometry.Vec3i ForgeToInternalVec3i(Vec3i vec) {
        return new yorickbm.skyblockaddon.core.util.geometry.Vec3i(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vec3i InternalToForgeVec3i(yorickbm.skyblockaddon.core.util.geometry.Vec3i vec) {
        return new Vec3i(vec.getX(), vec.getY(), vec.getZ());
    }

    public static BoundingBox InternalToForgeBoundingBox(yorickbm.skyblockaddon.core.util.geometry.BoundingBox box) {
        return new BoundingBox((int)box.minX, (int)box.minY, (int)box.minZ, (int)box.maxX, (int)box.maxY, (int)box.maxZ);
    }

    public static GUIItemStackHolder JSONItemToGUIItemStackHolder(final ItemStackJson jsonData) {
        return JSONItemToGUIItemStackHolder(jsonData, null);
    }

    public static GUIItemStackHolder JSONItemToGUIItemStackHolder(final ItemStackJson jsonData, final Predicate<String> isModLoaded) {
        if (jsonData == null) return null;

        // Use barrier as fallback when the item isn't registered (mod not loaded)
        final Item item = yorickbm.guilibrary.util.Helper.getItem(jsonData.getItem().toLowerCase(), Items.BARRIER);

        final List<String> display = (jsonData.getDisplay_name() != null)
                ? Arrays.asList(jsonData.getDisplay_name())
                : List.of();

        final List<List<String>> lore = (jsonData.getLore() != null)
                ? new ArrayList<>(Arrays.stream(jsonData.getLore())
                        .map(Arrays::asList)
                        .collect(Collectors.toList()))
                : new ArrayList<>();

        // Append conditional lore lines when their condition is met
        if (isModLoaded != null && jsonData.getConditionalLore() != null) {
            for (final ItemStackJson.ConditionalLoreEntry entry : jsonData.getConditionalLore()) {
                if (entry.line != null && evaluateLoreCondition(entry.condition, isModLoaded)) {
                    lore.add(Arrays.asList(entry.line));
                }
            }
        }

        return new GUIItemStackHolder(item, 1, new CompoundTag(), display, lore);
    }

    private static boolean evaluateLoreCondition(final String condition, final Predicate<String> isModLoaded) {
        if (condition == null || condition.isEmpty()) return true;
        if (condition.startsWith("any_mod_loaded:")) {
            final String[] mods = condition.substring("any_mod_loaded:".length()).split(",");
            return Arrays.stream(mods).map(String::trim).anyMatch(isModLoaded);
        }
        return true;
    }

}
