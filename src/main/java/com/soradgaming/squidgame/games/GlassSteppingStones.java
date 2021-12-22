package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Messages;
import com.soradgaming.squidgame.math.BlockUtils;
import com.soradgaming.squidgame.math.Cuboid;
import com.soradgaming.squidgame.math.Generator;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GlassSteppingStones implements Runnable, Listener {
    private SquidGame plugin;
    private Arena arena;
    private Generator generator;
    private boolean Started = false;
    private BukkitScheduler gameTimer = Bukkit.getScheduler();
    private BukkitScheduler bossBarProgress = Bukkit.getScheduler();
    private BossBar bossBar;
    private double timerInterval;
    public int timeGlobal;
    private Cuboid glassZone;
    private Cuboid goalZone;
    private Cuboid barrierZone;
    private ArrayList<Block> fakeBlocks;
    private ArrayList<Cuboid> fakeCuboids;

    public GlassSteppingStones(SquidGame plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @Override
    public void run() {
        Started = true;
        timeGlobal = arena.getStructureManager().getTimeLimit(Games.Game6);
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBar = Bukkit.createBossBar(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds , BarColor.BLUE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(0);
        generator = new Generator(this);
        generator.generateTiles(Material.valueOf(plugin.getConfig().getString("Game6.material")), arena.getPlayerHandler().getAlivePlayers().size());

        for (Block block : getBarrier().getBlocks()) {
            if (block.getType() == Material.AIR) {
                block.setType(Material.BARRIER);
            }
        }
        for (Player p : arena.getPlayerHandler().getAllPlayers()) {
            Objects.requireNonNull(p).teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Game6.spawn")));
            bossBar.addPlayer(Objects.requireNonNull(p));
        }
        Messages.onExplainStart(arena.getPlayerHandler().getAllPlayers(), "sixth");
        timerInterval = (1 / (double) timeGlobal);
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            gameTimer.runTaskLater(plugin, endGame6(), 20L * (timeGlobal + 1));
            bossBarProgress.runTaskTimer(plugin, bossBarProgress(), 20L, 20L);
            //START
            for (Block block : getBarrier().getBlocks()) {
                if (block.getType() == Material.BARRIER) {
                    block.setType(Material.AIR);
                }
            }
            arena.getGameHandler().setPvPAllowed(true);
        }, 20L * 15);
    }

    public Runnable endGame6() {
        gameTimer.cancelTasks(plugin);
        bossBarProgress.cancelTasks(plugin);
        if (Started) {
            bossBar.removeAll();
            bossBar.setVisible(false);
            List<Player> playersList = arena.getPlayerHandler().getAlivePlayers();
            Messages.broadcastTitle(playersList,"events.game-timeout.title", "events.game-timeout.subtitle", 5);
            Started = false;
            arena.getGameHandler().setPvPAllowed(false);
            for (Player player : playersList) {
                Location location = Objects.requireNonNull(player).getLocation();
                if (!getGoalZone().isBetween(location)) { //Player didn't make it to end in time
                    Location spawn = arena.getStructureManager().getSpawn(Games.Game6);
                    if (plugin.getConfig().getBoolean("eliminate-players")) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(spawn);
                        arena.getPlayerHandler().killPlayer(player);
                    } else {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(spawn);
                    }
                } else {
                    plugin.data.set(player.getUniqueId() + ".points", plugin.data.getInt(player.getUniqueId() + ".points") + 1);
                }
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (Player player : arena.getPlayerHandler().getDeadPlayers()) {
                    Objects.requireNonNull(player).sendTitle(Messages.formatMessage(player,"events.game-timeout-died.title") , Messages.formatMessage(player,"events.game-timeout-died.subtitle"),10, 30,10);
                }
                for (Player player : arena.getPlayerHandler().getAlivePlayers()) {
                    Objects.requireNonNull(player).sendTitle(Messages.formatMessage(player,"events.game-pass.title") , Messages.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
                }
            }, 40L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> arena.getGameHandler().intermission(Games.Game7), 20L * arena.getStructureManager().getEndTime());
        }
        return null;
    }

    @EventHandler(ignoreCancelled = true)
    private void onEntityDamage(final EntityDamageEvent e) {
        final Entity entity = e.getEntity();
        if (entity instanceof Player) {
            final Player player = ((Player) entity).getPlayer();
            if (player != null && arena.getPlayerHandler().getAllPlayers().contains(player)) {
                if (Started) {
                    //player dies
                    if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && !arena.getGameHandler().isPvPAllowed()) {
                        e.setDamage(0);
                        e.setCancelled(true);
                    } else if (e.getCause() == EntityDamageEvent.DamageCause.FALL && !arena.getGameHandler().isPvPAllowed()) {
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

        if (!arena.getPlayerHandler().getAlivePlayers().contains(player) || !Started) {
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

    public void onPlayerDeathFall(Player player) {
        if (!player.getGameMode().equals(GameMode.SPECTATOR) && Started && arena.getPlayerHandler().getAllPlayers().contains(player)) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(plugin.getConfig().getLocation("Game6.spawn"));
            arena.getPlayerHandler().killPlayer(player);
        }
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

    public Cuboid getGlassZone() {
        if (glassZone == null) {
            BlockVector vector1 = arena.getStructureManager().configToVectors("Game6.glass.first_point");
            BlockVector vector2 = arena.getStructureManager().configToVectors("Game6.glass.second_point");
            World world = arena.getStructureManager().getWorld(Games.Game6);
            glassZone = new Cuboid(Objects.requireNonNull(world),vector1.toBlockVector(),vector2.toBlockVector());
        }
        return glassZone;
    }

    private Cuboid getGoalZone() {
        if (goalZone == null) {
            BlockVector vector1 = arena.getStructureManager().configToVectors("Game6.goal.first_point");
            BlockVector vector2 = arena.getStructureManager().configToVectors("Game6.goal.second_point");
            World world = arena.getStructureManager().getWorld(Games.Game6);
            goalZone = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
        }
        return goalZone;
    }

    public Cuboid getBarrier() {
        if (barrierZone == null) {
            BlockVector vector1 = arena.getStructureManager().configToVectors("Game6.barrier.first_point");
            BlockVector vector2 = arena.getStructureManager().configToVectors("Game6.barrier.second_point");
            World world = arena.getStructureManager().getWorld(Games.Game6);
            barrierZone = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
        }
        return barrierZone;
    }

    public ArrayList<Cuboid> getFakeCuboids() {
        if (fakeCuboids == null) {
            fakeCuboids = generator.getFakeCuboids();
        }
        return fakeCuboids;
    }

    public ArrayList<Block> getFakeBlocks() {
        if (fakeBlocks == null) {
            fakeBlocks = generator.getFakeBlocks();
        }
        return fakeBlocks;
    }

    public boolean isStarted() {
        return Started;
    }
}
