package yorickbm.skyblockaddon.mixins;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {

    @Override
    public void connect() {
        Mixins.addConfigurations(new String[]{"assets/skyblockaddon/skyblockaddon.mixin.json","assets/skyblockaddon/skyblockaddon.mixin.optional.json"});
    }
}
