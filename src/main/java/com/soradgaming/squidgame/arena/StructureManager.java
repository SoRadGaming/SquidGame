package com.soradgaming.squidgame.arena;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.games.Games;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BlockVector;

import java.io.IOException;
import java.util.*;

public class StructureManager {
    private SquidGame plugin;
    private Arena arena;
    private HashMap<Games, String> world;
    private int maxPlayers = 12;
    private int minPlayers = 2;
    private Location lobby = null;
    private int startTime = 30;
    private int endTime = 5;
    private int intermissionTime = 10;
    private HashMap<Games, Integer> timeLimit = new HashMap<>();
    private HashMap<Games, Integer> countdown = new HashMap<>();
    private HashMap<Games,Location> spawn = new HashMap<>();
    private HashMap<Games,List<Location>> additionalSpawnPoints = new HashMap<>();
    private HashMap<Games,List<Location>> freeSpawnList = new HashMap<>();
    private int lightSwitchMin = 1;
    private int lightSwitchMax = 5;
    private int lightSwitchOn = 1;
    private int lightSwitchOff = 8;
    private Material bridgeBlock = Material.LIGHT_GRAY_STAINED_GLASS;
    private Material killBlock = Material.LIGHT_GRAY_STAINED_GLASS;
    private List<String> rewards;

    public StructureManager(Arena arena) {
        this.plugin = SquidGame.plugin;
        this.arena = arena;
    }

    public String getWorldName(Games games) {
        return world.get(games);
    }

    public World getWorld(Games games) {
        return Bukkit.getWorld(world.get(games));
    }

    public void setWorld(Games games, String worldI) {world.put(games,worldI);}

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int i) {maxPlayers = i;}

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int i) {minPlayers = i;}

    public Location getSpawn(Games games) {return spawn.get(games);}

    public boolean isSpawnSet(Games games) {
        return spawn.get(games) != null;
    }

    public void removeAdditionalSpawnPoints(Games games) {additionalSpawnPoints.get(games).clear();}

    public void setSpawnPoint(Games games, Location location) {
        this.spawn.put(games,location);
        this.world.put(games,location.getWorld().getName());
    }

    public void addSpawnPoint(Games games, Location loc) {additionalSpawnPoints.get(games).add(loc);}

    public boolean hasAdditionalSpawnPoints(Games games) {return !additionalSpawnPoints.get(games).isEmpty();}

    public void removeSpawn(Games games,Location location) {spawn.put(games, null);}

    private Location nextSpawnPoint(Games games) {
        if (freeSpawnList.get(games).isEmpty()) {
            freeSpawnList.get(games).add(spawn.get(games));
            freeSpawnList.get(games).addAll(additionalSpawnPoints.get(games));
        }
        return freeSpawnList.get(games).remove(0);
    }

    public List<Location> getFreeSpawnList(Games games) {
        return freeSpawnList.get(games);
    }

    public int getTimeLimit(Games games) {return timeLimit.get(games);}

    public void setTimeLimit(Games games,int i) {timeLimit.put(games, i);}

    public int getCountdown(Games games) {return countdown.get(games);}

    public void setCountdown(Games games,int i) {countdown.put(games, i);}

    public void saveToConfig() {
        FileConfiguration config = new YamlConfiguration();
        config.set("maxPlayers", maxPlayers);
        config.set("minPlayers", minPlayers);
        config.set("lobby", lobby);
        config.set("startTime", startTime);
        config.set("endTime", endTime);
        config.set("intermissionTime", intermissionTime);
        for (Games key : world.keySet()) {
            config.set("world." + key, world.get(key));
        }
        for (Games key : spawn.keySet()) {
            config.set("spawn." + key, spawn.get(key));
        }
        for (Games key : timeLimit.keySet()) {
            config.set("timeLimit." + key, timeLimit.get(key));
        }
        for (Games key : countdown.keySet()) {
            config.set("countdown." + key, countdown.get(key));
        }
        for (Games key : additionalSpawnPoints.keySet()) {
            config.set("additionalSpawnPoints." + key, additionalSpawnPoints.get(key));
        }
        config.set("lightSwitchMin", lightSwitchMin);
        config.set("lightSwitchMax", lightSwitchMax);
        config.set("lightSwitchOn", lightSwitchOn);
        config.set("lightSwitchOff", lightSwitchOff);
        config.set("bridgeBlock", bridgeBlock);
        config.set("killBlock", killBlock);
        config.set("rewards", rewards);
        //Save Arena file
        try {
            config.save(arena.getArenaFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromConfig() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(arena.getArenaFile());
        maxPlayers = config.getInt("maxPlayers");
        minPlayers = config.getInt("minPlayers");
        lobby = config.getLocation("lobby");
        startTime = config.getInt("startTime");
        endTime = config.getInt("endTime");
        intermissionTime = config.getInt("intermissionTime");
        for (String key : config.getConfigurationSection("world").getKeys(false)) {
            world.put(stringToGame(key), config.getString("world." + key));
        }
        for (String key : config.getConfigurationSection("spawn").getKeys(false)) {
            spawn.put(stringToGame(key), config.getLocation("spawn." + key));
        }
        for (String key : config.getConfigurationSection("timeLimit").getKeys(false)) {
            timeLimit.put(stringToGame(key), config.getInt("timeLimit." + key));
        }
        for (String key : config.getConfigurationSection("countdown").getKeys(false)) {
            countdown.put(stringToGame(key), config.getInt("countdown." + key));
        }
        for (String key : config.getConfigurationSection("additionalSpawnPoints").getKeys(false)) {
            additionalSpawnPoints.put(stringToGame(key), (List<Location>) config.getList("additionalSpawnPoints." + key, new ArrayList<>()));
        }
        lightSwitchMin = config.getInt("lightSwitchMin");
        lightSwitchMax = config.getInt("lightSwitchMax");
        lightSwitchOn = config.getInt("lightSwitchOn");
        lightSwitchOff = config.getInt("lightSwitchOff");
        bridgeBlock = Material.getMaterial(config.getString("bridgeBlock"));
        killBlock = Material.getMaterial(config.getString("killBlock"));
        rewards = config.getStringList("rewards");
    }

    public BlockVector configToVectors(String key) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(arena.getArenaFile());
        BlockVector pos = new BlockVector();
        pos.setX(config.getDouble(key + ".x"));
        pos.setY(config.getDouble(key + ".y"));
        pos.setZ(config.getDouble(key + ".z"));
        return pos;
    }

    public Games stringToGame(String string) {
        if (string.equalsIgnoreCase("Game1")) {
            return Games.Game1;
        } else if (string.equalsIgnoreCase("Game2")) {
            return Games.Game2;
        } else if (string.equalsIgnoreCase("Game3")) {
            return Games.Game3;
        } else if (string.equalsIgnoreCase("Game4")) {
            return Games.Game4;
        } else if (string.equalsIgnoreCase("Game5")) {
            return Games.Game5;
        } else if (string.equalsIgnoreCase("Game6")) {
            return Games.Game6;
        } else if (string.equalsIgnoreCase("Game7")) {
            return Games.Game7;
        }
        return null;
    }
}