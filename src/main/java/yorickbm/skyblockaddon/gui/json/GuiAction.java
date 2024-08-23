package yorickbm.skyblockaddon.gui.json;

import com.google.gson.Gson;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.gui.GUIManager;
import yorickbm.skyblockaddon.gui.ServerGui;
import yorickbm.skyblockaddon.gui.interfaces.GuiContext;
import yorickbm.skyblockaddon.gui.util.TargetHolder;
import yorickbm.skyblockaddon.gui.util.TargetType;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;
import yorickbm.skyblockaddon.util.ServerHelper;
import yorickbm.skyblockaddon.util.UsernameCache;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class GuiAction implements JSONSerializable {
    private static final Logger LOGGER = LogManager.getLogger();

    private String onClick;
    private String onSecondClick;
    private TargetType targetType;
    private TargetType sourceType;

    public void onPrimaryClick(ItemStack item,
                               TargetHolder source,
                               GuiContext cSource,
                               ServerGui gui) {
        handleClick(onClick, item, source, cSource, gui);
    }
    public void onSecondaryClick(ItemStack item,
                                 TargetHolder source,
                                 GuiContext cSource,
                                 ServerGui gui) {
        handleClick(onSecondClick, item,  source, cSource, gui);
    }

    private void handleClick(String action, ItemStack item, TargetHolder source, GuiContext cSource, ServerGui gui) {
        ServerHelper.playSongToPlayer((ServerPlayer) source.getEntity(), SoundEvents.UI_BUTTON_CLICK, SkyblockAddon.UI_SOUND_VOL, 1f);

        TargetHolder target = null;
        AtomicReference<GuiContext> cTarget = new AtomicReference<>();
        CompoundTag modTag = item.getOrCreateTagElement(SkyblockAddon.MOD_ID);

        if(modTag.contains("islandId")) source.getEntity().getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            cTarget.set(cap.getIslandByUUID(modTag.getUUID("islandId")));
        });

        if(modTag.contains("playerId")) {
            UUID playerUUID = modTag.getUUID("playerId");
            ServerPlayer playerEntity = Objects.requireNonNull(source.getEntity().getServer()).getPlayerList().getPlayer(playerUUID);
            target = new TargetHolder(playerEntity, playerUUID);
        }

        modTag.getAllKeys().forEach(i -> {
            if(!i.endsWith("Id")) LOGGER.info("%s -> %s".formatted(i, modTag.getString(i)));
            else LOGGER.info("%s -> %s".formatted(i, modTag.getUUID(i)));
        });


        switch (action) {
            case "openMenu":
                if(!modTag.contains("gui")) return;
                boolean hasOpened = GUIManager.getInstance().openMenu(
                        modTag.getString("gui"),
                        getTargetEntity(source, target).getEntity(),
                        getContext(cSource, cTarget.get()));

                if(!hasOpened && source.getEntity() != null) source.getEntity().sendMessage(new TextComponent(String.format(SkyBlockAddonLanguage.getLocalizedString("menu.not.found"), modTag.getString("gui"))).withStyle(ChatFormatting.RED), source.getEntity().getUUID());

                return;
            case "teleportTo":
                getContext(cSource, cTarget.get()).teleportTo(getTargetEntity(source, target).getEntity());
                gui.close();

                ServerHelper.playSongToPlayer((ServerPlayer) source.getEntity(), SoundEvents.AMETHYST_BLOCK_CHIME, SkyblockAddon.UI_SUCCESS_VOL, 1f);
                source.getEntity().sendMessage(new TextComponent(
                        SkyBlockAddonLanguage.getLocalizedString("island.travel")
                ).withStyle(ChatFormatting.GREEN),source.getUuid());

                return;
            case "kickMember":
                getContext(cSource, cTarget.get()).kickMember(getEntity(source, target).getEntity(), getTargetEntity(source, target).getUuid());
                gui.close();
                return;

            case "setSpawnPoint":
                if(getEntity(source, target).getEntity() == null) return; //Source is not a valid entity
                getContext(cSource, cTarget.get()).setSpawnPoint(getEntity(source, target).getEntity().blockPosition());
                gui.draw(); //Update GUI items
                return;
            case "toggleVisibility":
                getContext(cSource, cTarget.get()).toggleVisibility();
                gui.draw();
                return;

            case "updateBiome":
                if(!modTag.contains("biome")) return;
                String biome = modTag.getString("biome");

                getContext(cSource, cTarget.get()).updateBiome(biome, (ServerLevel) source.getEntity().getLevel());
                gui.close();
                ServerHelper.playSongToPlayer((ServerPlayer) source.getEntity(), SoundEvents.AMETHYST_BLOCK_CHIME, SkyblockAddon.UI_SUCCESS_VOL, 1f);
                getEntity(source, target).getEntity().sendMessage(
                        new TextComponent(
                                String.format(SkyBlockAddonLanguage.getLocalizedString("island.biome"), biome)),
                        getEntity(source, target).getUuid());
                return;

            case "previousPage":
                gui.previousPage();
                return;
            case "nextPage":
                gui.nextPage();
                return;

        }
        return;
    }

    private TargetHolder getEntity(TargetHolder source, TargetHolder target) {
        return sourceType == TargetType.HOLDER ? source : target;
    }
    private GuiContext getContext(GuiContext source, GuiContext target) {
        return sourceType == TargetType.HOLDER ? source : target;
    }
    private TargetHolder getTargetEntity(TargetHolder source, TargetHolder target) {
        return targetType == TargetType.HOLDER ? source : target;
    }
    private GuiContext getTargetContext(GuiContext source, GuiContext target) {
        return targetType == TargetType.HOLDER ? source : target;
    }

    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public void fromJSON(String json) {
        Gson gson = new Gson();
        GuiAction temp = gson.fromJson(json, GuiAction.class);
        this.onClick = temp.onClick;
        this.onSecondClick = temp.onSecondClick;
        this.targetType = temp.targetType;
        this.sourceType = temp.sourceType;
    }

    public boolean notNone() {
        return !this.onClick.equals("NONE") || !this.onSecondClick.equals("NONE");
    }

    public String getPrimaryType() {
        return this.onClick;
    }
}
