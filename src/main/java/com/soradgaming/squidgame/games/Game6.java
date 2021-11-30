package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.math.Cuboid;
import com.soradgaming.squidgame.math.Generator;
import com.soradgaming.squidgame.math.BlockUtils;
import com.soradgaming.squidgame.utils.gameManager;
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

import java.util.*;

public class Game6 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    private static boolean Started = false;
    private static final BukkitScheduler gameTimer = Bukkit.getScheduler();
    private static final BukkitScheduler bossBarProgress = Bukkit.getScheduler();
    private static BossBar bossBar;
    private static double timerInterval;
    public static int timeGlobal;
    private static Cuboid glassZone;
    private static Cuboid goalZone;
    private static Cuboid barrierZone;
    private static ArrayList<Block> fakeBlocks;
    private static ArrayList<Cuboid> fakeCuboids;
    private static ArrayList<UUID> playersList = new ArrayList<>();

    public static void startGame6() {
        Started = true;
        timeGlobal = plugin.getConfig().getInt("Game6.timer");
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBar = Bukkit.createBossBar(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds , BarColor.BLUE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(0);
        Generator.generateTiles(Material.valueOf(plugin.getConfig().getString("Game6.material")), gameManager.getAlivePlayers().size());
        for (Block block : getBarrier().getBlocks()) {
            if (block.getType() == Material.AIR) {
                block.setType(Material.BARRIER);
            }
        }
        for (UUID uuid : gameManager.getAllPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            Objects.requireNonNull(p).teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Game6.spawn")));
            bossBar.addPlayer(Objects.requireNonNull(p));
        }
        gameManager.onExplainStart("sixth");
        timerInterval = (1 / (double) timeGlobal);
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            gameTimer.runTaskLater(plugin, Game6::endGame6, 20L * (timeGlobal + 1));
            bossBarProgress.runTaskTimer(plugin, Game6::bossBarProgress, 20L, 20L);
            //START
            for (Block block : getBarrier().getBlocks()) {
                if (block.getType() == Material.BARRIER) {
                    block.setType(Material.AIR);
                }
            }
            gameManager.setPvPAllowed(true);
        }, 20L * 15);
    }

    public static void endGame6() {
        gameTimer.cancelTasks(plugin);
        bossBarProgress.cancelTasks(plugin);
        if (Started) {
            bossBar.removeAll();
            bossBar.setVisible(false);
            playersList = gameManager.getAlivePlayers();
            gameManager.broadcastTitle("events.game-timeout.title", "events.game-timeout.subtitle", 5);
            Started = false;
            gameManager.setPvPAllowed(false);
            for (UUID value : playersList) {
                Player player = Bukkit.getPlayer(value);
                Location location = Objects.requireNonNull(player).getLocation();
                if (!getGoalZone().isBetween(location)) { //Player didn't make it to end in time
                    if (plugin.getConfig().getBoolean("eliminate-players")) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(plugin.getConfig().getLocation("Game6.spawn"));
                        gameManager.killPlayer(player);
                    } else {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(plugin.getConfig().getLocation("Game6.spawn"));
                    }
                } else {
                    plugin.data.set(player.getUniqueId() + ".points", plugin.data.getInt(player.getUniqueId() + ".points") + 1);
                }
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (final UUID uuid : gameManager.getDeadPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    Objects.requireNonNull(player).sendTitle(gameManager.formatMessage(player,"events.game-timeout-died.title") , gameManager.formatMessage(player,"events.game-timeout-died.subtitle"),10, 30,10);
                }
                for (final UUID uuid : gameManager.getAlivePlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    Objects.requireNonNull(player).sendTitle(gameManager.formatMessage(player,"events.game-pass.title") , gameManager.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
                }
            }, 40L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> gameManager.intermission(Games.Game7), 20L * plugin.getConfig().getInt("endgame-time"));
        }
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityDamage(final EntityDamageEvent e) {
        final Entity entity = e.getEntity();
        if (entity instanceof Player) {
            final Player player = ((Player) entity).getPlayer();
            if (player != null && gameManager.getAllPlayers().contains(player.getUniqueId())) {
                if (Started) {
                    //player dies
                    if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && !gameManager.isPvPAllowed()) {
                        e.setDamage(0);
                        e.setCancelled(true);
                    } else if (e.getCause() == EntityDamageEvent.DamageCause.FALL && !gameManager.isPvPAllowed()) {
                        e.setDamage(0);
                        onPlayerDeathFall(player);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void PlayerMoveEvent(@NotNull PlayerMoveEvent e) {
        if (e.getFrom().distance(Objects.requireNonNull(e.getTo())) <= 0.015) {
            return;
        }
        Player player = e.getPlayer();

        if (!gameManager.getAlivePlayers().contains(player.getUniqueId()) || !Started) {
            return;
        }

        final Location location = Objects.requireNonNull(e.getTo()).clone().subtract(0, 1, 0);
        final Block block = location.getBlock();

        if (block != null && block.getType() == Material.valueOf(plugin.getConfig().getString("Game6.material"))) {
            if (getFakeBlocks().contains(block)) {
                BlockUtils.destroyBlockGroup(location.getBlock(), true);
            }
        }
    }

    public static void onPlayerDeathFall(Player player) {
        if (!player.getGameMode().equals(GameMode.SPECTATOR) && Started && gameManager.getAllPlayers().contains(player.getUniqueId())) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(plugin.getConfig().getLocation("Game6.spawn"));
            gameManager.killPlayer(player);
        }
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

    public static Cuboid getGlassZone() {
        if (glassZone == null) {
            BlockVector vector1 = gameManager.configToVectors("Game6.glass.first_point");
            BlockVector vector2 = gameManager.configToVectors("Game6.glass.second_point");
            World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game6.world")));
            glassZone = new Cuboid(Objects.requireNonNull(world),vector1.toBlockVector(),vector2.toBlockVector());
        }
        return glassZone;
    }

    private static Cuboid getGoalZone() {
        if (goalZone == null) {
            BlockVector vector1 = gameManager.configToVectors("Game6.goal.first_point");
            BlockVector vector2 = gameManager.configToVectors("Game6.goal.second_point");
            World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game6.world")));
            goalZone = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
        }
        return goalZone;
    }

    public static Cuboid getBarrier() {
        if (barrierZone == null) {
            BlockVector vector1 = gameManager.configToVectors("Game6.barrier.first_point");
            BlockVector vector2 = gameManager.configToVectors("Game6.barrier.second_point");
            World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game6.world")));
            barrierZone = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
        }
        return barrierZone;
    }

    public static ArrayList<Cuboid> getFakeCuboids() {
        if (fakeCuboids == null) {
            fakeCuboids = Generator.getFakeCuboids();
        }
        return fakeCuboids;
    }

    public static ArrayList<Block> getFakeBlocks() {
        if (fakeBlocks == null) {
            fakeBlocks = Generator.getFakeBlocks();
        }
        return fakeBlocks;
    }

    public static boolean isStarted() {
        return Started;
    }

    public static void reloadConfig() {
        fakeBlocks = null;
        fakeCuboids = null;
        barrierZone = null;
        glassZone = null;
        goalZone = null;
    }
}
