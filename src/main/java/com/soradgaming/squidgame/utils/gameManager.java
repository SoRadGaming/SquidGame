package com.soradgaming.squidgame.utils;

import com.soradgaming.squidgame.SquidGame;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class gameManager {

    private static ArrayList<UUID> playerListAlive = new ArrayList<>();
    private static ArrayList<UUID> playerListDead = new ArrayList<>();
    private static ArrayList<UUID> playerListAll = new ArrayList<>();
    private static boolean pvp = false;
    private static final SquidGame plugin = SquidGame.plugin;

    public static void Initialise() {
        playerManager.playerBracket(playerListAll);
    }

    public static ArrayList<UUID> getAlivePlayers() { return playerListAlive; }

    public static ArrayList<UUID> getDeadPlayers() { return playerListDead; }

    public static ArrayList<UUID> getAllPlayers() { return playerListAll; }

    public static boolean isPvPAllowed() { return pvp; }

    public static void setPvPAllowed(boolean TorF) { pvp = TorF; }

    public static void updateTotal() {
        playerListAll.clear();
        playerListAll.addAll(playerListAlive);
        playerListAll.addAll(playerListDead);
    }

    public static synchronized boolean removePlayer(Player player) {
        if(playerListAlive.contains(player.getUniqueId())) {
            playerListAlive.remove(player.getUniqueId());
            updateTotal();
            return true;
        } else {
            return false;
        }
    }

    public static synchronized boolean revivePlayer(Player player) {
        if(playerListDead.contains(player.getUniqueId())) {
            playerListDead.remove(player.getUniqueId());
            updateTotal();
            return true;
        } else {
            return false;
        }
    }

    public static synchronized boolean killPlayer(Player player) {
        if(!playerListDead.contains(player.getUniqueId())) {
            playerListDead.add(player.getUniqueId());
            plugin.data.set("dead",player.getUniqueId().toString());
            for (UUID uuid : gameManager.getAlivePlayers()) {
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(gameManager.formatMessage(player,"arena.death"));
            }
            updateTotal();
            return true;
        } else {
            return false;
        }
    }

    public static synchronized boolean addPlayer(@NotNull Player player) {
        if(!playerListAlive.contains(player.getUniqueId())) {
            playerListAlive.add(player.getUniqueId());
            updateTotal();
            return true;
        } else {
            return false;
        }
    }

    public static BlockVector configToVectors(String key) {
        BlockVector pos = new BlockVector();
        pos.setX(plugin.getConfig().getDouble(key + ".x"));
        pos.setY(plugin.getConfig().getDouble(key + ".y"));
        pos.setZ(plugin.getConfig().getDouble(key + ".z"));
        return pos;
    }

    //Message Formatting
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
}
