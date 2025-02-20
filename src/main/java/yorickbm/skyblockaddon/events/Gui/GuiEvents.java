package yorickbm.skyblockaddon.events.Gui;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yorickbm.guilibrary.events.GuiDrawItemEvent;
import yorickbm.guilibrary.events.OpenMenuEvent;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuiEvents {

    @SubscribeEvent
    public void itemRenderer(GuiDrawItemEvent event) {
        if(event.isCanceled()) return; //Skip if canceled

        //Ignore condition filter if no island_id is set
        if(!event.getHolder().getData().contains("island_id")) { return; }

        event.getHolder().getOwner().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByUUID(event.getHolder().getData().getUUID("island_id"));

            //Ignore condition if not found
            if(island == null) { return; }

            if(event.getItemHolder().hasCondition("is_admin")) {
                //TODO: Add condition for admin permission
                event.setCanceled(!island.isOwner(event.getHolder().getOwner().getUUID()) && !event.getHolder().getOwner().hasPermissions(Commands.LEVEL_ADMINS));
            } else if(event.getItemHolder().hasCondition("!is_admin")) {
                event.setCanceled(island.isOwner(event.getHolder().getOwner().getUUID()) || event.getHolder().getOwner().hasPermissions(Commands.LEVEL_ADMINS));
            }

            if(event.getItemHolder().hasCondition("is_part")) {
                event.setCanceled(!island.isPartOf(event.getHolder().getOwner().getUUID()));
            } else if(event.getItemHolder().hasCondition("!is_part")) {
                event.setCanceled(island.isPartOf(event.getHolder().getOwner().getUUID()));
            }

        });
    }

    @SubscribeEvent
    public void dynamicTitle(OpenMenuEvent event) {
        if(event.isCanceled()) return; //Skip if canceled

        //Ignore condition filter if no island_id is set
        if(!event.getData().contains("island_id")) { return; }

        event.getTarget().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByUUID(event.getData().getUUID("island_id"));

            //Ignore condition if not found
            if (island == null) {
                return;
            }

            List<TextComponent> title = event.getTitle();

            Map<String, String> replacements = new HashMap<>() {{
                put("%owner%", UsernameCache.getBlocking(island.getOwner()));
                put("%x%", island.getSpawn().getX()+"");
                put("%y%", island.getSpawn().getY()+"");
                put("%z%", island.getSpawn().getZ()+"");
                put("%biome%", island.getBiome());
                put("%visibility%", island.isVisible() ? SkyBlockAddonLanguage.getLocalizedString("island.public") : SkyBlockAddonLanguage.getLocalizedString("island.private"));
            }};

            event.setTitle(title.stream()
                    .map(component -> {
                        String modifiedText = replacements.entrySet().stream()
                                .reduce(component.getString(), (text, entry) -> text.replace(entry.getKey(), entry.getValue()), (a, b) -> b);
                        return new TextComponent(modifiedText).withStyle(component.getStyle());
                    })
                    .filter(c -> c instanceof TextComponent) // Ensure it's a TextComponent
                    .map(c -> (TextComponent) c) // Cast to TextComponent
                    .collect(Collectors.toList()));
        });
    }

    @SubscribeEvent
    public void dynamicText(GuiDrawItemEvent event) {
        if(event.isCanceled()) return; //Skip if canceled

        //Ignore condition filter if no island_id is set
        if(!event.getHolder().getData().contains("island_id")) { return; }

        event.getHolder().getOwner().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            Island island = cap.getIslandByUUID(event.getHolder().getData().getUUID("island_id"));

            //Ignore condition if not found
            if(island == null) { return; }

            List<TextComponent> name = event.getItemStackHolder().getDisplayName();
            List<List<TextComponent>> lore = event.getItemStackHolder().getLore();

            Map<String, String> replacements = new HashMap<>() {{
                put("%owner%", UsernameCache.getBlocking(island.getOwner()));
                put("%x%", island.getSpawn().getX()+"");
                put("%y%", island.getSpawn().getY()+"");
                put("%z%", island.getSpawn().getZ()+"");
                put("%biome%", island.getBiome());
                put("%visibility%", island.isVisible() ? SkyBlockAddonLanguage.getLocalizedString("island.public") : SkyBlockAddonLanguage.getLocalizedString("island.private"));
            }};

            event.getItemStackHolder().setDisplayName(name.stream()
                    .map(component -> {
                        String modifiedText = replacements.entrySet().stream()
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
                                        String modifiedText = replacements.entrySet().stream()
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
