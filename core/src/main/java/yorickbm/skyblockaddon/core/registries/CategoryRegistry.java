package yorickbm.skyblockaddon.core.registries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.core.JSON.CategoryJson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CategoryRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final CategoryRegistry INSTANCE = new CategoryRegistry();

    public static CategoryRegistry getInstance() { return INSTANCE; }

    private List<CategoryJson> categories = new ArrayList<>();

    private static class CategoriesWrapper { List<CategoryJson> categories; }

    public void load(final Path path) {
        try {
            final String content = Files.readString(path);
            final CategoriesWrapper data = GSON.fromJson(content, CategoriesWrapper.class);
            if (data == null || data.categories == null) {
                LOGGER.warn("categories.json at {} is empty or malformed", path);
                return;
            }
            categories = data.categories;
            LOGGER.info("Loaded {} category definition(s) from categories.json", categories.size());
        } catch (final IOException e) {
            LOGGER.error("Failed to load categories.json from {}: {}", path, e.getMessage());
        }
    }

    public List<CategoryJson> getVisibleCategories(final Predicate<String> isModLoaded) {
        return categories.stream()
                .filter(c -> c.mods == null || c.mods.isEmpty()
                          || c.mods.stream().anyMatch(isModLoaded))
                .collect(Collectors.toList());
    }

    // ── JSON generation ───────────────────────────────────────────────────────

    private static class GuiOutput {
        String[] title;
        int rows;
        String key;
        List<Object> fillers;
        List<Map<String, Object>> items;
    }

    /**
     * Generates the permissions.json GUI file containing only the categories whose mods
     * are loaded. Always overwrites the output file so the GUI stays in sync with the
     * server's installed mods.
     */
    public void generatePermissionsGui(final Path outputPath, final Predicate<String> isModLoaded) {
        final List<CategoryJson> visible = getVisibleCategories(isModLoaded);

        final List<Map<String, Object>> items = new ArrayList<>();

        // Admin button at slot 4 — always present, gated by is_op
        items.add(buildNavItem(
                "Admin Controls", "dark_red", "minecraft:command_block",
                "» Access special administrative controls.",
                "admin",
                4,
                List.of("is_op")
        ));

        // Category items from slot 9 onwards
        final List<Integer> categorySlots = new ArrayList<>();
        for (int i = 9; i <= 17; i++) categorySlots.add(i); // row 2: 9 slots
        for (int i = 18; i <= 26; i++) categorySlots.add(i); // row 3: overflow

        for (int i = 0; i < visible.size() && i < categorySlots.size(); i++) {
            final CategoryJson cat = visible.get(i);
            items.add(buildCategoryItem(cat, categorySlots.get(i)));
        }

        // Navigation row (row 4, slots 27-35)
        items.add(buildActionItem(
                "Remove group", "red", "minecraft:barrier", List.of(),
                27, "yorickbm.skyblockaddon.events.IslandEvents$RemoveGroup", Map.of()
        ));
        items.add(buildActionItem(
                "Members", "blue", "minecraft:oak_boat", List.of(),
                31, "yorickbm.guilibrary.events.ItemOpenMenuEvent", Map.of("gui", "skyblockaddon:members_group")
        ));
        items.add(buildActionItem(
                "Back", "red", "minecraft:arrow", List.of(),
                35, "yorickbm.guilibrary.events.ItemOpenMenuEvent", Map.of("gui", "skyblockaddon:groups")
        ));

        final Map<String, Object> gui = new HashMap<>();
        gui.put("title", new String[]{"{\"text\": \"%group_name% - Permissions\"}"});
        gui.put("rows", 4);
        gui.put("key", "permissions");
        gui.put("fillers", buildFillers());
        gui.put("items", items);

        try {
            Files.writeString(outputPath, GSON.toJson(gui));
            LOGGER.info("Generated permissions.json with {} visible categories", visible.size());
        } catch (final IOException e) {
            LOGGER.error("Failed to write permissions.json to {}: {}", outputPath, e.getMessage());
        }
    }

    private Map<String, Object> buildCategoryItem(final CategoryJson cat, final int slot) {
        final Map<String, Object> itemData = new HashMap<>();
        itemData.put("display_name", cat.item.getDisplay_name());
        itemData.put("item", cat.item.getItem());
        if (cat.item.getLore() != null) {
            itemData.put("lore", Arrays.stream(cat.item.getLore()).map(Arrays::asList).collect(Collectors.toList()));
        }
        itemData.put("data", Map.of("category_id", cat.id));

        final Map<String, Object> action = new HashMap<>();
        action.put("onClick", "yorickbm.guilibrary.events.ItemOpenMenuEvent");
        action.put("data", Map.of("gui", "skyblockaddon:set_permission"));

        final Map<String, Object> entry = new HashMap<>();
        entry.put("item", itemData);
        entry.put("slot", slot);
        entry.put("action", action);

        // Add any_mod_loaded condition if mods are specified (extra guard for dynamic reloads)
        if (cat.mods != null && !cat.mods.isEmpty()) {
            entry.put("conditions", List.of("any_mod_loaded:" + String.join(",", cat.mods)));
        }

        return entry;
    }

    private Map<String, Object> buildNavItem(final String name, final String color, final String item,
                                              final String loreText, final String categoryId,
                                              final int slot, final List<String> conditions) {
        final Map<String, Object> itemData = new HashMap<>();
        itemData.put("display_name", new String[]{"{\"text\": \"" + name + "\", \"color\":\"" + color + "\", \"bold\":\"true\"}"});
        itemData.put("item", item);
        itemData.put("lore", List.of(List.of("{\"text\":\"" + loreText + "\",\"color\":\"dark_gray\"}")));
        itemData.put("data", Map.of("category_id", categoryId));

        final Map<String, Object> action = new HashMap<>();
        action.put("onClick", "yorickbm.guilibrary.events.ItemOpenMenuEvent");
        action.put("data", Map.of("gui", "skyblockaddon:set_permission"));

        final Map<String, Object> entry = new HashMap<>();
        entry.put("item", itemData);
        entry.put("slot", slot);
        entry.put("action", action);
        if (!conditions.isEmpty()) entry.put("conditions", conditions);

        return entry;
    }

    private Map<String, Object> buildActionItem(final String name, final String color, final String item,
                                                  final List<String> loreLines, final int slot,
                                                  final String onClick, final Map<String, String> actionData) {
        final Map<String, Object> itemData = new HashMap<>();
        itemData.put("display_name", new String[]{"{\"text\": \"" + name + "\", \"bold\":\"true\", \"color\":\"" + color + "\"}"});
        itemData.put("item", item);
        itemData.put("lore", loreLines);
        itemData.put("data", Map.of());

        final Map<String, Object> action = new HashMap<>();
        action.put("onClick", onClick);
        if (!actionData.isEmpty()) action.put("data", actionData);

        final Map<String, Object> entry = new HashMap<>();
        entry.put("item", itemData);
        entry.put("slot", slot);
        entry.put("action", action);

        return entry;
    }

    private List<Map<String, Object>> buildFillers() {
        final Map<String, Object> fillerItem = new HashMap<>();
        fillerItem.put("display_name", new String[]{"{\"text\": \"\"}"});
        fillerItem.put("item", "minecraft:gray_stained_glass_pane");
        fillerItem.put("lore", List.of());
        fillerItem.put("data", Map.of());

        final Map<String, Object> filler = new HashMap<>();
        filler.put("item", fillerItem);
        filler.put("pattern", "EMPTY");
        filler.put("action", Map.of());

        return List.of(filler);
    }
}
