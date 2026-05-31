package yorickbm.skyblockaddon.core.JSON;

import com.google.gson.Gson;
import yorickbm.skyblockaddon.core.registries.PermissionGroupRegistry;
import yorickbm.skyblockaddon.core.util.JSON.JSONSerializable;

import java.util.*;

public class PermissionDataJson implements JSONSerializable {

    // ── Old format (backward compat) ──────────────────────────────────────
    protected String[] skyblockaddon;
    protected String[] items;
    protected String[] blocks;
    protected String[] entities;
    protected boolean defaults = false;

    // ── New format ────────────────────────────────────────────────────────
    protected List<String> item;
    protected List<String> block;
    protected List<String> entity;
    protected List<String> dimension;
    protected List<String> gui;

    // ── Resolved state (populated by resolve()) ───────────────────────────
    // null  = context not set (not relevant for this permission)
    // []    = set but empty (binary: matches everything, old format only)
    // [..] = patterns to match against
    private transient Map<String, List<String>> resolvedFilters;

    /**
     * Resolve group references and normalise old/new format into resolvedFilters.
     * Must be called after deserialisation, before getFiltersForContext().
     */
    public void resolve(final PermissionGroupRegistry groups) {
        resolvedFilters = new HashMap<>();

        final boolean hasNewFormat = item != null || block != null || entity != null
                || dimension != null || gui != null;

        if (hasNewFormat) {
            addNewContext("item",      item,      groups);
            addNewContext("block",     block,     groups);
            addNewContext("entity",    entity,    groups);
            addNewContext("dimension", dimension, groups);
            addNewContext("gui",       gui,       groups);
        } else {
            // Old format – keep empty-array = binary semantics
            addOldContext("item",   items   != null ? Arrays.asList(items)   : null, groups);
            addOldContext("block",  blocks  != null ? Arrays.asList(blocks)  : null, groups);
            addOldContext("entity", entities != null ? Arrays.asList(entities) : null, groups);

            if (skyblockaddon != null && skyblockaddon.length > 0) {
                final List<String> dims = new ArrayList<>();
                final List<String> guis = new ArrayList<>();
                for (final String s : skyblockaddon) {
                    if (s.startsWith("dimension:")) dims.add(s.substring("dimension:".length()));
                    else if (s.startsWith("gui:"))       guis.add(s.substring("gui:".length()));
                }
                if (!dims.isEmpty()) resolvedFilters.put("dimension", dims);
                if (!guis.isEmpty()) resolvedFilters.put("gui",       guis);
            }
        }
    }

    private void addNewContext(final String ctx, final List<String> patterns,
                               final PermissionGroupRegistry groups) {
        if (patterns == null) return; // absent from JSON → not relevant

        final List<String> resolved = groups.expandPatterns(ctx, patterns);

        // If all entries were group references that resolved to nothing → mod not loaded → inactive
        if (resolved.isEmpty() && !patterns.isEmpty()
                && patterns.stream().allMatch(p -> p.matches("!?#.*"))) {
            return;
        }

        resolvedFilters.put(ctx, resolved);
    }

    private void addOldContext(final String ctx, final List<String> patterns,
                               final PermissionGroupRegistry groups) {
        if (patterns == null) return;
        // Old format: always add (even empty = binary semantics)
        resolvedFilters.put(ctx, groups.expandPatterns(ctx, patterns));
    }

    /**
     * Returns the resolved pattern list for the given context, or null if the
     * context is not set for this permission.
     *
     * null  → not relevant (context not declared in data)
     * []    → binary match (empty old-format array or explicitly empty new-format)
     * [..] → patterns to match
     */
    public List<String> getFiltersForContext(final String context) {
        if (resolvedFilters == null) resolve(PermissionGroupRegistry.getInstance());
        return resolvedFilters.get(context);
    }

    public boolean hasContext(final String context) {
        if (resolvedFilters == null) resolve(PermissionGroupRegistry.getInstance());
        return resolvedFilters.containsKey(context);
    }

    public boolean getDefault() { return defaults; }

    // ── Backward compat getters (used by old code paths) ──────────────────

    public List<String> getItemsData() {
        final List<String> d = getFiltersForContext("item");
        return d != null ? d : List.of();
    }

    public List<String> getBlocksData() {
        final List<String> d = getFiltersForContext("block");
        return d != null ? d : List.of();
    }

    public List<String> getEntitiesData() {
        final List<String> d = getFiltersForContext("entity");
        return d != null ? d : List.of();
    }

    public List<String> getSkyblockaddonData() {
        final List<String> d = getFiltersForContext("skyblockaddon");
        return d != null ? d : List.of();
    }

    @Override
    public String toJSON() { return new Gson().toJson(this); }

    @Override
    public void fromJSON(final String json) {
        final PermissionDataJson temp = new Gson().fromJson(json, PermissionDataJson.class);
        this.skyblockaddon = temp.skyblockaddon;
        this.items         = temp.items;
        this.blocks        = temp.blocks;
        this.entities      = temp.entities;
        this.defaults      = temp.defaults;
        this.item          = temp.item;
        this.block         = temp.block;
        this.entity        = temp.entity;
        this.dimension     = temp.dimension;
        this.gui           = temp.gui;
        this.resolvedFilters = null; // reset
    }
}
