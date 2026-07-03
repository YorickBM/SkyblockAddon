package yorickbm.skyblockaddon.util;

import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import yorickbm.guilibrary.GUIItemStackHolder;
import yorickbm.guilibrary.util.ConditionEvaluator;
import yorickbm.skyblockaddon.core.JSON.ItemStackJson;

import java.util.Arrays;
import java.util.List;

public class ForgeConverter {

    public static yorickbm.skyblockaddon.core.util.geometry.Vec3i ForgeToInternalVec3i(Vec3i vec) {
        return new yorickbm.skyblockaddon.core.util.geometry.Vec3i(vec.getX(), vec.getY(), vec.getZ());
    }

    public static Vec3i InternalToForgeVec3i(yorickbm.skyblockaddon.core.util.geometry.Vec3i vec) {
        return new Vec3i(vec.getX(), vec.getY(), vec.getZ());
    }

    public static BoundingBox InternalToForgeBoundingBox(yorickbm.skyblockaddon.core.util.geometry.BoundingBox box) {
        return new BoundingBox((int) box.minX, (int) box.minY, (int) box.minZ, (int) box.maxX, (int) box.maxY, (int) box.maxZ);
    }

    public static GUIItemStackHolder JSONItemToGUIItemStackHolder(final ItemStackJson jsonData) {
        return JSONItemToGUIItemStackHolder(jsonData, null);
    }

    public static GUIItemStackHolder JSONItemToGUIItemStackHolder(final ItemStackJson jsonData, final ConditionEvaluator.Context context) {
        if (jsonData == null) return null;

        // Use barrier as fallback when the item isn't registered (mod not loaded)
        final Item item = yorickbm.guilibrary.util.Helper.getItem(jsonData.getItem().toLowerCase(), Items.BARRIER);

        final List<String> display = (jsonData.getDisplay_name() != null)
                ? Arrays.asList(jsonData.getDisplay_name())
                : List.of();

        final List<List<String>> lore = LoreModelBridge.render(jsonData.getLore(), context);

        return new GUIItemStackHolder(item, 1, new CompoundTag(), display, lore);
    }

}