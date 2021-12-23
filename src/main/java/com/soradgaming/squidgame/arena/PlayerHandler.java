package com.soradgaming.squidgame.arena;

import com.soradgaming.squidgame.SquidGame;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class PlayerHandler {
    private SquidGame plugin;
    private Arena arena;

    public PlayerHandler(SquidGame plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    //Stats
    private HashMap<Player, ItemStack[]> playerInv = new HashMap<>();
    private HashMap<Player, ItemStack[]> playerArmour = new HashMap<>();
    private HashMap<Player, Collection<PotionEffect>> playerEffects = new HashMap<>();
    private HashMap<Player, Location> last_location = new HashMap<>();
    private HashMap<Player, GameMode> gamemode = new HashMap<>();
    private HashMap<Player, Double> healthScale = new HashMap<>();
    private HashMap<Player, Double> health = new HashMap<>();
    private HashMap<Player, Integer> level = new HashMap<>();
    private HashMap<Player, Float> xp = new HashMap<>();
    private HashMap<Player, Location> bedSpawn = new HashMap<>();
    private HashMap<Player, Integer> foodLevel = new HashMap<>();
    //Players
    private List<Player> playerListAlive = new ArrayList<>();
    private List<Player> playerListDead = new ArrayList<>();
    private List<Player> playerListAll = new ArrayList<>();

    private void savePlayerData(Player player) {
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

    }

    private void setDefaultData(Player player) {
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
    }

    private void returnPlayerData(Player player) {
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
    }

    public List<Player> getAlivePlayers() { return playerListAlive; }

    public List<Player> getDeadPlayers() { return playerListDead; }

    public List<Player> getAllPlayers() { return playerListAll; }

    public boolean addPlayer(Player player) {
        if (!playerListAll.contains(player)) {
            playerListAlive.add(player);
            playerListAll.add(player);
            return true;
        }
        return false;
    }

    public boolean removePlayer(Player player) {
        if (playerListAll.contains(player)) {
            if (playerListAlive.contains(player)) {
                playerListAlive.remove(player);
            } else playerListDead.remove(player);
            playerListAll.remove(player);
            return true;
        }
        return false;
    }

    public boolean killPlayer(Player player) {
        if (playerListAll.contains(player) && playerListAlive.contains(player)) {
            playerListAlive.remove(player);
            playerListDead.add(player);
            return true;
        }
        return false;
    }

    public void Initialise() {
        //Active Players
        for (Player player : getAllPlayers()) {
            UUID uuid = player.getUniqueId();
            //Data Create if Never Player Before
            if (!plugin.data.contains(uuid + ".wins")) {
                plugin.data.set(uuid + ".wins", 0);
                plugin.data.set(uuid + ".kills", 0);
            }
            plugin.data.set(uuid + ".points", 0);

            //Save Data
            plugin.saveFile();
        }
    }

    public boolean playerJoin(Player player) {
        if (getAllPlayers().size() <= plugin.getConfig().getInt("max-players") && arena.getGameHandler().getStatus().equals(Status.Offline) || arena.getGameHandler().getStatus().equals(Status.Starting)) {
            plugin.data.set("join",player.getUniqueId().toString());
            //checkStart();
            for (Player players : getAllPlayers()) {
                Objects.requireNonNull(players).sendMessage(Messages.formatMessage(player,"arena.join"));
            }
            savePlayerData(player);
            setDefaultData(player);
            addPlayer(player);
            return true;
        }
        return false;
    }

    public boolean playerLeave(Player player) {
        if (arena.getGameHandler().getStatus().equals(Status.Starting)) {
            plugin.data.set("leave",player.getUniqueId().toString());
            for (Player players : getAllPlayers()) {
                Objects.requireNonNull(players).sendMessage(Messages.formatMessage(player,"arena.leave"));
            }
            returnPlayerData(player);
            removePlayer(player);
            return true;
        }
        return false;
    }

    public void playerQuit(Player player) {
        returnPlayerData(player);
    }
}
