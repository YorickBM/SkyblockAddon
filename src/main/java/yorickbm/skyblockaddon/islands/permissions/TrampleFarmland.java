package yorickbm.skyblockaddon.islands.permissions;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import yorickbm.skyblockaddon.util.LanguageFile;
import yorickbm.skyblockaddon.util.ServerHelper;

public class TrampleFarmland extends Permission {

    public TrampleFarmland(boolean state) {
        super(state);
    }

    @Override
    public Item getDisplayItem() {
        return Items.FARMLAND;
    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return ServerHelper.formattedText(LanguageFile.getForKey("guis.permissions.TrampleFarmland.title"), ChatFormatting.BLUE, ChatFormatting.BOLD);
    }

    @Override
    public Component[] getDescription() {
        return new Component[] {
                ServerHelper.formattedText(LanguageFile.getForKey("guis.permissions.TrampleFarmland.desc"), ChatFormatting.GRAY),
                ServerHelper.formattedText("\n\n", ChatFormatting.ITALIC),
                ServerHelper.combineComponents(
                        ServerHelper.formattedText("\u2666 Allowed: ", ChatFormatting.GRAY),
                        ServerHelper.formattedText((state ? "TRUE" : "FALSE"), (state ? ChatFormatting.GREEN : ChatFormatting.RED))
                )
        };
    }
}
