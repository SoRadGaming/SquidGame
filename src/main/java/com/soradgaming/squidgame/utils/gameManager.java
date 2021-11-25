package com.soradgaming.squidgame.utils;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.games.*;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class gameManager {

    private static ArrayList<UUID> playerListAlive = new ArrayList<>();
    private static ArrayList<UUID> playerListDead = new ArrayList<>();
    private static ArrayList<UUID> playerListAll = new ArrayList<>();
    private static boolean pvp;
    private static boolean block;
    private static final SquidGame plugin = SquidGame.plugin;

    public static void Initialise() {
        playerManager.playerBracket(playerListAll);
    }

    public static ArrayList<UUID> getAlivePlayers() { return playerListAlive; }

    public static ArrayList<UUID> getDeadPlayers() { return playerListDead; }

    public static ArrayList<UUID> getAllPlayers() {
        updateTotal();
        return playerListAll;
    }

    public static boolean isPvPAllowed() { return pvp; }

    public static void setPvPAllowed(boolean TorF) { pvp = TorF; }

    public static boolean isBlockAllowed() { return block; }

    public static void setBlockAllowed(boolean TorF) { block = TorF; }

    public static void updateTotal() {
        playerListAll.clear();
        playerListAll.addAll(playerListAlive);
        playerListAll.addAll(playerListDead);
        checkEnoughPlayersLeft();
    }

    public static void checkEnoughPlayersLeft() {
        /*
        if (playerListAlive.size() < 2 && playerManager.gameStarted && !Game7.isStarted()) {
            //End Game as not enough players left
            playerManager.gameStarted = false;
            ArrayList<UUID> players  = playerListAll;
            for (UUID uuid: players) {
                Player player = Bukkit.getPlayer(uuid);
                revivePlayer(player);
                playerManager.playerLeave(player);
                player.sendTitle(gameManager.formatMessage(player,"events.finish.draw.title"),gameManager.formatMessage(player,"events.finish.draw.subtitle"),10,30,10);
            }
            playerListAll.clear();
            playerListAlive.clear();
            playerListDead.clear();
        }

         */
    }

    public static synchronized boolean removePlayer(Player player) {
        if(playerListAlive.contains(player.getUniqueId())) {
            playerListAlive.remove(player.getUniqueId());
            updateTotal();
            return true;
        } else if(playerListDead.contains(player.getUniqueId())) {
            playerListDead.remove(player.getUniqueId());
            updateTotal();
            return true;
        } else {
            return false;
        }
    }

    public static synchronized boolean revivePlayer(Player player) {
        if(playerListDead.contains(player.getUniqueId())) {
            playerListDead.remove(player.getUniqueId());
            addPlayer(player);
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
            if (!plugin.getConfig().getBoolean("eliminate-players")) {
                for (UUID uuid : gameManager.getAlivePlayers()) {
                    Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(gameManager.formatMessage(player,"arena.death"));
                }
            }
            if(playerListAlive.contains(player.getUniqueId())) {
                playerListAlive.remove(player.getUniqueId());
                return true;
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

    public static void broadcastTitle(final String title, final String subtitle , int time) {
        for (final UUID uuid : gameManager.getAllPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            Objects.requireNonNull(player).sendTitle(gameManager.formatMessage(player,title) , gameManager.formatMessage(player,subtitle),10, time * 20,10);
        }
    }

    public static void broadcastTitleAfterSeconds(int seconds, final String title, final String subtitle) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> broadcastTitle(title, subtitle, 2), seconds * 20L);
    }

    public static void onExplainStart(String input) {
        final String key = "games." + input + ".tutorial";
        broadcastTitleAfterSeconds(3, key + ".1.title", key + ".1.subtitle");
        broadcastTitleAfterSeconds(6, key + ".2.title", key + ".2.subtitle");
        broadcastTitleAfterSeconds(9, key + ".3.title", key + ".3.subtitle");
        broadcastTitleAfterSeconds(12, key + ".4.title", key + ".4.subtitle");
        broadcastTitleAfterSeconds(15, "events.game-start.title", "events.game-start.subtitle");
    }

    public static void intermission(Games games) {
        if (!games.equals(Games.Game3) && !games.equals(Games.Game1) && !games.equals(Games.Game4)) {
            for (UUID uuid:gameManager.getAllPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                player.teleport(plugin.getConfig().getLocation("Lobby"));
                broadcastTitle("events.intermission.title", "events.intermission.subtitle" , 3);
            }
        }
        if (games.equals(Games.Game1)) {
            //No Delay on Game 1 as players already in lobby
            Game1.startGame1();
        } else if (games.equals(Games.Game2)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    Game2.startGame2();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, 20L * plugin.getConfig().getInt("intermission-time"));
        } else if (games.equals(Games.Game3)) {
            //No Delay on Game 3 as lobby is the arena
            Game3.startGame3();
        } else if (games.equals(Games.Game4)) {
            //No Delay on Game 4 as players just finished fighting in lobby
            Game4.startGame4();
        } else if (games.equals(Games.Game5)) {
            Bukkit.getScheduler().runTaskLater(plugin, Game5::startGame5, 20L * plugin.getConfig().getInt("intermission-time"));
        } else if (games.equals(Games.Game6)) {
            Bukkit.getScheduler().runTaskLater(plugin, Game6::startGame6, 20L * plugin.getConfig().getInt("intermission-time"));
        } else if (games.equals(Games.Game7)) {
            Bukkit.getScheduler().runTaskLater(plugin, Game7::startGame7, 20L * plugin.getConfig().getInt("intermission-time"));
        }
    }
}
