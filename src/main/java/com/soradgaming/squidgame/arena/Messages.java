package com.soradgaming.squidgame.arena;

import com.soradgaming.squidgame.SquidGame;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

public class Messages {
    private static final SquidGame plugin = SquidGame.plugin;

    //Message Formatting
    public static void broadcastTitle(List<Player> players, final String title, final String subtitle, int time) {
        for (final Player player : players) {
            Objects.requireNonNull(player).sendTitle(formatMessage(player,title) , formatMessage(player,subtitle),10, time * 20,10);
        }
    }

    public static String getI18n(final String key) {
        return plugin.messages.getString(key);
    }

    public static String formatMessage(final Player player, final String message) {
        final String translatedMessage = getI18n(message);
        final String formatColor = ChatColor.translateAlternateColorCodes('&',
                translatedMessage == null
                        ? "§6§lWARNING: §eMissing translation key §7" + message + " §ein message.yml file"
                        : translatedMessage);
        return PlaceholderAPI.setPlaceholders(player,formatColor);
    }

    public static void broadcastTitleAfterSeconds(List<Player> players, int seconds, final String title, final String subtitle) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> broadcastTitle(players, title, subtitle, 2), seconds * 20L);
    }

    public static void onExplainStart(List<Player> players, String input) {
        final String key = "games." + input + ".tutorial";
        broadcastTitleAfterSeconds(players,3, key + ".1.title", key + ".1.subtitle");
        broadcastTitleAfterSeconds(players,6, key + ".2.title", key + ".2.subtitle");
        broadcastTitleAfterSeconds(players,9, key + ".3.title", key + ".3.subtitle");
        broadcastTitleAfterSeconds(players,12, key + ".4.title", key + ".4.subtitle");
        broadcastTitleAfterSeconds(players,15, "events.game-start.title", "events.game-start.subtitle");
    }
}
