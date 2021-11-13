package com.soradgaming.squidgame.games;

import com.sk89q.worldedit.math.Vector3;
import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.math.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Game1 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    private static ArrayList<UUID> playerList;
    private static ArrayList<UUID> playerListAlive;
    private static boolean Started = false;
    private static boolean canWalk = true;
    private static final BukkitScheduler scheduler = Bukkit.getScheduler();
    private static final BukkitScheduler scheduler1 = Bukkit.getScheduler();
    private static final BukkitScheduler redLight = Bukkit.getScheduler();
    private static final BukkitScheduler greenLight = Bukkit.getScheduler();
    private static final BukkitScheduler delay = Bukkit.getScheduler();
    private static BossBar bossBar;
    private static double timerInterval;
    private static Cuboid killZone;
    private static Cuboid goalZone;
    private static Cuboid barrierZone;

    public static void startGame1(ArrayList<UUID> input) {
        playerList = input;
        playerListAlive = input;
        Started = true;
        canWalk = true;
        int minutes = (plugin.getConfig().getInt("game1_timer")/60);
        int seconds = (plugin.getConfig().getInt("game1_timer") - (minutes * 60));
        bossBar = Bukkit.createBossBar(ChatColor.BOLD + "Game Timer " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds , BarColor.BLUE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(0);
        //BARRIER
        for (UUID uuid : playerList) {
            Player p = Bukkit.getPlayer(uuid);
            //TP PLAYERS
            bossBar.addPlayer(Objects.requireNonNull(p));
        }
        for (UUID uuid : playerList) {
            Player p = Bukkit.getPlayer(uuid);
            //GIVE BRIEFING
        }
        timerInterval = (1 / (double) plugin.getConfig().getInt("game1_timer"));
        // With BukkitScheduler
        scheduler.runTaskLater(plugin, Game1::endGame1, 20L * (plugin.getConfig().getInt("game1_timer") + 1));
        scheduler1.runTaskTimer(plugin, Game1::bossBarProgress, 20L, 20L);
        //START
        singDoll();
    }

    public static void bossBarProgress() {
        double bossBarProgress = bossBar.getProgress();
        if (bossBarProgress + timerInterval < 1) {
            bossBar.setProgress(bossBarProgress + timerInterval);
        }
    }

    public static void endGame1() {
        scheduler1.cancelTasks(plugin);
        scheduler.cancelTasks(plugin);
        redLight.cancelTasks(plugin);
        greenLight.cancelTasks(plugin);
        delay.cancelTasks(plugin);
        if (Started) {
            bossBar.removeAll();
            bossBar.setVisible(false);
            Started = false;
            for (UUID value : playerList) {
                Player player = Bukkit.getPlayer(value);
                int oldWins = plugin.data.getInt(value + ".wins");
                plugin.data.set(value + ".wins", oldWins);
                //Save Data
                plugin.saveFile();
                Objects.requireNonNull(player).teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Lobby")));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void PlayerMoveEvent(@NotNull PlayerMoveEvent e) {
        if (e.getFrom().distance(Objects.requireNonNull(e.getTo())) <= 0.015) {
            return;
        } else if (e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
            return;
        }
        Player player = e.getPlayer();
        if (Started && playerList.contains(player.getUniqueId())) {
            if (!canWalk) {
                final Location location = e.getPlayer().getLocation();
                if (getKillZone().contains(location)) {
                    playerListAlive.remove(player.getUniqueId());
                }
            }
        }
    }

    private static void singDoll() {
        if (!Started) {
            return;
        }
        int max = 5;
        int min = 1;
        final int time = (int) Math.floor(Math.random()*(max-min+1)+min);
        broadcastTitle("games.first.green-light.title", "games.first.green-light.subtitle", time);
        canWalk = true;

        redLight.runTaskLater(plugin, () -> {
            final int waitTime = (int) Math.floor(Math.random()*(max-min+1)+min);
            broadcastTitle("games.first.red-light.title", "games.first.red-light.subtitle", waitTime);
            delay.runTaskLater(plugin, () -> {
                canWalk = false;
                greenLight.runTaskLater(plugin, Game1::singDoll, waitTime * 20L);
            }, 20);
        }, time * 20L);
    }

    public static void broadcastTitle(final String title, final String subtitle , int time) {
        for (final UUID uuid : playerList) {
            Player player = Bukkit.getPlayer(uuid);
            Objects.requireNonNull(player).sendTitle(title, subtitle,0, time,0);
        }
    }

    public static Cuboid getBarrier() {
        if (barrierZone == null) {
            Location loc1 = plugin.getConfig().getLocation("games.first.barrier.first");
            Location loc2 = plugin.getConfig().getLocation("games.first.barrier.second");
            barrierZone = new Cuboid(Objects.requireNonNull(loc1), Objects.requireNonNull(loc2));
        }
        return barrierZone;
    }

    public static Cuboid getKillZone() {
        if (killZone == null) {
            Location loc1 = plugin.getConfig().getLocation("games.first.killZone.first");
            Location loc2 = plugin.getConfig().getLocation("games.first.killZone.second");
            killZone = new Cuboid(Objects.requireNonNull(loc1), Objects.requireNonNull(loc2));
        }
        return killZone;
    }

    public static Cuboid getGoalZone() {
        if (goalZone == null) {
            Location loc1 = plugin.getConfig().getLocation("games.first.goal.first");
            Location loc2 = plugin.getConfig().getLocation("games.first.goal.second");
            goalZone = new Cuboid(Objects.requireNonNull(loc1), Objects.requireNonNull(loc2));
        }
        return goalZone;
    }
}
