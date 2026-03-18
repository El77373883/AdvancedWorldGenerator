package com.advancedworldgen.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class TitleUtil {

    public static void send(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(translate(title), translate(subtitle), fadeIn, stay, fadeOut);
    }

    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(translate(message)));
    }

    private static String translate(String text) {
        if (text == null) return "";
        return text.replace("&", "§");
    }
}
