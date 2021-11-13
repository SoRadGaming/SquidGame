package com.soradgaming.squidgame.managment;

import com.soradgaming.squidgame.SquidGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class gameManager {

    public static ArrayList<UUID> playerList;
    public static ArrayList<UUID> playerListAlive;
    private static final SquidGame plugin = SquidGame.plugin;

    public static void addAllOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            addPlayer(player);
        }
    }

    public static void Initialise() {
        bracketManager.playerBracket(playerList);
    }

    public static void updateAlive() {
        playerListAlive = playerList;
    }

    public static ArrayList<UUID> getPlayerList() { return playerList; }

    public static synchronized boolean removePlayer(Player player) {
        if(playerList.contains(player.getUniqueId())) {
            playerList.remove(player.getUniqueId());
            return true;
        } else {
            return false;
        }
    }

    public static synchronized boolean killPlayer(Player player) {
        if(playerListAlive.contains(player.getUniqueId())) {
            playerListAlive.remove(player.getUniqueId());
            return true;
        } else {
            return false;
        }
    }

    public static synchronized boolean addPlayer(@NotNull Player player) {
        if(!playerList.contains(player.getUniqueId())) {
            playerList.add(player.getUniqueId());
            return true;
        } else {
            return false;
        }
    }
}
