package yorickbm.skyblockaddon.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ServerHelper {
    private static final HashMap<UUID, UUID> spawnerTracker = new HashMap<>();
    private static final HashMap<UUID, UUID> terminatorTracker = new HashMap<>();

    public static void playSongToPlayer(ServerPlayer player, SoundEvent event, float vol, float pitch) {
        ServerHelper.SendPacket(player, new ClientboundSoundPacket(event, SoundSource.PLAYERS, player.position().x, player.position().y, player.position().z, vol, pitch));
    }

    public static void SendPacket(ServerPlayer player, Packet<?> packet) {
        player.connection.send(packet);
    }

    public static void showParticleToPlayer(ServerPlayer player, Vec3i location, ParticleOptions particle, int count) {
        ServerHelper.SendPacket(player, new ClientboundLevelParticlesPacket(particle, false, location.getX() + 0.5f, location.getY() + 0.5f, location.getZ() + 0.5f, 0.1f, 0f, 0.1f, 0f, count));
    }

    public static Component formattedText(String text, ChatFormatting... formattings) {
        return new TextComponent(text).setStyle(Style.EMPTY.withItalic(false).applyFormats(formattings));
    }

    public static Component styledText(String text, Style style, ChatFormatting... formattings) {
        return new TextComponent(text).setStyle(style.applyFormats(formattings));
    }

    /**
     * Get item from ForgeRegistries.
     * If not found returns @param basic
     *
     * @return - Minecraft Registry Item
     */
    public static Item getItem(String item, Item basic) {
        try {
            Item mcItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(item));
            return mcItem != null ? mcItem : basic;
        } catch (Exception ex) {
            return basic;
        }
    }

    public static void addLore(ItemStack stack, Component... components) {
        ListTag lore = new ListTag();
        Arrays.stream(components).toList().forEach(text -> lore.add(StringTag.valueOf(Component.Serializer.toJson(text))));
        stack.getOrCreateTagElement("display").put("Lore", lore);
    }

    public static Component combineComponents(Component... components) {
        MutableComponent comp = components[0].plainCopy().setStyle(components[0].getStyle());
        Arrays.stream(Arrays.copyOfRange(components, 1, components.length)).toList().forEach(comp::append);
        return comp;
    }

    public static int calculateDistance(Vec3i point1, Vec3i point2) {
        int dx = point2.getX() - point1.getX();
        int dy = point2.getY() - point1.getY();
        int dz = point2.getZ() - point1.getZ();

        return (int) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static void registerIslandBorder(ServerPlayer player, List<Vec3i> points, Vec3i location) {
        //Cleanup old threads
        if (spawnerTracker.containsKey(player.getUUID())) {
            UUID oldSpawner = spawnerTracker.get(player.getUUID());
            UUID oldTerminator = terminatorTracker.get(oldSpawner);

            ThreadManager.terminateThread(oldSpawner);
            ThreadManager.terminateThread(oldTerminator);
        }

        //Setup threads for spawner and tracker
        UUID particleSpawner = ThreadManager.startLoopingThread((id) -> {
            ServerHelper.showParticleToPlayer(player, location, ParticleTypes.CLOUD, 3);
            for (Vec3i pos : points) {
                ServerHelper.showParticleToPlayer(player, pos, ParticleTypes.CLOUD, 3);
            }
        }, 500);
        UUID terminator = ThreadManager.startThread((id) -> {
            try {
                Thread.sleep(1000 * 60 * 5);

                //Terminate spawner thread
                ThreadManager.terminateThread(particleSpawner);

                //Cleanup trackers
                spawnerTracker.remove(player.getUUID());
                terminatorTracker.remove(id);
            } catch (InterruptedException e) {
                //Nothing here
            }
        });

        //Register threads to trackers
        spawnerTracker.put(player.getUUID(), particleSpawner);
        terminatorTracker.put(particleSpawner, terminator);
    }
}