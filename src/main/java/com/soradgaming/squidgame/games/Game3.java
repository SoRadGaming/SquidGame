package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.utils.gameManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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

    public static void startGame3(ArrayList<UUID> input) {
        playerList = input;
        Started = true;
        onExplainStart("third");
        for (UUID uuid:playerList) {
            Player player = Bukkit.getPlayer(uuid);
            //TODO give item (wooden sword)
        }
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            gameTimer.runTaskLater(plugin, Game3::endGame3, 20L * (plugin.getConfig().getInt("Game3.timer") + 1));
            //START
            //TODO Set PVP ON
            flashLights();
        }, 20L * 15);
    }

    public static void endGame3() {
        gameTimer.cancelTasks(plugin);
        lightsOn.cancelTasks(plugin);
        lightsOff.cancelTasks(plugin);
        delay.cancelTasks(plugin);
        if (Started) {
            //TODO Set PVP off
            for (UUID uuid: playerList) {
                Player player = Bukkit.getPlayer(uuid);
                Objects.requireNonNull(player).setHealth(20);
                player.setFoodLevel(20);
            }
            for (UUID uuid : gameManager.getPlayerList()) {
                Player player = Bukkit.getPlayer(uuid);
                Objects.requireNonNull(player).sendTitle(gameManager.formatMessage(player,"events.game-pass.title") , gameManager.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            }
            //TODO end code
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
        lightsOff.runTaskLater(plugin, () -> delay.runTaskLater(plugin, () -> {
            for (UUID uuid: playerList) {
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, timeOn * 20, 1, false, false));
            }
            lightsOn.runTaskLater(plugin, Game3::flashLights, timeOff * 20L);
        }, 20), timeOn * 20L);
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

        if (playerList.contains(player.getUniqueId()) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
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
