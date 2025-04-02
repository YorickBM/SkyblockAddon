package yorickbm.skyblockaddon.events.Gui;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yorickbm.guilibrary.GUILibraryRegistry;
import yorickbm.guilibrary.events.GuiDrawItemEvent;
import yorickbm.guilibrary.events.OpenMenuEvent;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.islands.groups.IslandGroup;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuiEvents {

    @SubscribeEvent
    public void itemRenderer(final GuiDrawItemEvent event) {
        if(event.isCanceled()) return; //Skip if canceled

        //Ignore condition filter if no island_id is set
        if(!event.getHolder().getData().contains("island_id")) { return; }

        event.getHolder().getOwner().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            final Island island = cap.getIslandByUUID(event.getHolder().getData().getUUID("island_id"));

            //Ignore condition if not found
            if(island == null) { return; }

            final boolean isAdmin = event.getItemHolder().hasCondition("is_admin");
            final boolean isNotAdmin = event.getItemHolder().hasCondition("!is_admin");

            if (isAdmin || isNotAdmin) {
                final Optional<IslandGroup> group = island.getGroupForEntityUUID(event.getHolder().getOwner().getUUID());
                final boolean canAccessAdminMenu = group.map(g -> g.canDo("admin_menu")).orElse(true);
                final boolean isOwner = island.isOwner(event.getHolder().getOwner().getUUID());
                final boolean hasAdminPermissions = event.getHolder().getOwner().hasPermissions(Commands.LEVEL_ADMINS);

                final boolean shouldCancel = isAdmin
                        ? !canAccessAdminMenu && !isOwner && !hasAdminPermissions // Same logic as before
                        : canAccessAdminMenu || isOwner || hasAdminPermissions;   // Negated logic

                event.setCanceled(shouldCancel);
            }

            if(event.getItemHolder().hasCondition("is_part")) {
                event.setCanceled(!island.isPartOf(event.getHolder().getOwner().getUUID()));
            } else if(event.getItemHolder().hasCondition("!is_part")) {
                event.setCanceled(island.isPartOf(event.getHolder().getOwner().getUUID()));
            }

        });
    }

    @SubscribeEvent
    public void dynamicTitle(final OpenMenuEvent event) {
        if(event.isCanceled()) return; //Skip if canceled

        //Ignore condition filter if no island_id is set
        if(!event.getData().contains("island_id")) { return; }

        event.getTarget().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            final Island island = cap.getIslandByUUID(event.getData().getUUID("island_id"));

            //Ignore condition if not found
            if (island == null) {
                return;
            }

            final List<TextComponent> title = event.getTitle();

            //Add island variables
            final Map<String, String> replacements = new HashMap<>() {{
                put("%owner%", UsernameCache.getBlocking(island.getOwner()));
                put("%x%", island.getSpawn().getX()+"");
                put("%y%", island.getSpawn().getY()+"");
                put("%z%", island.getSpawn().getZ()+"");
                put("%biome%", island.getBiome());
                put("%visibility%", island.isVisible() ? SkyBlockAddonLanguage.getLocalizedString("island.public") : SkyBlockAddonLanguage.getLocalizedString("island.private"));

            }};

            //Add group variables
            if(event.getData().contains(SkyblockAddon.MOD_ID) && event.getData().getCompound(SkyblockAddon.MOD_ID).contains("group_id")) {
                final IslandGroup group = island.getGroup(event.getData().getCompound(SkyblockAddon.MOD_ID).getUUID("group_id"));
                replacements.put("%group_name%", group.getItem().getDisplayName().getString().trim());
                replacements.put("%group_id%", group.getId().toString());
                replacements.put("%group_member_count%", group.getMembers().size()+"");

                if(event.getData().getCompound(GUILibraryRegistry.MOD_ID).contains("category_id")) {
                    final String category = event.getData().getCompound(GUILibraryRegistry.MOD_ID).getString("category_id").replace("_", " ");
                    replacements.put("%group_category%", Character.toUpperCase(category.charAt(0)) + category.substring(1));
                }

            }

            //Parse title through variables
            event.setTitle(title.stream()
                    .map(component -> {
                        final String modifiedText = replacements.entrySet().stream()
                                .reduce(component.getString(), (text, entry) -> text.replace(entry.getKey(), entry.getValue()), (a, b) -> b);
                        return new TextComponent(modifiedText).withStyle(component.getStyle());
                    })
                    .filter(c -> c instanceof TextComponent) // Ensure it's a TextComponent
                    .map(c -> (TextComponent) c) // Cast to TextComponent
                    .collect(Collectors.toList()));
        });
    }

    @SubscribeEvent
    public void dynamicText(final GuiDrawItemEvent event) {
        if(event.isCanceled()) return; //Skip if canceled

        //Ignore condition filter if no island_id is set
        if(!event.getHolder().getData().contains("island_id")) { return; }

        event.getHolder().getOwner().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            final Island island = cap.getIslandByUUID(event.getHolder().getData().getUUID("island_id"));

            //Ignore condition if not found
            if(island == null) { return; }

            final List<TextComponent> name = event.getItemStackHolder().getDisplayName();
            final List<List<TextComponent>> lore = event.getItemStackHolder().getLore();

            final Map<String, String> replacements = new HashMap<>() {{
                put("%owner%", UsernameCache.getBlocking(island.getOwner()));
                put("%x%", island.getSpawn().getX()+"");
                put("%y%", island.getSpawn().getY()+"");
                put("%z%", island.getSpawn().getZ()+"");
                put("%biome%", island.getBiome());
                put("%visibility%", island.isVisible() ? SkyBlockAddonLanguage.getLocalizedString("island.public") : SkyBlockAddonLanguage.getLocalizedString("island.private"));
            }};

            event.getItemStackHolder().setDisplayName(name.stream()
                    .map(component -> {
                        final String modifiedText = replacements.entrySet().stream()
                                .reduce(component.getString(), (text, entry) -> text.replace(entry.getKey(), entry.getValue()), (a, b) -> b);
                        return new TextComponent(modifiedText).withStyle(component.getStyle());
                    })
                    .filter(c -> c instanceof TextComponent) // Ensure it's a TextComponent
                    .map(c -> (TextComponent) c) // Cast to TextComponent
                    .collect(Collectors.toList()));

            event.getItemStackHolder().setLore(
                    lore.stream()
                            .map(innerList -> innerList.stream() // Process each inner List<TextComponent>
                                    .map(component -> {
                                        final String modifiedText = replacements.entrySet().stream()
                                                .reduce(component.getString(), (text, entry) -> text.replace(entry.getKey(), entry.getValue()), (a, b) -> b);
                                        return new TextComponent(modifiedText).withStyle(component.getStyle());
                                    })
                                    .filter(c -> c instanceof TextComponent) // Ensure it's a TextComponent
                                    .map(c -> (TextComponent) c) // Cast to TextComponent
                                    .collect(Collectors.toList()) // Collect modified components back into List<TextComponent>
                            )
                            .collect(Collectors.toList()) // Maintain the original List<List<TextComponent>> structure
            );


        });
    }
}
