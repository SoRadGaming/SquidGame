package com.soradgaming.squidgame.utils;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.games.Game1;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class playerManager implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    public static boolean gameStarted = false;
    //Store Player Stats
    public static HashMap<Player, ItemStack[]> playerInv = new HashMap<>();
    public static HashMap<Player, ItemStack[]> playerArmour = new HashMap<>();
    public static HashMap<Player, Collection<PotionEffect>> playerEffects = new HashMap<>();
    public static HashMap<Player, Location> last_location = new HashMap<>();
    public static HashMap<Player, GameMode> gamemode = new HashMap<>();
    public static HashMap<Player, Double> healthScale = new HashMap<>();
    public static HashMap<Player, Double> health = new HashMap<>();
    public static HashMap<Player, Integer> level = new HashMap<>();
    public static HashMap<Player, Float> xp = new HashMap<>();
    public static HashMap<Player, Location> bedSpawn = new HashMap<>();
    public static HashMap<Player, Integer> foodLevel = new HashMap<>();


    public static void playerBracket(@NotNull ArrayList<UUID> input) {
        //Active Players

        for (UUID uuid : input) {
            //Data Create if Never Player Before
            if (!plugin.data.contains(uuid + ".wins")) {
                plugin.data.set(uuid + ".wins", 0);
            }
            //Save Data
            plugin.saveFile();
        }
    }

    public static boolean playerJoin(Player player) {
        if (gameManager.getAlivePlayers().size() <= plugin.getConfig().getInt("max-players") && gameManager.addPlayer(Objects.requireNonNull(player)) && !gameStarted) {
            gameManager.revivePlayer(player);
            plugin.data.set("join",player.getUniqueId().toString());
            playerManager.checkStart();
            for (UUID uuid : gameManager.getAllPlayers()) {
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(gameManager.formatMessage(player,"arena.join"));
            }
            //Save Stats
            playerInv.put(player, player.getInventory().getContents());
            playerArmour.put(player, player.getInventory().getArmorContents());
            playerEffects.put(player, player.getActivePotionEffects());
            last_location.put(player, player.getLocation());
            gamemode.put(player, player.getGameMode());
            healthScale.put(player,player.getHealthScale());
            health.put(player, player.getHealth());
            level.put(player, player.getLevel());
            xp.put(player,player.getExp());
            bedSpawn.put(player,player.getBedSpawnLocation());
            foodLevel.put(player,player.getFoodLevel());
            //SetNewStats
            player.teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Lobby")));
            player.setGameMode(GameMode.ADVENTURE);
            player.setHealthScale(20);
            player.setHealth(20);
            player.setLevel(0);
            player.setExp(0);
            player.setBedSpawnLocation(plugin.getConfig().getLocation("Lobby"));
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            return true;
        }
        return false;
    }

    public static boolean playerLeave(Player player) {
        if (gameManager.removePlayer(Objects.requireNonNull(player)) || gameManager.revivePlayer(player)) {
            plugin.data.set("leave",player.getUniqueId().toString());
            for (UUID uuid : gameManager.getAllPlayers()) {
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(gameManager.formatMessage(player,"arena.leave"));
            }
            //Give Old Stats Back
            player.teleport(last_location.get(player));
            player.setGameMode(gamemode.get(player));
            player.setHealthScale(healthScale.get(player));
            player.setHealth(health.get(player));
            player.setLevel(level.get(player));
            player.setExp(xp.get(player));
            player.setBedSpawnLocation(bedSpawn.get(player));
            player.setFoodLevel(foodLevel.get(player));
            player.getInventory().setContents(playerInv.get(player));
            player.getInventory().setArmorContents(playerArmour.get(player));
            player.addPotionEffects(playerEffects.get(player));
            return true;
        }
        return false;
    }

    public static boolean checkStart() {
        int min = plugin.getConfig().getInt("min-players");
        BukkitScheduler gameStartTask = Bukkit.getScheduler();
        if (gameManager.getAlivePlayers().size() >= min && !gameStarted) {
            gameStartTask.runTaskLater(plugin, () -> {
                gameManager.Initialise();
                Game1.startGame1(gameManager.getAllPlayers());
                for (UUID uuid : gameManager.getAllPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    Objects.requireNonNull(player).sendMessage(gameManager.formatMessage(player, "arena.started"));
                }
                gameStarted = true;
                }, 20L * plugin.getConfig().getInt("start-time"));
                //Message Starting
            for (UUID uuid : gameManager.getAllPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                Objects.requireNonNull(player).sendMessage(gameManager.formatMessage(player, "arena.starting"));
            }
            return true;
        } else {
            gameStartTask.cancelTasks(plugin);
            return false;
        }
    }
}
