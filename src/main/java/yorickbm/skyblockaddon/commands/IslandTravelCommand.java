package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.gui.GUIManager;
import yorickbm.skyblockaddon.gui.interfaces.GuiContext;

import java.util.UUID;

public class IslandTravelCommand {
    public IslandTravelCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("island")
                .then(Commands.literal("travel")
                        .requires(source -> source.getEntity() instanceof ServerPlayer)
                        .executes(context -> execute(context.getSource(), (ServerPlayer) context.getSource().getEntity()))
                )
        );
    }

    private int execute(CommandSourceStack command, ServerPlayer executor) {
        if (executor.level.dimension() != Level.OVERWORLD) {
            command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.not.in.overworld")));
            return Command.SINGLE_SUCCESS;
        }

        GUIManager.getInstance().openMenu("travel", executor, new GuiContext() {
            @Override
            public void teleportTo(Entity entity) {

            }

            @Override
            public boolean kickMember(Entity source, UUID entity) {
                return false;
            }

            @Override
            public void setSpawnPoint(Vec3i point) {

            }

            @Override
            public void toggleVisibility() {

            }

            @Override
            public void updateBiome(String biome, ServerLevel serverlevel) {

            }

            @Override
            public Component parseTextComponent(@NotNull Component original) {
                return null;
            }
        });
        return Command.SINGLE_SUCCESS;
    }
}
