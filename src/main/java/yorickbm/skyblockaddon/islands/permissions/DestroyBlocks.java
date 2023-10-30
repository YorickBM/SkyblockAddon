package yorickbm.skyblockaddon.islands.permissions;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import yorickbm.skyblockaddon.configs.SkyblockAddonLanguageConfig;
import yorickbm.skyblockaddon.util.ServerHelper;

public class DestroyBlocks extends Permission {

    public DestroyBlocks(boolean state) {
        super(state);
    }

    @Override
    public Item getDisplayItem() {
        return Items.DIAMOND_PICKAXE;
    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.permissions.BreakBlocks.title"), ChatFormatting.BLUE, ChatFormatting.BOLD);
    }

    @Override
    public Component[] getDescription() {
        return new Component[] {
                ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.permissions.BreakBlocks.desc"), ChatFormatting.GRAY),
                ServerHelper.formattedText("\n\n", ChatFormatting.ITALIC),
                ServerHelper.combineComponents(
                        ServerHelper.formattedText(SkyblockAddonLanguageConfig.getForKey("guis.default.allowed") + " ", ChatFormatting.GRAY),
                        ServerHelper.formattedText((state ? SkyblockAddonLanguageConfig.getForKey("guis.default.true") : SkyblockAddonLanguageConfig.getForKey("guis.default.false")), (state ? ChatFormatting.GREEN : ChatFormatting.RED))
                )
        };
    }
}
