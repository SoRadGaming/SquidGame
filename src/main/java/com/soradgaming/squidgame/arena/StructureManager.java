package com.soradgaming.squidgame.arena;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.games.Games;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BlockVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StructureManager {
    private SquidGame plugin;
    private Arena arena;
    private String world = null;
    private int maxPlayers = 12;
    private int minPlayers = 2;
    private Location spawn = null;
    private Location spectatorSpawn = null;
    private int timeLimit = 180;
    private int countdown = 30;
    private List<Location> additionalSpawnPoints = new ArrayList<>();
    private List<Location> freeSpawnList = new ArrayList<>();

    public StructureManager(Arena arena) {
        this.plugin = SquidGame.plugin;
        this.arena = arena;
    }

    public String getWorldName() {
        return world;
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    public void setWorld(String worldI) {world = worldI;}

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int i) {maxPlayers = i;}

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int i) {minPlayers = i;}

    public Location getSpectatorSpawn() {return spectatorSpawn;}

    public Location getSpawn() {return spawn;}

    public boolean isSpawnSet() {
        return spawn != null;
    }

    public boolean isSpectatorSpawnSet() {
        return spectatorSpawn != null;
    }

    public void removeSpectatorsSpawn() {spectatorSpawn = null;}

    public void removeAdditionalSpawnPoints() {additionalSpawnPoints.clear();}

    public void setSpawnPoint(Location location) {
        this.spawn = location;
        this.world = Objects.requireNonNull(location.getWorld()).getName();
    }

    public void addSpawnPoint(Location loc) {additionalSpawnPoints.add(loc);}

    public boolean hasAdditionalSpawnPoints() {return !additionalSpawnPoints.isEmpty();}

    public void removeSpawn(Location location) {spawn = null;}

    private Location nextSpawnPoint() {
        if (freeSpawnList.isEmpty()) {
            freeSpawnList.add(spawn);
            freeSpawnList.addAll(additionalSpawnPoints);
        }
        return freeSpawnList.remove(0);
    }

    public List<Location> getFreeSpawnList() {
        return freeSpawnList;
    }

    public int getTimeLimit() {return timeLimit;}

    public void setTimeLimit(int i) {timeLimit = i;}

    public int getCountdown() {return countdown;}

    public void setCountdown(int i) {countdown = i;}

    public void setSpectatorsSpawn(Location loc) {spectatorSpawn = loc;}

    public void saveToConfig() {
        FileConfiguration config = new YamlConfiguration();
        config.set("world", world);
        config.set("maxPlayers", maxPlayers);
        config.set("minPlayers", minPlayers);
        config.set("spawn", spawn);
        config.set("spectatorSpawn", spectatorSpawn);
        config.set("timeLimit", timeLimit);
        config.set("countdown", countdown);
        config.set("additionalSpawnPoints", additionalSpawnPoints);
        //Save Arena file
        try {
            config.save(arena.getArenaFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromConfig() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(arena.getArenaFile());
        world = config.getString("world");
        maxPlayers = config.getInt("maxPlayers");
        minPlayers = config.getInt("minPlayers");
        spawn = config.getLocation("spawn");
        spectatorSpawn = config.getLocation("spectatorSpawn");
        timeLimit = config.getInt("timeLimit");
        countdown = config.getInt("countdown");
        additionalSpawnPoints = (List<Location>) config.getList("additionalSpawnPoints", new ArrayList<>());
    }

    public BlockVector configToVectors(String key) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(arena.getArenaFile());
        BlockVector pos = new BlockVector();
        pos.setX(config.getDouble(key + ".x"));
        pos.setY(config.getDouble(key + ".y"));
        pos.setZ(config.getDouble(key + ".z"));
        return pos;
    }

    public int getTimeLimit(Games games) {
        if (games.equals(Games.Game1)) {

        }
    }
}
