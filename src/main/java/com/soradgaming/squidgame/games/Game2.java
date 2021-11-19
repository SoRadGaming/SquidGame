package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.math.Cuboid;
import com.soradgaming.squidgame.utils.gameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;


public class Game2 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    private static ArrayList<UUID> playerList;
    private static boolean Started = false;
    private static final BukkitScheduler gameTimer = Bukkit.getScheduler();
    private static final BukkitScheduler bossBarProgress = Bukkit.getScheduler();
    private static final BukkitScheduler checkWin = Bukkit.getScheduler();
    private static BossBar bossBar;
    private static double timerInterval;
    public static int timeGlobal;
    private static Cuboid BuildZone1;
    private static Cuboid DisplayZone1;
    private static Cuboid BuildZone2;
    private static Cuboid DisplayZone2;
    private static Cuboid BuildZone3;
    private static Cuboid DisplayZone3;
    private static Cuboid BuildZone4;
    private static Cuboid DisplayZone4;


    public static void startGame2(ArrayList<UUID> input) {
        playerList = input;
        Started = true;
        timeGlobal = plugin.getConfig().getInt("Game2.timer");
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBar = Bukkit.createBossBar(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds , BarColor.BLUE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(0);
        gameManager.setPvPAllowed(false);
        for (UUID uuid : playerList) {
            Player p = Bukkit.getPlayer(uuid);
            Objects.requireNonNull(p).teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Game2.spawn")));
            bossBar.addPlayer(Objects.requireNonNull(p));
        }
        timerInterval = (1 / (double) timeGlobal);
        gameManager.onExplainStart("first");
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            gameTimer.runTaskLater(plugin, Game2::endGame2, 20L * (timeGlobal + 1));
            bossBarProgress.runTaskTimer(plugin, Game2::bossBarProgress, 20L, 20L);
            checkWin.runTaskTimer(plugin, Game2::checkWinLoop, 20L, 20L);
            //START
            gameManager.setPvPAllowed(true);
        }, 20L * 15);
    }

    public static void endGame2() {
        bossBarProgress.cancelTasks(plugin);
        gameTimer.cancelTasks(plugin);
        if (Started) {
            gameManager.setPvPAllowed(false);
            //End Code
            Bukkit.getScheduler().runTaskLater(plugin, () -> gameManager.intermission(Games.Game3), 20L * plugin.getConfig().getInt("endgame-time"));
        }
    }

    //TODO Override Death
    @EventHandler
    private void onPlayerDeath(final PlayerDeathEvent e) {
        final Player player = e.getEntity();

        if (Started && playerList.contains(player.getUniqueId()) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
            if (player.getKiller() != null) {
                plugin.data.set(player.getKiller().getUniqueId() + ".kills", plugin.data.getInt(player.getKiller().getUniqueId() + ".kills") + 1);
            }
            if (plugin.getConfig().getBoolean("eliminate-players")) {
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(plugin.getConfig().getLocation("Lobby"));
                gameManager.killPlayer(player);
            } else {
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(plugin.getConfig().getLocation("Lobby"));
            }
        }
    }

    private static void checkWinLoop() {
    }

    private static void bossBarProgress() {
        double bossBarProgress = bossBar.getProgress();
        if (bossBarProgress + timerInterval < 1) {
            bossBar.setProgress(bossBarProgress + timerInterval);
        }
        timeGlobal = timeGlobal - 1;
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBar.setTitle(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds);
    }
}
