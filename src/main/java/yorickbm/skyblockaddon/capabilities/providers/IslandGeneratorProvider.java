package yorickbm.skyblockaddon.capabilities.providers;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
<<<<<<< Updated upstream
=======
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
>>>>>>> Stashed changes
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yorickbm.skyblockaddon.capabilities.IslandGenerator;

<<<<<<< Updated upstream
=======
import java.util.Objects;

>>>>>>> Stashed changes
public class IslandGeneratorProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<IslandGenerator> ISLAND_GENERATOR = CapabilityManager.get(new CapabilityToken<>() {
    });

<<<<<<< Updated upstream
    public static final Vec3i DEFAULT_SPAWN = new Vec3i(-7, 136, -6);
    public static final int SIZE = 400;
=======
    public static MinecraftServer server = null;
    public static Vec3i SPAWN = new Vec3i(-7, 110, -6);
>>>>>>> Stashed changes
    public static final int BUFFER = 200; //Put blocks as buffer where no can build

    public static IslandGeneratorProvider instance;
    public static IslandGeneratorProvider getInstance() { return instance; }
<<<<<<< Updated upstream
    public IslandGeneratorProvider() { instance = this; }
=======
    public IslandGeneratorProvider(MinecraftServer mcServer) {
        instance = this;
        server = mcServer;
    }
>>>>>>> Stashed changes

    private IslandGenerator generator = null;
    private final LazyOptional<IslandGenerator> optional = LazyOptional.of(this::createWorldIslandGenerator);

    private @NotNull IslandGenerator createWorldIslandGenerator() {
        if(this.generator == null) {
            this.generator = new IslandGenerator();
        }

        return this.generator;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ISLAND_GENERATOR) {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
<<<<<<< Updated upstream
        createWorldIslandGenerator().saveNBTData(nbt);
=======
        createWorldIslandGenerator().saveNBTData(nbt, server);
>>>>>>> Stashed changes
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
<<<<<<< Updated upstream
        createWorldIslandGenerator().loadNBTData(nbt);
=======
        createWorldIslandGenerator().loadNBTData(nbt, server);
>>>>>>> Stashed changes
    }
}
