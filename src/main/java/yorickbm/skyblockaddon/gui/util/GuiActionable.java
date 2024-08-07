package yorickbm.skyblockaddon.gui.util;

import yorickbm.skyblockaddon.gui.json.GuiAction;
import yorickbm.skyblockaddon.gui.json.GuiItemHolder;

public class GuiActionable {
    protected GuiItemHolder item;
    protected GuiAction action;

    public GuiAction getAction() { return action; }
    public GuiItemHolder getItem() { return item; }
}
