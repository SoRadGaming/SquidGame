package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.utils.gameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Game4 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    private static ArrayList<UUID> playerList;
    private static ArrayList<UUID> team1;
    private static ArrayList<UUID> team2;
    private static boolean Started = false;
    private static final BukkitScheduler gameTimer = Bukkit.getScheduler();

    public static void startGame4(ArrayList<UUID> input) {
        playerList = input;
        Started = true;
        onExplainStart("fourth");
        for (UUID uuid:playerList) {
            Player player = Bukkit.getPlayer(uuid);
            //TODO give item (wooden sword) add teams
        }
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            gameTimer.runTaskLater(plugin, Game4::endGame4, 20L * (plugin.getConfig().getInt("Game4.timer") + 1));
            //START
            //TODO Set PVP ON
        }, 20L * 15);
    }

    public static void endGame4() {
        gameTimer.cancelTasks(plugin);
        if (Started) {
            //TODO Set PVP off
            for (UUID uuid: playerList) {
                Player player = Bukkit.getPlayer(uuid);
                Objects.requireNonNull(player).setHealth(20);
                player.setFoodLevel(20);
            }
            for (UUID uuid : gameManager.getPlayerList()) {
                Player player = Bukkit.getPlayer(uuid);
                Objects.requireNonNull(player).sendTitle(gameManager.formatMessage(player,"events.game-pass.title") , gameManager.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            }
            //TODO end code
        }
    }

    public static void broadcastTitle(final String title, final String subtitle , int time) {
        for (final UUID uuid : playerList) {
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
}
