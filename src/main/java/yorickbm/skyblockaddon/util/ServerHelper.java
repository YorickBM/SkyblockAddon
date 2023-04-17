package yorickbm.skyblockaddon.util;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class ServerHelper {
    public static void playSongToPlayer(ServerPlayer player, SoundEvent event, float vol, float pitch) {
        player.connection.send(new ClientboundSoundPacket(event, SoundSource.PLAYERS, player.position().x, player.position().y, player.position().z, vol, pitch));
    }

    public static Component formattedText(String text, ChatFormatting... formattings) {
        return new TextComponent(text).setStyle(Style.EMPTY.withItalic(false).applyFormats(formattings));
    }
    public static Component styledText(String text, Style style, ChatFormatting... formattings) {
        return new TextComponent(text).setStyle(style.applyFormats(formattings));
    }

    public static void addLore(ItemStack stack, Component... components) {
        ListTag lore = new ListTag();
        Arrays.stream(components).toList().forEach(text -> lore.add(StringTag.valueOf(Component.Serializer.toJson(text))));
        stack.getOrCreateTagElement("display").put("Lore", lore);
    }

    public static Component combineComponents(Component... components) {
        MutableComponent comp = components[0].plainCopy().setStyle(components[0].getStyle());
        Arrays.stream(Arrays.copyOfRange(components, 1, components.length)).toList().forEach(component -> {
            comp.append(component);
        });
        return comp;
    }

}
