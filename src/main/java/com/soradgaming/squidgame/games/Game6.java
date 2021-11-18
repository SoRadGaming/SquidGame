package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.math.Cuboid;
import com.soradgaming.squidgame.math.Generator;
import com.soradgaming.squidgame.utils.gameManager;
import com.soradgaming.squidgame.utils.playerManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Game6 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    private static ArrayList<UUID> playerList;
    private static boolean Started = false;
    private static final BukkitScheduler gameTimer = Bukkit.getScheduler();
    private static final BukkitScheduler bossBarProgress = Bukkit.getScheduler();
    private static BossBar bossBar;
    private static double timerInterval;
    public static int timeGlobal;
    private static Cuboid glassZone;
    private static Cuboid goalZone;
    private static ArrayList<Block> fakeBlocks = new ArrayList<>();
    private static ArrayList<Cuboid> fakeCuboids = new ArrayList<>();

    public static void startGame6(ArrayList<UUID> input) {
        playerList = input;
        Started = true;
        timeGlobal = plugin.getConfig().getInt("Game6.timer");
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBar = Bukkit.createBossBar(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds , BarColor.BLUE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(0);
        //Bukkit.getScheduler().runTaskAsynchronously(plugin,() -> {
            Generator.generateTiles(Material.valueOf(plugin.getConfig().getString("Game6.material")), playerList.size());
            fakeBlocks = Generator.getFakeBlocks();
            fakeCuboids = Generator.getFakeCuboids();
        //});
        for (UUID uuid : playerList) {
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
            gameManager.setPvPAllowed(true);
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

    public static void endGame6() {
        gameTimer.cancelTasks(plugin);
        bossBarProgress.cancelTasks(plugin);
        if (Started) {
            bossBar.removeAll();
            bossBar.setVisible(false);
            gameManager.broadcastTitle("events.game-timeout.title", "events.game-timeout.subtitle", 5);
            Started = false;
            gameManager.setPvPAllowed(false);
            for (UUID value : gameManager.getAlivePlayers()) {
                Player player = Bukkit.getPlayer(value);
                Location location = Objects.requireNonNull(player).getLocation();
                if (!getGoalZone().contains(location)) { //Player didn't make it to end in time
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
                    Objects.requireNonNull(player).teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Lobby")));
                }
                for (final UUID uuid : gameManager.getAlivePlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    Objects.requireNonNull(player).sendTitle(gameManager.formatMessage(player,"events.game-pass.title") , gameManager.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
                    Objects.requireNonNull(player).teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Lobby")));
                }
            }, 40L);
            //TODO end code
            for (UUID uuid: gameManager.getAllPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                playerManager.playerLeave(player);
                player.sendMessage("Game Done");
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void PlayerMoveEvent(@NotNull PlayerMoveEvent e) {
        if (e.getFrom().distance(Objects.requireNonNull(e.getTo())) <= 0.015) {
            return;
        }
        if (!Started) {
            return;
        }
        Player player = e.getPlayer();
        if (!playerList.contains(player.getUniqueId())) {
            return;
        }
        if (e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
            return;
        }

        final Location location = Objects.requireNonNull(e.getTo()).clone().subtract(0, 1, 0);
        final Block block = location.getBlock();

        if (block.getType() == Material.valueOf(plugin.getConfig().getString("Game6.material")) && fakeBlocks.contains(block)) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin,() -> {
                for (Cuboid cuboid: fakeCuboids) {
                    if (cuboid.contains(location)) {
                        for (Block blocks : cuboid.getBlocks()) {
                            blocks.setType(Material.AIR);
                        }
                    }
                }
            });
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Player player = e.getEntity();
        if (!player.getGameMode().equals(GameMode.SPECTATOR) && Started && playerList.contains(player.getUniqueId())) {
            if (plugin.getConfig().getBoolean("eliminate-players")) {
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(plugin.getConfig().getLocation("Game6.spawn"));
                gameManager.killPlayer(player);
            } else {
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(plugin.getConfig().getLocation("Game6.spawn"));
            }
        }
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

    public static Cuboid getGoalZone() {
        if (goalZone == null) {
            BlockVector vector1 = gameManager.configToVectors("Game6.goal.first_point");
            BlockVector vector2 = gameManager.configToVectors("Game6.goal.second_point");
            World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game6.world")));
            goalZone = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
        }

        return goalZone;
    }
}
