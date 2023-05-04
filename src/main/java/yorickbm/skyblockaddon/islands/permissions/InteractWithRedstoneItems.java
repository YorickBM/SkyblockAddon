package yorickbm.skyblockaddon.islands.permissions;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yorickbm.skyblockaddon.util.LanguageFile;
import yorickbm.skyblockaddon.util.MouseButton;
import yorickbm.skyblockaddon.util.ServerHelper;

import java.util.ArrayList;
import java.util.List;

public class InteractWithRedstoneItems extends Permission {
    private static final Logger LOGGER = LogManager.getLogger();
    private List<Item> Allowed_Clickable_Blocks = new ArrayList<>();

    public InteractWithRedstoneItems(boolean state) {
        super(state);
    }

    @Override
    public boolean isAllowed(Object ...data) {
        Item itemClickedOn = (Item) data[0];

        if(state) return true; //User is allowed to Interact with Blocks, we can just skip unnecessary checks

        //Go through filters to know if we can just allow it.
        if(Allowed_Clickable_Blocks.contains(itemClickedOn)) return true;

        return false;
    }

    @Override
    public Item getDisplayItem() {
        return Items.REDSTONE;
    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return ServerHelper.formattedText(LanguageFile.getForKey("guis.permissions.UseRedstone.title"), ChatFormatting.BLUE, ChatFormatting.BOLD);
    }

    @Override
    public Component[] getDescription() {
        return new Component[] {
                ServerHelper.formattedText(LanguageFile.getForKey("guis.permissions.UseRedstone.desc"), ChatFormatting.GRAY),
                ServerHelper.formattedText("\n\n", ChatFormatting.ITALIC),
                ServerHelper.combineComponents(
                        ServerHelper.formattedText("\u2666 Allowed: ", ChatFormatting.GRAY),
                        ServerHelper.formattedText((state ? "TRUE" : "FALSE"), (state ? ChatFormatting.GREEN : ChatFormatting.RED))
                )
        };
    }
}
