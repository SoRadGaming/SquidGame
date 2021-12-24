package com.soradgaming.squidgame.arena;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.games.Games;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BlockVector;

import java.io.IOException;
import java.util.*;

public class StructureManager {
    private SquidGame plugin;
    private Arena arena;
    private int maxPlayers = 12;
    private int minPlayers = 2;
    private int startTime = 30;
    private int endTime = 5;
    private int intermissionTime = 10;
    private HashMap<Games, Integer> timeLimit = new HashMap<>();
    private HashMap<Games, Integer> countdown = new HashMap<>();
    private HashMap<Games,Location> spawn = new HashMap<>();
    private HashMap<String,Location> spawnGame2 = new HashMap<>();
    private HashMap<Games,List<Location>> additionalSpawnPoints = new HashMap<>();
    private HashMap<Games,List<Location>> freeSpawnList = new HashMap<>();
    private int lightSwitchMin = 1;
    private int lightSwitchMax = 5;
    private int lightSwitchOn = 1;
    private int lightSwitchOff = 8;
    private Material bridgeBlock = Material.LIGHT_GRAY_STAINED_GLASS;
    private Material killBlock = Material.LIGHT_GRAY_STAINED_GLASS;
    private List<String> rewards;//TODo Global Config

    public StructureManager(Arena arena) {
        this.plugin = SquidGame.plugin;
        this.arena = arena;
    }

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

    public void setLightSwitchMin(int lightSwitchMin) {
        this.lightSwitchMin = lightSwitchMin;
    }

    public void setLightSwitchMax(int lightSwitchMax) {
        this.lightSwitchMax = lightSwitchMax;
    }

    public void setLightSwitchOn(int lightSwitchOn) {
        this.lightSwitchOn = lightSwitchOn;
    }

    public void setLightSwitchOff(int lightSwitchOff) {
        this.lightSwitchOff = lightSwitchOff;
    }

    public int getLightSwitchMin() {
        return this.lightSwitchMin;
    }

    public int getLightSwitchMax() {
        return this.lightSwitchMax;
    }

    public int getLightSwitchOn() {
        return this.lightSwitchOn;
    }

    public int getLightSwitchOff() {
        return this.lightSwitchOff;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getEndTime() {
        return this.endTime;
    }

    public int getIntermissionTime() {
        return this.intermissionTime;
    }

    public void setIntermissionTime(int intermissionTime) {this.intermissionTime = intermissionTime;}

    public Material getBridgeBlock() {return bridgeBlock;}

    public void setBridgeBlock(Material bridgeBlock) {this.bridgeBlock = bridgeBlock;}

    public Material getKillBlock() {return killBlock;}

    public void setKillBlock(Material killBlock) {this.killBlock = killBlock;}

    public void saveToConfig() {
        YamlConfiguration config;
        if (!arena.getArenaFile().isFile()) {
            config = new YamlConfiguration();
        } else {
            config = YamlConfiguration.loadConfiguration(arena.getArenaFile());
        }
        config.set("maxPlayers", maxPlayers);
        config.set("minPlayers", minPlayers);
        config.set("startTime", startTime);
        config.set("endTime", endTime);
        config.set("intermissionTime", intermissionTime);
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
        config.set("bridgeBlock", bridgeBlock.toString());
        config.set("killBlock", killBlock.toString());
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
        startTime = config.getInt("startTime");
        endTime = config.getInt("endTime");
        intermissionTime = config.getInt("intermissionTime");
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

    public void setConfigVectors(String key, BlockVector pos1, BlockVector pos2) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(arena.getArenaFile());
        config.set(key + ".first_point.x",pos1.getX());
        config.set(key + ".first_point.y",pos1.getY());
        config.set(key + ".first_point.z",pos1.getZ());
        config.set(key + ".second_point.x",pos2.getX());
        config.set(key + ".second_point.y",pos2.getY());
        config.set(key + ".second_point.z",pos2.getZ());
        try {
            config.save(arena.getArenaFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void setSpawnGame2(String key, Location location) {
        spawnGame2.put(key,location);
    }

    public Location getSpawnGame2(String key) {
        return spawnGame2.get(key);
    }
}
