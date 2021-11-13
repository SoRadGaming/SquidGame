package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.managment.gameManager;
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
        Started = true;
        canWalk = true;
        int minutes = (plugin.getConfig().getInt("Game1.timer")/60);
        int seconds = (plugin.getConfig().getInt("Game1.timer") - (minutes * 60));
        bossBar = Bukkit.createBossBar(ChatColor.BOLD + "Game Timer " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds , BarColor.BLUE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(0);
        for (Block block : getBarrier().getBlocks()) {
            if (block.getType().equals(Material.AIR)) {
                block.setType(Material.BARRIER);
            }
        }
        for (UUID uuid : playerList) {
            Player p = Bukkit.getPlayer(uuid);
            Objects.requireNonNull(p).teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Game1.spawn")));
            bossBar.addPlayer(Objects.requireNonNull(p));
        }
        onExplainStart("first");
        timerInterval = (1 / (double) plugin.getConfig().getInt("Game1.timer"));
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            scheduler.runTaskLater(plugin, Game1::endGame1, 20L * (plugin.getConfig().getInt("Game1.timer") + 1));
            scheduler1.runTaskTimer(plugin, Game1::bossBarProgress, 20L, 20L);
            //START
            for (Block block : getBarrier().getBlocks()) {
                if (block.getType().equals(Material.BARRIER)) {
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
            broadcastTitle("events.game-timeout.title", "events.game-timeout.subtitle", 5);
            Started = false;
            canWalk = false;
            for (UUID value : gameManager.playerListAlive) {
                Player player = Bukkit.getPlayer(value);
                Location location = Objects.requireNonNull(player).getLocation();
                if (!getGoalZone().contains(location)) { //Player didn't make it to end in time
                    gameManager.playerListAlive.remove(player.getUniqueId());
                    gameManager.playerListDead.add(player.getUniqueId());
                }
                Objects.requireNonNull(player).teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Lobby")));
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (final UUID uuid : gameManager.playerListDead) {
                    Player player = Bukkit.getPlayer(uuid);
                    Objects.requireNonNull(player).sendTitle(ChatColor.translateAlternateColorCodes('&',"events.game-timeout-died.title") , ChatColor.translateAlternateColorCodes('&',"events.game-timeout-died.subtitle"),10, 30,20);
                }
                for (final UUID uuid : gameManager.playerListAlive) {
                    Player player = Bukkit.getPlayer(uuid);
                    Objects.requireNonNull(player).sendTitle(ChatColor.translateAlternateColorCodes('&',"events.game-pass.title") , ChatColor.translateAlternateColorCodes('&',"events.game-pass.subtitle"),10, 30,20);
                }
            }, 40L);
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
        if (Started && gameManager.playerListAlive.contains(player.getUniqueId())) {
            if (!canWalk) {
                final Location location = e.getPlayer().getLocation();
                if (getKillZone().contains(location)) {
                    gameManager.playerListAlive.remove(player.getUniqueId());
                    gameManager.playerListDead.add(player.getUniqueId());
                    player.setGameMode(GameMode.SPECTATOR);
                    //TODO: send message stating eliminated
                }
            }
        }
    }

    private static void singDoll() {
        if (!Started) {
            return;
        }
        int max = plugin.getConfig().getInt("Game1.lightSwitchMax");
        int min = plugin.getConfig().getInt("Game1.lightSwitchMin");
        final int time = (int) Math.floor(Math.random()*(max-min+1)+min);
        broadcastTitle("games.first.green-light.title", "games.first.green-light.subtitle",time);
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
            Objects.requireNonNull(player).sendTitle(gameManager.formatMessage(title) , gameManager.formatMessage(subtitle),0, time * 20,0);
        }
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

    public static void onExplainStart(String input) {
        final String key = "games." + input + ".tutorial";
        broadcastTitleAfterSeconds(3, key + ".1.title", key + ".1.subtitle");
        broadcastTitleAfterSeconds(6, key + ".2.title", key + ".2.subtitle");
        broadcastTitleAfterSeconds(9, key + ".3.title", key + ".3.subtitle");
        broadcastTitleAfterSeconds(12, key + ".4.title", key + ".4.subtitle");
        broadcastTitleAfterSeconds(15, "events.game-start.title", "events.game-start.subtitle");
    }

    public static void broadcastTitleAfterSeconds(int seconds, final String title, final String subtitle) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> broadcastTitle(title, subtitle, 2), seconds * 20L);
    }
}
