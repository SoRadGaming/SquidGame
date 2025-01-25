package com.soradgaming.squidgame.arena;

import com.soradgaming.squidgame.SquidGame;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class Arena {
    private final String arenaName;
    private static final HashMap<String, Arena> arenaNames = new HashMap<>();

    public Arena(String name, SquidGame plugin) {
        this.arenaName = name;
        this.arenaPlayer = new PlayerHandler(plugin, this);
        this.arenaGameHandler = new GameHandler(plugin, this);
        this.arenaFile = new File(plugin.getDataFolder() + File.separator + "arenas" + File.separator + arenaName + ".yml");
    }

    public String getArenaName() {
        return arenaName;
    }

    private final StructureManager structureManager = new StructureManager(this);
    public StructureManager getStructureManager() {
        return structureManager;
    }

    private final GameHandler arenaGameHandler;
    public GameHandler getGameHandler() {
        return arenaGameHandler;
    }

    private final File arenaFile;
    public File getArenaFile() {
        return arenaFile;
    }

    private final PlayerHandler arenaPlayer;
    public PlayerHandler getPlayerHandler() {
        return arenaPlayer;
    }

    //Arena Managers
    public static void registerArena(Arena arena) {
        enableArena(arena);
        arena.getStructureManager().saveToConfig();
    }

    public static void enableArena(Arena arena) {
        arenaNames.put(arena.getArenaName(), arena);
        arena.getGameHandler().setGameStatus(Status.Offline);
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
