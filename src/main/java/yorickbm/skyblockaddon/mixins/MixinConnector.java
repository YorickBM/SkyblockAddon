package yorickbm.skyblockaddon.mixins;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {

    @Override
    public void connect() {
        Mixins.addConfigurations("/mixins/skyblockaddon.mixin.json");
        Mixins.addConfiguration("/mixins/quark.mixin.json");
    }
}