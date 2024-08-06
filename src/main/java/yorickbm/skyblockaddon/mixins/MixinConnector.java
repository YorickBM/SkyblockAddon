package yorickbm.skyblockaddon.mixins;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {

    @Override
    public void connect() {
        Mixins.addConfigurations("assets/skyblockaddon/skyblockaddon.mixin.json","assets/skyblockaddon/skyblockaddon.mixin.optional.json");
    }
}