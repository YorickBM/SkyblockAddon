package yorickbm.skyblockaddon.capabilities.providers;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yorickbm.skyblockaddon.capabilities.PlayerIsland;

public class PlayerIslandProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static final Capability<PlayerIsland> PLAYER_ISLAND = CapabilityManager.get(new CapabilityToken<>() {
    });

    private PlayerIsland island = null;
    private final LazyOptional<PlayerIsland> optional = LazyOptional.of(this::createPlayerIsland);

    private @NotNull PlayerIsland createPlayerIsland() {
        if(this.island == null) {
            this.island = new PlayerIsland();
        }

        return this.island;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == PLAYER_ISLAND) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return createPlayerIsland().saveNBTData(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerIsland().loadNBTData(nbt);
    }
}
