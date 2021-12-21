package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Messages;
import com.soradgaming.squidgame.arena.Status;
import com.soradgaming.squidgame.math.Cuboid;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RedLightGreenLight implements Runnable, Listener {
    private SquidGame plugin;
    private Arena arena;
    private boolean canWalk = true;
    private boolean Started = false;
    private final BukkitScheduler gameTimer = Bukkit.getScheduler();
    private final BukkitScheduler bossBarProgress = Bukkit.getScheduler();
    private final BukkitScheduler redLight = Bukkit.getScheduler();
    private final BukkitScheduler greenLight = Bukkit.getScheduler();
    private final BukkitScheduler delay = Bukkit.getScheduler();
    private BossBar bossBar;
    private double timerInterval;
    private int timeGlobal;
    private int max;
    private int min;
    private Cuboid killZone;
    private Cuboid goalZone;
    private Cuboid barrierZone;

    public RedLightGreenLight(SquidGame plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @Override
    public void run() {
        canWalk = true;
        max = plugin.getConfig().getInt("Game1.lightSwitchMax");
        min = plugin.getConfig().getInt("Game1.lightSwitchMin");
        timeGlobal = arena.getStructureManager().getTimeLimit(Games.Game1);
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBar = Bukkit.createBossBar(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds , BarColor.BLUE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(0);
        arena.getGameHandler().setPvPAllowed(false);
        arena.getGameHandler().setBlockAllowed(false);
        for (Block block : getBarrier().getBlocks()) {
            if (block.getType() == Material.AIR) {
                block.setType(Material.BARRIER);
            }
        }
        for (Player p : arena.getPlayerHandler().getAllPlayers()) {
            Objects.requireNonNull(p).teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Game1.spawn")));
            bossBar.addPlayer(Objects.requireNonNull(p));
        }
        Messages.onExplainStart(arena.getPlayerHandler().getAllPlayers(),"first");
        timerInterval = (1 / (double) timeGlobal);
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            gameTimer.runTaskLater(plugin, endGame1(), 20L * (timeGlobal + 1));
            bossBarProgress.runTaskTimer(plugin, bossBarProgress(), 20L, 20L);
            //START
            arena.getGameHandler().setPvPAllowed(true);
            for (Block block : getBarrier().getBlocks()) {
                if (block.getType() == Material.BARRIER) {
                    block.setType(Material.AIR);
                }
            }
            singDoll();
        }, 20L * 15);
    }

    private Runnable endGame1() {
        bossBarProgress.cancelTasks(plugin);
        gameTimer.cancelTasks(plugin);
        redLight.cancelTasks(plugin);
        greenLight.cancelTasks(plugin);
        delay.cancelTasks(plugin);
        if (Started) {
            bossBar.removeAll();
            bossBar.setVisible(false);
            Messages.broadcastTitle(arena.getPlayerHandler().getAlivePlayers(), "events.game-timeout.title", "events.game-timeout.subtitle", 5);
            Started = false;
            canWalk = true;
            List<Player> playerList = arena.getPlayerHandler().getAlivePlayers();
            arena.getGameHandler().setPvPAllowed(false);
            for (Player player : playerList) {
                Location location = Objects.requireNonNull(player).getLocation();
                if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
                    if (!getGoalZone().contains(location)) { //Player didn't make it to end in time
                        if (plugin.getConfig().getBoolean("eliminate-players")) {
                            player.setGameMode(GameMode.SPECTATOR);
                            player.teleport(plugin.getConfig().getLocation("Game1.spawn"));
                            arena.getPlayerHandler().killPlayer(player);
                        } else {
                            player.setGameMode(GameMode.SPECTATOR);
                            player.teleport(plugin.getConfig().getLocation("Game1.spawn"));
                        }
                    } else {
                        plugin.data.set(player.getUniqueId() + ".points", plugin.data.getInt(player.getUniqueId() + ".points") + 1);
                    }
                }
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (final Player player : arena.getPlayerHandler().getDeadPlayers()) {
                    Objects.requireNonNull(player).sendTitle(Messages.formatMessage(player,"events.game-timeout-died.title") , Messages.formatMessage(player,"events.game-timeout-died.subtitle"),10, 30,10);
                }
                for (final Player player : arena.getPlayerHandler().getAlivePlayers()) {
                    Objects.requireNonNull(player).sendTitle(Messages.formatMessage(player,"events.game-pass.title") , Messages.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
                }
            }, 40L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> arena.getGameHandler().intermission(Games.Game2), 20L * plugin.getConfig().getInt("endgame-time"));
        }
        return null;
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityDamage(final EntityDamageEvent e) {
        final Entity entity = e.getEntity();
        if (entity instanceof Player) {
            final Player player = ((Player) entity).getPlayer();
            if (player != null && arena.getPlayerHandler().getAllPlayers().contains(player)) {
                if (arena.getGameHandler().isPvPAllowed()) {
                    e.setDamage(0);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void PlayerMoveEvent(@NotNull PlayerMoveEvent e) {
        if (e.getFrom().distance(Objects.requireNonNull(e.getTo())) <= 0.015) {
            return;
        } else if (e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
            return;
        }
        Player player = e.getPlayer();
        if (arena.getPlayerHandler().getAlivePlayers().contains(player)) {
            if (!canWalk) {
                final Location location = e.getPlayer().getLocation();
                if (getKillZone().isBetween(location)) {
                    if (plugin.getConfig().getBoolean("eliminate-players")) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(plugin.getConfig().getLocation("Game1.spawn"));
                        arena.getPlayerHandler().killPlayer(player);
                    } else {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(plugin.getConfig().getLocation("Game1.spawn"));
                    }
                }
            }
        }
    }

    private Runnable singDoll() {
        if (!arena.getGameHandler().getStatus().equals(Status.Online)) {
            return null;
        }
        final int time = (int) Math.floor(Math.random()*(max-min+1)+min);
        Messages.broadcastTitle(arena.getPlayerHandler().getAllPlayers(),"games.first.green-light.title", "games.first.green-light.subtitle",time);
        canWalk = true;
        redLight.runTaskLater(plugin, () -> {
            final int waitTime = (int) Math.floor(Math.random()*(max-min+1)+min);
            Messages.broadcastTitle(arena.getPlayerHandler().getAllPlayers(),"games.first.red-light.title", "games.first.red-light.subtitle", waitTime);
            delay.runTaskLater(plugin, () -> {
                canWalk = false;
                greenLight.runTaskLater(plugin, singDoll(), waitTime * 20L);
            }, 20);
        }, time * 20L);
        return null;
    }

    private Runnable bossBarProgress() {
        double bossBarProgress = bossBar.getProgress();
        if (bossBarProgress + timerInterval < 1) {
            bossBar.setProgress(bossBarProgress + timerInterval);
        }
        timeGlobal = timeGlobal - 1;
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBar.setTitle(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds);
        return null;
    }

    private Cuboid getBarrier() {
        if (barrierZone == null) {
            BlockVector vector1 = arena.getStructureManager().configToVectors("Game1.barrier.first_point");
            BlockVector vector2 = arena.getStructureManager().configToVectors("Game1.barrier.second_point");
            World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game1.world")));
            barrierZone = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
        }
        return barrierZone;
    }

    private Cuboid getKillZone() {
        if (killZone == null) {
            BlockVector vector1 = arena.getStructureManager().configToVectors("Game1.killzone.first_point");
            BlockVector vector2 = arena.getStructureManager().configToVectors("Game1.killzone.second_point");
            World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game1.world")));
            killZone = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
        }
        return killZone;
    }

    private Cuboid getGoalZone() {
        if (goalZone == null) {
            BlockVector vector1 = arena.getStructureManager().configToVectors("Game1.goal.first_point");
            BlockVector vector2 = arena.getStructureManager().configToVectors("Game1.goal.second_point");
            World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game1.world")));
            goalZone = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
        }
        return goalZone;
    }

}
