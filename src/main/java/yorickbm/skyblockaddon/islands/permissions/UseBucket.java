package yorickbm.skyblockaddon.islands.permissions;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import yorickbm.skyblockaddon.util.LanguageFile;
import yorickbm.skyblockaddon.util.ServerHelper;

public class UseBucket extends Permission {

    public UseBucket(boolean state) {
        super(state);
    }

    @Override
    public Item getDisplayItem() {
        return Items.BUCKET;
    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return ServerHelper.formattedText(LanguageFile.getForKey("guis.permissions.UseBucket.title"), ChatFormatting.BLUE, ChatFormatting.BOLD);
    }

    @Override
    public Component[] getDescription() {
        return new Component[] {
                ServerHelper.formattedText(LanguageFile.getForKey("guis.permissions.UseBucket.desc"), ChatFormatting.GRAY),
                ServerHelper.formattedText("\n\n", ChatFormatting.ITALIC),
                ServerHelper.combineComponents(
                        ServerHelper.formattedText("\u2666 Allowed: ", ChatFormatting.GRAY),
                        ServerHelper.formattedText((state ? "TRUE" : "FALSE"), (state ? ChatFormatting.GREEN : ChatFormatting.RED))
                )
        };
    }
}
