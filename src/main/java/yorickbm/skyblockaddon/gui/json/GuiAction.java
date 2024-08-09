package yorickbm.skyblockaddon.gui.json;

import com.google.gson.Gson;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.gui.GUIManager;
import yorickbm.skyblockaddon.gui.util.GuiContext;
import yorickbm.skyblockaddon.gui.util.TargetHolder;
import yorickbm.skyblockaddon.gui.util.TargetType;
import yorickbm.skyblockaddon.util.JSON.JSONSerializable;

public class GuiAction implements JSONSerializable {
    private static final Logger LOGGER = LogManager.getLogger();

    private String onClick;
    private String onSecondClick;
    private TargetType targetType;
    private TargetType sourceType;

    public void onPrimaryClick(ItemStack item, TargetHolder source, TargetHolder target, GuiContext cSource, GuiContext cTarget) {
        handleClick(onClick, item, source, target, cSource, cTarget);
    }
    public void onSecondaryClick(ItemStack item, TargetHolder source, TargetHolder target, GuiContext cSource, GuiContext cTarget) {
        handleClick(onSecondClick, item,  source, target, cSource, cTarget);
    }

    private void handleClick(String action, ItemStack item, TargetHolder source, TargetHolder target, GuiContext cSource, GuiContext cTarget) {
        switch (action) {
            case "openMenu":
                if(!item.getOrCreateTagElement(SkyblockAddon.MOD_ID).contains("gui")) return;
                boolean hasOpened = GUIManager.getInstance().openMenu(
                        item.getOrCreateTagElement(SkyblockAddon.MOD_ID).getString("gui"),
                        getTargetEntity(source, target).getEntity(),
                        getSourceContext(cSource, cTarget));

                if(!hasOpened && source.getEntity() != null) source.getEntity().sendMessage(new TextComponent(String.format(SkyBlockAddonLanguage.getLocalizedString("menu.not.found"), item.getOrCreateTagElement(SkyblockAddon.MOD_ID).getString("gui"))).withStyle(ChatFormatting.RED), source.getEntity().getUUID());

                return;
            case "teleportTo":
                getSourceContext(cSource, cTarget).teleportTo(getTargetEntity(source, target).getEntity());
                return;
            case "kickMember":
                getSourceContext(cSource, cTarget).kickMember(getSourceEntity(source, target).getEntity(), getTargetEntity(source, target).getUuid());
                return;

            case "setSpawnPoint":
                return;

        }
        return;
    }

    private TargetHolder getSourceEntity(TargetHolder source, TargetHolder target) {
        return sourceType == TargetType.HOLDER ? source : target;
    }
    private GuiContext getSourceContext(GuiContext source, GuiContext target) {
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
