package com.soradgaming.squidgame;

import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Status;
import com.soradgaming.squidgame.bStats.Metrics;
import com.soradgaming.squidgame.commands.CommandTabCompleter;
import com.soradgaming.squidgame.commands.Commands;
import com.soradgaming.squidgame.commands.setup.SetupCommandsHandler;
import com.soradgaming.squidgame.commands.setup.SetupTabCompleter;
import com.soradgaming.squidgame.listeners.*;
import com.soradgaming.squidgame.placeholders.placeholder;
import com.soradgaming.squidgame.utils.configuration;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class SquidGame extends JavaPlugin {
    public static SquidGame plugin;
    public File arenaFolder = new File(getDataFolder() + File.separator + "arenas");
    public File schematics = new File(getDataFolder() + File.separator + "schematics");
    public File dataFile = new File(getDataFolder() + File.separator + "data"+ File.separator + "players.yml");
    public File messageFile = new File(getDataFolder(), "messages.yml");
    public FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
    public FileConfiguration messages = YamlConfiguration.loadConfiguration(messageFile);
    int pluginId = 13361;


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        //Check Dependencies
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("Hooked into PlaceholderAPI");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
            getLogger().info("Hooked into WorldEdit");
        } else {
            getLogger().info("Could not Find WorldEdit, SpeedBuilders (Game2) Wont Work Without it!");
        }

        //Load EventHandler and Commands
        loadMethod();

        //Load Files
        loadFile();
        loadMessage();
        loadArenas();
        try {
            createSchematicsDir();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Config
        registerConfig();

        //Check if Config is Up-to-Date
        configuration.updateConfig();

        //bStats
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            for (String name : Arena.getArenasNames()) {
                Arena arena = Arena.getArenaByName(name);
                List<Player> allPlayers = arena.getPlayerHandler().getAllPlayers();
                for (Player player : allPlayers) {
                    arena.getPlayerHandler().playerQuit(player);
                }
            }
            plugin.saveFile();
            plugin.saveMessages();
            plugin.saveArenas();
            getLogger().info("The plugin has been disabled correctly!");
        } catch (Exception e) {
            getLogger().severe("An error occurred during plugin shutdown: " + e.getMessage());
        }
    }

    //Loads all the Events and Commands
    public void loadMethod() {
        //Registers Commands
        getCommand("squidgame").setExecutor(new Commands());
        getCommand("squidgame").setTabCompleter(new CommandTabCompleter());
        getCommand("squidgamesetup").setExecutor(new SetupCommandsHandler(this));
        getCommand("squidgamesetup").setTabCompleter(new SetupTabCompleter());
        //Listener
        Bukkit.getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDropEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        //PAPI
        new placeholder().register();
    }

    // Save the data file.
    public void saveFile() {
        try {
            data.save(dataFile);
            getLogger().info("Data file saved successfully.");
        } catch (IOException e) {
            getLogger().severe("Failed to save data file: " + e.getMessage());
        }
    }

    // Save the messages file.
    public void saveMessages() {
        try {
            messages.save(messageFile);
            getLogger().info("Messages file saved successfully.");
        } catch (IOException e) {
            getLogger().severe("Failed to save messages file: " + e.getMessage());

        }
    }

    private void saveArenas() {
        for (Arena arena : Arena.getArenas()) {
            try {
                arena.getGameHandler().setGameStatus(Status.Offline);
                arena.getStructureManager().saveToConfig();
                getLogger().info("Saved " + arena.getArenaName());
            } catch (Exception e) {
                getLogger().severe("Failed to save arena: " + arena.getArenaName());
                getLogger().severe("Exception occurred while saving arena: " + arena.getArenaName() + " - " + e.getMessage());
            }
        }
    }

    private void loadArenas() {
        if (!arenaFolder.exists()) {
            if (!arenaFolder.mkdir()) {
                getLogger().severe("Failed to create arena folder!");
                return;
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                String[] arenaList = arenaFolder.list();
                if (arenaList == null) {
                    getLogger().severe("Failed to list files in arena folder!");
                    return;
                }
                for (String file : arenaList) {
                    try {
                        Arena arena = new Arena(file.substring(0, file.length() - 4), plugin);
                        arena.getStructureManager().loadFromConfig();
                        Arena.enableArena(arena);
                        getLogger().info("Loaded " + arena.getArenaName());
                    } catch (Exception e) {
                        getLogger().severe("Failed to load arena: " + file);
                        getLogger().severe("Exception occurred while loading arena: " + file + " - " + e.getMessage());
                    }
                }
            }
        }.runTask(this);
    }

    // Config
    private void registerConfig() {
        saveDefaultConfig();
    }

    // Load the data file
    public void loadFile() {
        if (dataFile.exists()) {
            try {
                data.load(dataFile);
                getLogger().info("Data file loaded successfully.");
            } catch (IOException e) {
                getLogger().severe("Failed to load data file due to an IOException: " + e.getMessage());
            } catch (InvalidConfigurationException e) {
                getLogger().severe("Failed to load data file due to an InvalidConfigurationException: " + e.getMessage());
            } catch (Exception e) {
                getLogger().severe("Unexpected error occurred while loading data file: " + e.getMessage());
            }
        } else {
            try {
                data.save(dataFile);
                getLogger().info("Data file created and saved successfully.");
            } catch (IOException e) {
                getLogger().severe("Failed to save data file: " + e.getMessage());
            } catch (Exception e) {
                getLogger().severe("Unexpected error occurred while saving data file: " + e.getMessage());
            }
        }
    }

    // Create schematic file
    public void createSchematicsDir() throws IOException {
        if (!schematics.exists()) {
            if (schematics.mkdir()) {
                getLogger().info("Schematics directory created successfully.");
            } else {
                throw new IOException("Failed to create schematics directory.");
            }
        } else {
            getLogger().info("Schematics directory already exists.");
        }
    }

    // Load the Message
    public void loadMessage() {
        if (!messageFile.exists()) {
            messageFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }
        try {
            messages.load(messageFile);
        } catch (IOException e) {
            getLogger().severe("Failed to load messages file due to an IOException: " + e.getMessage());
        } catch (InvalidConfigurationException e) {
            getLogger().severe("Failed to load messages file due to an InvalidConfigurationException: " + e.getMessage());
        } catch (Exception e) {
            getLogger().severe("Unexpected error occurred while loading messages file: " + e.getMessage());
        }
    }

    public void reloadMessages() {
        // Look for defaults in the jar
        try (Reader defConfigStream = new InputStreamReader(this.getResource("messages.yml"), StandardCharsets.UTF_8)) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            messages.setDefaults(defConfig);
        } catch (NullPointerException e) {
            getLogger().severe("Failed to find messages.yml in the plugin jar. NullPointerException: " + e.getMessage());
        } catch (IOException e) {
            getLogger().severe("Failed to load messages.yml. IOException: " + e.getMessage());
        } catch (Exception e) {
            getLogger().severe("Unexpected error occurred while loading messages.yml: " + e.getMessage());
        }
    }
}
