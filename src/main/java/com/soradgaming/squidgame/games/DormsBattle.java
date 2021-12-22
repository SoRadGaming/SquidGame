package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

public class DormsBattle implements Runnable {
    private SquidGame plugin;
    private Arena arena;
    private boolean Started = false;
    private BukkitScheduler gameTimer = Bukkit.getScheduler();
    private BukkitScheduler lightsOff = Bukkit.getScheduler();
    private BukkitScheduler lightsOn = Bukkit.getScheduler();
    private BukkitScheduler delay = Bukkit.getScheduler();
    private BukkitScheduler bossBarProgress = Bukkit.getScheduler();
    private BossBar bossBar;
    private double timerInterval;
    private int timeGlobal;
    private int timeOn;
    private int timeOff;

    public DormsBattle(SquidGame plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @Override
    public void run() {
        Started = true;
        Messages.onExplainStart(arena.getPlayerHandler().getAlivePlayers(), "third");
        timeGlobal = arena.getStructureManager().getTimeLimit(Games.Game3);
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBar = Bukkit.createBossBar(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds , BarColor.BLUE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(0);
        timerInterval = (1 / (double) timeGlobal);
        timeOn = arena.getStructureManager().getLightSwitchOn();
        timeOff = arena.getStructureManager().getLightSwitchOff();
        for (Player player: arena.getPlayerHandler().getAllPlayers()) {
            player.teleport(plugin.getConfig().getLocation("Lobby"));
            bossBar.addPlayer(player);
        }
        for (Player player:arena.getPlayerHandler().getAlivePlayers()) {
            player.setGameMode(GameMode.ADVENTURE);
        }
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            gameTimer.runTaskLater(plugin, endGame3(), 20L * (timeGlobal + 1));
            bossBarProgress.runTaskTimer(plugin, bossBarProgress(), 20L, 20L);
            //START
            for (Player player:arena.getPlayerHandler().getAlivePlayers()) {
                player.getInventory().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD));
            }
            arena.getGameHandler().setPvPAllowed(true);
            flashLights();
        }, 20L * 15);
    }

    public Runnable endGame3() {
        gameTimer.cancelTasks(plugin);
        lightsOn.cancelTasks(plugin);
        lightsOff.cancelTasks(plugin);
        delay.cancelTasks(plugin);
        if (Started) {
            Started = false;
            bossBar.removeAll();
            bossBar.setVisible(false);
            arena.getGameHandler().setPvPAllowed(false);
            for (Player player : arena.getPlayerHandler().getAlivePlayers()) {
                player.setHealth(20);
                player.getInventory().clear();
                player.setFoodLevel(20);
                player.getActivePotionEffects().clear();
                player.sendTitle(Messages.formatMessage(player,"events.game-pass.title") , Messages.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> arena.getGameHandler().intermission(Games.Game4), 20L * arena.getStructureManager().getEndTime());
        }
        return null;
    }

    private Runnable flashLights() {
        if (!Started) {
            return null;
        }
        for (Player player: arena.getPlayerHandler().getAllPlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, timeOff * 20, 1, false, false));
        }
        lightsOff.runTaskLater(plugin, () -> {
            for (Player player: arena.getPlayerHandler().getAllPlayers()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, timeOn * 20, 1, false, false));
            }
            lightsOn.runTaskLater(plugin, flashLights(), timeOff * 20L);
        },  timeOn * 20L);
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

    @EventHandler(ignoreCancelled = true)
    private void onEntityDamage(final EntityDamageEvent e) {
        final Entity entity = e.getEntity();
        if (entity instanceof Player) {
            final Player player = ((Player) entity).getPlayer();
            if (player != null && arena.getPlayerHandler().getAllPlayers().contains(player)) {
                if (player.getHealth() - e.getDamage() <= 0) {
                    //player dies
                    if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK && !arena.getGameHandler().isPvPAllowed()) {
                        if (Started) {
                            e.setDamage(0);
                            e.setCancelled(true);
                        }
                    } else if (e.getCause() == EntityDamageEvent.DamageCause.FALL && !arena.getGameHandler().isPvPAllowed()) {
                        if (Started) {
                            e.setDamage(0);
                            onPlayerDeathKilled(player);
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    public void onPlayerDeathKilled(Player player) {
        if (!player.getGameMode().equals(GameMode.SPECTATOR) && Started && arena.getPlayerHandler().getAllPlayers().contains(player)) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(plugin.getConfig().getLocation("Lobby"));
            arena.getPlayerHandler().killPlayer(player);
        }
    }

    public boolean isStarted() {return Started;}
}
