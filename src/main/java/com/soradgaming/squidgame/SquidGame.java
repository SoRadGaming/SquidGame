package com.soradgaming.squidgame;

import com.soradgaming.squidgame.bStats.Metrics;
import com.soradgaming.squidgame.commands.CommandTabCompleter;
import com.soradgaming.squidgame.commands.Commands;
import com.soradgaming.squidgame.games.*;
import com.soradgaming.squidgame.listeners.*;
import com.soradgaming.squidgame.math.CalculateCuboid;
import com.soradgaming.squidgame.placeholders.placeholder;
import com.soradgaming.squidgame.utils.gameManager;
import com.soradgaming.squidgame.utils.playerManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public final class SquidGame extends JavaPlugin {
    public static SquidGame plugin;
    public File schematics = new File(getDataFolder() + "/schematics");
    public File dataFile = new File(getDataFolder() + "/data/players.yml");
    public File messageFile = new File(getDataFolder(), "messages.yml");
    public FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
    public FileConfiguration messages = YamlConfiguration.loadConfiguration(messageFile);


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
            getLogger().info("Could not Find WorldEdit, Game2 Wont Work Without it");
        }

        //Load EventHandler and Commands
        loadMethod();

        //Load Files
        loadFile();
        loadMessage();
        try {
            createSchematicsDir();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Config
        registerConfig();

        //bStats
        int pluginId = 13361;
        Metrics metrics = new Metrics(this, pluginId);

        // Optional: Add custom charts
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "My value"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ArrayList<UUID> playerList = gameManager.getAllPlayers();
        for (UUID uuid: playerList) {
            playerManager.playerQuit(Bukkit.getPlayer(uuid));
        }
        plugin.saveFile();
        plugin.saveMessages();
        getLogger().info("The plugin has been disabled correctly!");
    }

    //Loads all the Events and Commands
    public void loadMethod() {
        //Registers Commands
        Objects.requireNonNull(getCommand("squidgame")).setExecutor(new Commands());
        Objects.requireNonNull(getCommand("squidgame")).setTabCompleter(new CommandTabCompleter());
        //Listener
        Bukkit.getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new EntityDamageListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new FoodLevelChangeListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDropEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Game1(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Game2(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Game3(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Game4(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Game5(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Game6(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Game7(), this);
        //PAPI
        new placeholder().register();
    }

    //Save the data file.
    public void saveFile() {
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void saveMessages() {
        try {
            messages.save(messageFile);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    //Config
    private void registerConfig() {
        saveDefaultConfig();
    }

    //Load the data file
    public void loadFile() {
        if (dataFile.exists()) {
            try {
                data.load(dataFile); //IllegalArgumentException: unknown world
            } catch (IOException | InvalidConfigurationException e) {

                e.printStackTrace();
            }
        } else {
            try {
                data.save(dataFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Create schematic file
    public void createSchematicsDir() throws IOException {
        if(!schematics.exists()) {
            schematics.mkdir();
        }
    }

    //Load the Message
    public void loadMessage() {
        if (!messageFile.exists()) {
            messageFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }
        messages = new YamlConfiguration();
        try {
            messages.load(messageFile);

        } catch (IOException | InvalidConfigurationException e) {

            e.printStackTrace();
        }
    }

    public void reloadMessages() {
        // Look for defaults in the jar
        Reader defConfigStream = new InputStreamReader(Objects.requireNonNull(this.getResource("messages.yml")), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        messages.setDefaults(defConfig);
    }
}
