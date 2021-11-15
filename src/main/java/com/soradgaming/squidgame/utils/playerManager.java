package com.soradgaming.squidgame.utils;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.games.Game1;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class playerManager implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    public static boolean gameStarted = false;
    //Store Player Stats
    public static HashMap<UUID, PlayerInventory> playerInv = new HashMap<>();
    public static HashMap<UUID, Collection<PotionEffect>> playerEffects = new HashMap<>();

    //Start command
    public static void playerBracket(@NotNull ArrayList<UUID> input) {
        //Active Players

        for (UUID uuid : input) {
            Player p = Bukkit.getPlayer(uuid);
            //Data
            if (plugin.data.getInt(uuid + ".wins") >= 0) {
                plugin.data.set(uuid + ".wins", 0);
            }
            //Save Data
            plugin.saveFile();
        }
    }

    public static boolean playerJoin(Player player) {
        if (gameManager.getPlayerList().size() <= plugin.getConfig().getInt("max-players") && gameManager.addPlayer(Objects.requireNonNull(player)) && !gameStarted) {
            gameManager.revivePlayer(player);
            plugin.data.set("join",player.getUniqueId().toString());
            playerManager.checkStart();
            for (UUID uuid : gameManager.getPlayerList()) {
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(gameManager.formatMessage(player,"arena.join"));
            }
            //New System
            playerInv.put(player.getUniqueId(), player.getInventory());
            playerEffects.put(player.getUniqueId(), player.getActivePotionEffects());

            //Old System
            plugin.data.set(player.getUniqueId() + ".last_location",player.getLocation());
            plugin.data.set(player.getUniqueId() + ".gamemode", player.getGameMode().toString());
            plugin.data.set(player.getUniqueId() + ".healthScale", player.getHealthScale());
            plugin.data.set(player.getUniqueId() + ".health", player.getHealth());
            plugin.data.set(player.getUniqueId() + ".level", player.getLevel());
            plugin.data.set(player.getUniqueId() + ".xp", player.getExp());
            plugin.data.set(player.getUniqueId() + ".bedSpawn", player.getBedSpawnLocation());
            plugin.data.set(player.getUniqueId() + ".foodLevel", player.getFoodLevel());
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
            player.getActivePotionEffects().clear();
            return true;
        }
        return false;
    }

    public static boolean playerLeave(Player player) {
        if (gameManager.removePlayer(Objects.requireNonNull(player)) && !gameStarted) {
            gameManager.revivePlayer(player);
            playerManager.checkStart();
            plugin.data.set("leave",player.getUniqueId().toString());
            for (UUID uuid : gameManager.getPlayerList()) {
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage(gameManager.formatMessage(player,"arena.leave"));
            }
            player.teleport(Objects.requireNonNull(plugin.data.getLocation(player.getUniqueId() + ".last_location")));
            player.setGameMode(GameMode.valueOf(plugin.data.getString(player.getUniqueId() + ".gamemode")));
            player.setHealthScale(plugin.data.getDouble(player.getUniqueId() + ".healthScale"));
            player.setHealth(plugin.data.getDouble(player.getUniqueId() + ".health"));
            player.setLevel(plugin.data.getInt(player.getUniqueId() + ".level"));
            player.setExp((float) plugin.data.getDouble(player.getUniqueId() + ".xp"));
            player.setBedSpawnLocation(plugin.data.getLocation(player.getUniqueId() + ".bedSpawn"));
            player.setFoodLevel(plugin.data.getInt(player.getUniqueId() + ".foodLevel"));
            for (ItemStack inv :playerInv.get(player.getUniqueId())) {
                if (inv != null) {
                    player.getInventory().addItem(inv);
                }
            }
            for (PotionEffect effect :playerEffects.get(player.getUniqueId())) {
                if (effect != null) {
                    player.addPotionEffect(effect);
                }
            }
            return true;
        }
        return false;
    }

    public static boolean checkStart() {
        int min = plugin.getConfig().getInt("min-players");
        BukkitScheduler gameStartTask = Bukkit.getScheduler();
        if (gameManager.getPlayerList().size() >= min && !gameStarted) {
            gameStartTask.runTaskLater(plugin, () -> {
                gameManager.Initialise();
                Game1.startGame1(gameManager.getPlayerList());
                for (UUID uuid : gameManager.getPlayerList()) {
                    Player player = Bukkit.getPlayer(uuid);
                    Objects.requireNonNull(player).sendMessage(gameManager.formatMessage(player, "arena.started"));
                }
                gameStarted = true;
                }, 20L * plugin.getConfig().getInt("start-time"));
                //Message Starting
            for (UUID uuid : gameManager.getPlayerList()) {
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
