package yorickbm.skyblockaddon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import yorickbm.skyblockaddon.SkyblockAddon;
import yorickbm.skyblockaddon.capabilities.providers.SkyblockAddonWorldProvider;
import yorickbm.skyblockaddon.commands.interfaces.OverWorldCommandStack;
import yorickbm.skyblockaddon.configs.SkyBlockAddonLanguage;
import yorickbm.skyblockaddon.configs.SkyblockAddonConfig;
import yorickbm.skyblockaddon.islands.Island;

import java.util.UUID;
import java.util.WeakHashMap;

public class IslandCreateCommand extends OverWorldCommandStack {
    private static final WeakHashMap<UUID, Long> cooldowns = new WeakHashMap<>();

    public IslandCreateCommand(final CommandDispatcher<CommandSourceStack> dispatcher) {
        register(dispatcher, "island");
        register(dispatcher, "is"); // alias
    }

    private void register(CommandDispatcher<CommandSourceStack> dispatcher, String rootLiteral) {
        dispatcher.register(
                Commands.literal(rootLiteral)
                        .then(Commands.literal("create")
                                .requires(source -> source.getEntity() instanceof ServerPlayer)
                                .executes(context -> execute(
                                        context.getSource(),
                                        (ServerPlayer) context.getSource().getEntity()))
                        )
        );
    }

    @Override
    public int execute(final CommandSourceStack command, final ServerPlayer executor) {
        if(super.execute(command, executor) == 0) return Command.SINGLE_SUCCESS;
        if(this.hasActiveCooldown(executor)) {
            command.sendFailure(new TextComponent(String.format(SkyBlockAddonLanguage.getLocalizedString("commands.create.cooldown"), (this.getCooldownSecondsLeft(executor) + "s"))));
            return Command.SINGLE_SUCCESS;
        };

        command.getLevel().getCapability(SkyblockAddonWorldProvider.SKYBLOCKADDON_WORLD_CAPABILITY).ifPresent(cap -> {
            final Island island = cap.getIslandByEntityUUID(executor.getUUID());
            if(island != null) {
                command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.create.already")));
                return;
            }

            final Thread asyncIslandGen = new Thread(() -> {
                final Vec3i vec = cap.genIsland(command.getLevel());
                if(vec == null) {
                    command.sendFailure(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.create.failure")));
                    return;
                }

                final Island newIsland = new Island(executor.getUUID(), vec);
                cap.registerIsland(newIsland, executor.getUUID());

                executor.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.create.success")).withStyle(ChatFormatting.GREEN), executor.getUUID());
                newIsland.teleportTo(executor);
            });
            executor.sendMessage(new TextComponent(SkyBlockAddonLanguage.getLocalizedString("commands.create.generating")).withStyle(ChatFormatting.GREEN), executor.getUUID());
            asyncIslandGen.start();
            cooldowns.put(executor.getUUID(), System.currentTimeMillis());
        });

        return Command.SINGLE_SUCCESS;
    }

    private boolean hasActiveCooldown(final ServerPlayer player) {
        final UUID uuid = player.getUUID();
        final long currentTime = System.currentTimeMillis();

        final String cooldownStr = SkyblockAddonConfig.getForKey("island.create.cooldown");
        long cooldownSeconds;
        try {
            cooldownSeconds = Long.parseLong(cooldownStr);
        } catch (NumberFormatException e) {
            cooldownSeconds = SkyblockAddon.DEFAULT_CREATE_COOLDOWN; //Default value
        }

        final long cooldownTimeMs = cooldownSeconds * 1000;

        if (cooldowns.containsKey(uuid)) {
            final long lastUsed = cooldowns.get(uuid);
            if ((currentTime - lastUsed) < cooldownTimeMs) {
                return true;
            }
        }
        return false;
    }

    private long getCooldownSecondsLeft(final ServerPlayer player) {
        final UUID uuid = player.getUUID();

        if (!cooldowns.containsKey(uuid)) {
            return 0;
        }

        final long currentTime = System.currentTimeMillis();
        final long lastUsed = cooldowns.get(uuid);

        final String cooldownStr = SkyblockAddonConfig.getForKey("island.create.cooldown");
        long cooldownSeconds;
        try {
            cooldownSeconds = Long.parseLong(cooldownStr);
        } catch (final NumberFormatException e) {
            cooldownSeconds = SkyblockAddon.DEFAULT_CREATE_COOLDOWN;
        }

        final long cooldownTimeMs = cooldownSeconds * 1000;
        final long timeLeftMs = (lastUsed + cooldownTimeMs) - currentTime;

        // Round up
        return Math.max(0, (timeLeftMs + 999) / 1000);
    }
}
