package yorickbm.skyblockaddon.registries;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.eventbus.api.Cancelable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.guilibrary.GUIFiller;
import yorickbm.guilibrary.GUIItemStackHolder;
import yorickbm.guilibrary.events.GuiDrawFillerEvent;
import yorickbm.guilibrary.interfaces.ServerInterface;
import yorickbm.guilibrary.util.FillerPattern;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.islands.Island;
import yorickbm.skyblockaddon.registries.interfaces.SkyblockAddonRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Cancelable
public class RegistryEvents extends GuiDrawFillerEvent {
    private static final Logger LOGGER = LogManager.getLogger();

    protected SkyblockAddonRegistry registry;
    Map<String, String> replacements = new HashMap<>();

    public RegistryEvents(ServerInterface instance, GUIFiller filler, int slots) {
        super(instance, filler, slots);
    }

    private int getItemsPerPage() {
        int fillerRows = ((getSlots() / 9) - 2);
        return ((fillerRows * 9) - //Turn rows back into slots
                (fillerRows * 2)); // Remove 2 slots per each row
    }

    public SkyblockAddonRegistry getRegistry() {
        return this.registry;
    }

    public GUIItemStackHolder processHolder(GUIItemStackHolder holder, CompoundTag data) {

        //Add skullowner data into the item
        if(data.contains("SkullOwner")) {
            holder.addData("SkullOwner", data.getString("SkullOwner"));
        }

        //Add all data as replaceable keys
        for(String key : data.getAllKeys()) {
            replacements.put("%data_"+key+"%", data.getString(key));
        }

        //Replace found data keys with their values
        holder.setDisplayName(holder.getDisplayName().stream()
                .map(component -> {
                    String modifiedText = replacements.entrySet().stream()
                            .reduce(component.getString(), (text, entry) -> text.replace(entry.getKey(), entry.getValue()), (a, b) -> b);
                    return new TextComponent(modifiedText).withStyle(component.getStyle());
                })
                .filter(c -> c instanceof TextComponent) // Ensure it's a TextComponent
                .map(c -> (TextComponent) c) // Cast to TextComponent
                .collect(Collectors.toList()));

        holder.setLore(
                holder.getLore().stream()
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

        return holder;
    }

    @Cancelable
    public static class BiomeRegistry extends RegistryEvents {
        public BiomeRegistry(ServerInterface instance, GUIFiller filler, int slots) {
            super(instance, filler, slots);

            if (getPattern() != FillerPattern.INSIDE) { this.setCanceled(true); } //Pattern is not compatible with registry
            super.registry = new yorickbm.skyblockaddon.registries.BiomeRegistry();

            int maxPage = (int)Math.ceil((double) getRegistry().getSize() / super.getItemsPerPage()); //Divide the item amounts we have by available slots per page
            super.setMaxPage(maxPage);
            getRegistry().setIndex(((super.getCurrentPage() - 1) * super.getItemsPerPage())-1);

            if(instance.getData().contains("island_id")) {
                instance.getOwner().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
                    Island island = cap.getIslandByUUID(instance.getData().getUUID("island_id"));
                    if(island == null) return;
                    super.getItemStackHolder().addData("island_biome", island.getBiome());
                });
            }

        }
    }

    @Cancelable
    public static class IslandRegistry extends RegistryEvents {
        public IslandRegistry(ServerInterface instance, GUIFiller filler, int slots) {
            super(instance, filler, slots);

            if (getPattern() != FillerPattern.INSIDE) { this.setCanceled(true); } //Pattern is not compatible with registry

            instance.getOwner().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
                super.registry = new yorickbm.skyblockaddon.registries.IslandRegistry(cap);

                int maxPage = (int)Math.ceil((double) getRegistry().getSize() / super.getItemsPerPage()); //Divide the item amounts we have by available slots per page
                super.setMaxPage(maxPage);
                getRegistry().setIndex(((super.getCurrentPage() - 1) * super.getItemsPerPage())-1);
            });

        }
    }


    @Cancelable
    public static class GroupsRegistry extends RegistryEvents {
        public GroupsRegistry(ServerInterface instance, GUIFiller filler, int slots) {
            super(instance, filler, slots);

            if (getPattern() != FillerPattern.INSIDE) this.setCanceled(true); //Pattern is not compatible with registry
            if(!instance.getData().contains("island_id"))  this.setCanceled(true); //No island ID found

            instance.getOwner().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
                Island island = cap.getIslandByUUID(instance.getData().getUUID("island_id"));
                if(island == null) return;

                super.registry = new yorickbm.skyblockaddon.registries.GroupsRegistry(island);
                int maxPage = (int)Math.ceil((double) getRegistry().getSize() / super.getItemsPerPage()); //Divide the item amounts we have by available slots per page
                super.setMaxPage(maxPage);
                getRegistry().setIndex(((super.getCurrentPage() - 1) * super.getItemsPerPage())-1);
            });
        }
    }
}
