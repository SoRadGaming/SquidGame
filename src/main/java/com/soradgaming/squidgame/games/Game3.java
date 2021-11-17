package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.utils.gameManager;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Game3 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    private static ArrayList<UUID> playerList;
    private static boolean Started = false;
    private static final BukkitScheduler gameTimer = Bukkit.getScheduler();
    private static final BukkitScheduler lightsOff = Bukkit.getScheduler();
    private static final BukkitScheduler lightsOn = Bukkit.getScheduler();
    private static final BukkitScheduler delay = Bukkit.getScheduler();
    private static final BukkitScheduler bossBarProgress = Bukkit.getScheduler();
    private static BossBar bossBar;
    private static double timerInterval;
    public static int timeGlobal;

    public static void startGame3(ArrayList<UUID> input) {
        playerList = input;
        Started = true;
        onExplainStart("third");
        timeGlobal = plugin.getConfig().getInt("Game3.timer");
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBar = Bukkit.createBossBar(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds , BarColor.BLUE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(0);
        timerInterval = (1 / (double) timeGlobal);
        Bukkit.getScheduler().runTaskAsynchronously(plugin,() -> {
            for (UUID uuid:playerList) {
            Player player = Bukkit.getPlayer(uuid);
            bossBar.addPlayer(Objects.requireNonNull(player));
            }
        });
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            gameTimer.runTaskLater(plugin, Game3::endGame3, 20L * (plugin.getConfig().getInt("Game3.timer") + 1));
            bossBarProgress.runTaskTimer(plugin, Game3::bossBarProgress, 20L, 20L);
            //START
            for (UUID uuid:playerList) {
                Player player = Bukkit.getPlayer(uuid);
                bossBar.addPlayer(Objects.requireNonNull(player));
                player.getInventory().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD));
            }
            //TODO Set PVP ON
            flashLights();
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

    public static void endGame3() {
        gameTimer.cancelTasks(plugin);
        lightsOn.cancelTasks(plugin);
        lightsOff.cancelTasks(plugin);
        delay.cancelTasks(plugin);
        if (Started) {
            //TODO Set PVP off
            for (UUID uuid : gameManager.getAlivePlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                Objects.requireNonNull(player).setHealth(20);
                player.getInventory().clear();
                player.setFoodLevel(20);
                player.getActivePotionEffects().clear();
                player.sendTitle(gameManager.formatMessage(player,"events.game-pass.title") , gameManager.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            }
            //TODO end code
            Game4.startGame4(gameManager.getAllPlayers());
        }
    }

    private static void flashLights() {
        if (!Started) {
            return;
        }
        int timeOn = plugin.getConfig().getInt("Game3.lightSwitchOn");
        int timeOff = plugin.getConfig().getInt("Game3.lightSwitchOff");
        for (UUID uuid: playerList) {
            Objects.requireNonNull(Bukkit.getPlayer(uuid)).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, timeOff * 20, 1, false, false));
        }
        lightsOff.runTaskLater(plugin, () -> {
            for (UUID uuid: playerList) {
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, timeOn * 20, 1, false, false));
            }
            lightsOn.runTaskLater(plugin, Game3::flashLights, timeOff * 20L);
        },  timeOn * 20L);
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

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Player player = e.getEntity();

        if (Started && playerList.contains(player.getUniqueId()) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
            gameManager.killPlayer(player);
            player.setGameMode(GameMode.SPECTATOR);
        }
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
