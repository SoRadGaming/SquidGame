package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.utils.gameManager;
import com.soradgaming.squidgame.math.Cuboid;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Game1 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    private static ArrayList<UUID> playerList;
    private static boolean Started = false;
    private static boolean canWalk = true;
    private static final BukkitScheduler gameTimer = Bukkit.getScheduler();
    private static final BukkitScheduler bossBarProgress = Bukkit.getScheduler();
    private static final BukkitScheduler redLight = Bukkit.getScheduler();
    private static final BukkitScheduler greenLight = Bukkit.getScheduler();
    private static final BukkitScheduler delay = Bukkit.getScheduler();
    private static BossBar bossBar;
    private static double timerInterval;
    public static int timeGlobal;
    private static int max;
    private static int min;
    private static Cuboid killZone;
    private static Cuboid goalZone;
    private static Cuboid barrierZone;
    private static Cuboid head;

    public static void startGame1(ArrayList<UUID> input) {
        playerList = input;
        Started = true;
        canWalk = true;
        max = plugin.getConfig().getInt("Game1.lightSwitchMax");
        min = plugin.getConfig().getInt("Game1.lightSwitchMin");
        timeGlobal = plugin.getConfig().getInt("Game1.timer");
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBar = Bukkit.createBossBar(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds , BarColor.BLUE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(0);
        gameManager.setPvPAllowed(false);
        for (Block block : getBarrier().getBlocks()) {
            if (block.getType() == Material.AIR) {
                block.setType(Material.BARRIER);
            }
        }
        for (UUID uuid : playerList) {
            Player p = Bukkit.getPlayer(uuid);
            Objects.requireNonNull(p).teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Game1.spawn")));
            bossBar.addPlayer(Objects.requireNonNull(p));
        }
        gameManager.onExplainStart("first");
        timerInterval = (1 / (double) timeGlobal);
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            gameTimer.runTaskLater(plugin, Game1::endGame1, 20L * (timeGlobal + 1));
            bossBarProgress.runTaskTimer(plugin, Game1::bossBarProgress, 20L, 20L);
            //START
            gameManager.setPvPAllowed(true);
            for (Block block : getBarrier().getBlocks()) {
                if (block.getType() == Material.BARRIER) {
                    block.setType(Material.AIR);
                }
            }
            singDoll();
            }, 20L * 15);
    }

    public static void bossBarProgress() {
        double bossBarProgress = bossBar.getProgress();
        if (bossBarProgress + timerInterval < 1) {
            bossBar.setProgress(bossBarProgress + timerInterval);
        }
        timeGlobal = timeGlobal - 1;
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBar.setTitle(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds);
    }

    public static void endGame1() {
        bossBarProgress.cancelTasks(plugin);
        gameTimer.cancelTasks(plugin);
        redLight.cancelTasks(plugin);
        greenLight.cancelTasks(plugin);
        delay.cancelTasks(plugin);
        if (Started) {
            bossBar.removeAll();
            bossBar.setVisible(false);
            gameManager.broadcastTitle("events.game-timeout.title", "events.game-timeout.subtitle", 5);
            Started = false;
            canWalk = false;
            gameManager.setPvPAllowed(false);
            for (UUID value : gameManager.getAlivePlayers()) {
                Player player = Bukkit.getPlayer(value);
                Location location = Objects.requireNonNull(player).getLocation();
                if (!getGoalZone().contains(location)) { //Player didn't make it to end in time
                    if (plugin.getConfig().getBoolean("eliminate-players")) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(plugin.getConfig().getLocation("Game1.spawn"));
                        gameManager.killPlayer(player);
                    } else {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(plugin.getConfig().getLocation("Game1.spawn"));
                    }
                } else {
                    plugin.data.set(player.getUniqueId() + ".points", plugin.data.getInt(player.getUniqueId() + ".points") + 1);
                }
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (final UUID uuid : gameManager.getDeadPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    Objects.requireNonNull(player).sendTitle(gameManager.formatMessage(player,"events.game-timeout-died.title") , gameManager.formatMessage(player,"events.game-timeout-died.subtitle"),10, 30,10);
                    Objects.requireNonNull(player).teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Lobby")));
                }
                for (final UUID uuid : gameManager.getAlivePlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    Objects.requireNonNull(player).sendTitle(gameManager.formatMessage(player,"events.game-pass.title") , gameManager.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
                    Objects.requireNonNull(player).teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Lobby")));
                }
            }, 40L);
            //TODO Next Event
            Bukkit.getScheduler().runTaskLater(plugin, () -> gameManager.intermission(Games.Game3), 20L * plugin.getConfig().getInt("endgame-time"));
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
        plugin.data.set(player.getUniqueId() + ".points", plugin.data.getInt(player.getUniqueId() + ".points") + 1);
        if (Started && gameManager.getAlivePlayers().contains(player.getUniqueId())) {
            if (!canWalk) {
                final Location location = e.getPlayer().getLocation();
                if (getKillZone().contains(location)) {
                    if (plugin.getConfig().getBoolean("eliminate-players")) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(plugin.getConfig().getLocation("Game1.spawn"));
                        gameManager.killPlayer(player);
                    } else {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(plugin.getConfig().getLocation("Game1.spawn"));
                    }
                }
            }
        }
    }

    private static void singDoll() {
        if (!Started) {
            return;
        }
        final int time = (int) Math.floor(Math.random()*(max-min+1)+min);
        gameManager.broadcastTitle("games.first.green-light.title", "games.first.green-light.subtitle",time);
        canWalk = true;
        redLight.runTaskLater(plugin, () -> {
            final int waitTime = (int) Math.floor(Math.random()*(max-min+1)+min);
            gameManager.broadcastTitle("games.first.red-light.title", "games.first.red-light.subtitle", waitTime);
            delay.runTaskLater(plugin, () -> {
                canWalk = false;
                greenLight.runTaskLater(plugin, Game1::singDoll, waitTime * 20L);
            }, 20);
        }, time * 20L);
    }

    public static Cuboid getBarrier() {
        if (barrierZone == null) {
            BlockVector vector1 = gameManager.configToVectors("Game1.barrier.first_point");
            BlockVector vector2 = gameManager.configToVectors("Game1.barrier.second_point");
            World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game1.world")));
            barrierZone = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
        }
        return barrierZone;
    }

    public static Cuboid getKillZone() {
        if (killZone == null) {
            BlockVector vector1 = gameManager.configToVectors("Game1.killzone.first_point");
            BlockVector vector2 = gameManager.configToVectors("Game1.killzone.second_point");
            World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game1.world")));
            killZone = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
        }
        return killZone;
    }

    public static Cuboid getGoalZone() {
        if (goalZone == null) {
            BlockVector vector1 = gameManager.configToVectors("Game1.goal.first_point");
            BlockVector vector2 = gameManager.configToVectors("Game1.goal.second_point");
            World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game1.world")));
            goalZone = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
        }
        return goalZone;
    }

    public static Cuboid getHead() {
        if (head == null) {
            BlockVector vector1 = gameManager.configToVectors("Game1.head.first_point");
            BlockVector vector2 = gameManager.configToVectors("Game1.head.second_point");
            World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game1.world")));
            head = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
        }
        return head;
    }
}
