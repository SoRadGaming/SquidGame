package com.soradgaming.squidgame.managment;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.games.Game1;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class playerManager implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    public static boolean gameStarted = false;

    //Start command
    public static void playerBracket(@NotNull ArrayList<UUID> input) {
        //Active Players

        for (UUID uuid : input) {
            Player p = Bukkit.getPlayer(uuid);
            //Data
            if (plugin.data.getInt(uuid + ".wins") >= 0) {
                plugin.data.set(uuid + ".wins", 0);
            }
            //Save Data
            plugin.saveFile();
        }
    }

    public static boolean playerJoin(Player player) {
        if (gameManager.getPlayerList().size() <= plugin.getConfig().getInt("max-players") && gameManager.addPlayer(Objects.requireNonNull(player))) {
            gameManager.revivePlayer(player);
            plugin.data.set("join",player.getUniqueId().toString());
            playerManager.checkStart();
            for (UUID uuid : gameManager.getPlayerList()) {
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(gameManager.formatMessage(player,"arena.join"));
            }
            return true;
        }
        return false;
    }

    public static boolean playerLeave(Player player) {
        if (gameManager.removePlayer(Objects.requireNonNull(player))) {
            gameManager.revivePlayer(player);
            playerManager.checkStart();
            plugin.data.set("leave",player.getUniqueId().toString());
            for (UUID uuid : gameManager.getPlayerList()) {
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(gameManager.formatMessage(player,"arena.leave"));
            }
            return true;
        }
        return false;
    }

    public static void checkStart() {
        int min = plugin.getConfig().getInt("min-players");
        BukkitScheduler gameStartTask = Bukkit.getScheduler();
        if (gameManager.getPlayerList().size() >= min && !gameStarted) {
            gameStartTask.runTaskLater(plugin, () -> {
                gameManager.Initialise();
                Game1.startGame1(gameManager.getPlayerList());
                for (UUID uuid : gameManager.getPlayerList()) {
                    Player player = Bukkit.getPlayer(uuid);
                    Objects.requireNonNull(player).sendMessage(gameManager.formatMessage(player, "arena.started"));
                }
                gameStarted = true;
                }, 20L * plugin.getConfig().getInt("start-time"));
                //Message Starting
            for (UUID uuid : gameManager.getPlayerList()) {
                Player player = Bukkit.getPlayer(uuid);
                Objects.requireNonNull(player).sendMessage(gameManager.formatMessage(player, "arena.starting"));
            }
        } else {
            gameStartTask.cancelTasks(plugin);
        }
    }
}
