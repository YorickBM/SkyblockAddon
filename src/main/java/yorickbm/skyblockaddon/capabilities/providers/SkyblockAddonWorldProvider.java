package yorickbm.skyblockaddon.capabilities.providers;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.SkyblockAddonWorldCapability;

public class SkyblockAddonWorldProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<SkyblockAddonWorldCapability> SKYBLOCKADDON_WORLD_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    /**
     * Basic singleton instance management
     */
    public static SkyblockAddonWorldProvider instance;
    public static SkyblockAddonWorldProvider getInstance() { return instance; }
    public SkyblockAddonWorldProvider() { instance = this; }

    private SkyblockAddonWorldCapability capability;
    private final LazyOptional<SkyblockAddonWorldCapability> optional = LazyOptional.of(this::createCapability);

    @NotNull
    private SkyblockAddonWorldCapability createCapability() {
        if(this.capability == null) this.capability = new SkyblockAddonWorldCapability();
        return this.capability;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap != SKYBLOCKADDON_WORLD_CAPABILITY) return LazyOptional.empty();
        return optional.cast();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createCapability().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createCapability().loadNBTData(nbt);
    }
}
