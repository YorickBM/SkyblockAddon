package yorickbm.skyblockaddon.islands.permissions;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import yorickbm.skyblockaddon.configs.SkyblockAddonLanguageConfig;
import yorickbm.skyblockaddon.util.ServerHelper;

public class CollectXP extends Permission {

    public CollectXP(boolean state) {
        super(state);
    }

    @Override
    public Item getDisplayItem() {
        return Items.EXPERIENCE_BOTTLE;
    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.permissions.InteractWithXP.title"), ChatFormatting.BLUE, ChatFormatting.BOLD);
    }

    @Override
    public Component[] getDescription() {
        return new Component[] {
                ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.permissions.InteractWithXP.desc"), ChatFormatting.GRAY),
                ServerHelper.formattedText("\n\n", ChatFormatting.ITALIC),
                ServerHelper.combineComponents(
                        ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.default.allowed") + " ", ChatFormatting.GRAY),
                        ServerHelper.formattedText((state ? SkyblockAddonLanguageConfig.getForKey("guis.default.true") : SkyblockAddonLanguageConfig.getForKey("guis.default.false")), (state ? ChatFormatting.GREEN : ChatFormatting.RED))
                )
        };
    }
}
