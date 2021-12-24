package com.soradgaming.squidgame.arena;

import com.soradgaming.squidgame.SquidGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class Arena {
    private SquidGame plugin;
    private String arenaName;
    private static HashMap<String, Arena> arenaNames = new HashMap<>();

    public Arena(String name, SquidGame plugin) {
        this.plugin = plugin;
        this.arenaName = name;
        this.arenaPlayer = new PlayerHandler(plugin, this);
        this.arenaGameHandler = new GameHandler(plugin, this);
        this.arenaFile = new File(plugin.getDataFolder() + File.separator + "arenas" + File.separator + arenaName + ".yml");
    }

    public String getArenaName() {
        return arenaName;
    }

    private StructureManager structureManager = new StructureManager(this);
    public StructureManager getStructureManager() {
        return structureManager;
    }

    private GameHandler arenaGameHandler;
    public GameHandler getGameHandler() {
        return arenaGameHandler;
    }

    private File arenaFile;
    public File getArenaFile() {
        return arenaFile;
    }

    private PlayerHandler arenaPlayer;
    public PlayerHandler getPlayerHandler() {
        return arenaPlayer;
    }

    //Arena Managers
    public static void registerArena(Arena arena) {
        arenaNames.put(arena.getArenaName(), arena);
        arena.getGameHandler().setGameStatus(Status.Offline);
        arena.getStructureManager().saveToConfig();
    }

    public void unregisterArena(Arena arena) {
        arenaNames.remove(arena.getArenaName());
    }

    public static Collection<Arena> getArenas() {
        return arenaNames.values();
    }

    public static Set<String> getArenasNames() {
        return arenaNames.keySet();
    }

    public static Arena getArenaByName(String name) {
        return arenaNames.get(name);
    }

    public static Arena getPlayerArena(Player player) {
        for (Arena arena : arenaNames.values()) {
            if (arena.getPlayerHandler().getAllPlayers().contains(player)) {
                return arena;
            }
        }
        return null;
    }
}
