package com.soradgaming.squidgame;

import com.soradgaming.squidgame.commands.CommandTabCompleter;
import com.soradgaming.squidgame.commands.Commands;
import com.soradgaming.squidgame.games.Game1;
import com.soradgaming.squidgame.games.Game6;
import com.soradgaming.squidgame.games.Game7;
import com.soradgaming.squidgame.listeners.*;
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
import java.util.Objects;
import java.util.UUID;

public final class SquidGame extends JavaPlugin {
    public static SquidGame plugin;
    public File dataFile = new File(getDataFolder() + "/data/players.yml");
    public File messageFile = new File(getDataFolder(), "messages.yml");
    public FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile); //IllegalArgumentException
    public FileConfiguration messages = YamlConfiguration.loadConfiguration(messageFile);


    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        //Check Dependencies
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            getLogger().info("Hooked into PlaceholderAPI");
        }

        //Load EventHandler and Commands
        loadMethod();

        //Load Files
        loadFile();
        loadMessage();

        //Config
        registerConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        plugin.saveFile();
        plugin.saveMessages();
        for (UUID uuid: gameManager.getPlayerList()) {
            playerManager.playerLeave(Bukkit.getPlayer(uuid));
        }
        for (UUID uuid: gameManager.getDeadPlayerList()) {
            playerManager.playerLeave(Bukkit.getPlayer(uuid));
        }
        getLogger().info("The plugin has been disabled correctly!");
    }

    //Loads all the Events and Commands
    public void loadMethod() {
        //Registers Commands
        Objects.requireNonNull(getCommand("squidgame")).setExecutor(new Commands());
        Objects.requireNonNull(getCommand("squidgame")).setTabCompleter(new CommandTabCompleter());
        //Listener
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Game1(), this);
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

    //Save the data file.
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

    //Load the Message
    public void loadMessage() {
        if (messageFile.exists()) {
            try {
                messages.load(messageFile);
            } catch (IOException | InvalidConfigurationException e) {

                e.printStackTrace();
            }
        } else {
            try {
                messages.save(messageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reloadMessages() {
        if (messageFile == null) {
            messageFile = new File(getDataFolder(), "messages.yml");
        }
        messages = YamlConfiguration.loadConfiguration(messageFile);

        // Look for defaults in the jar
        Reader defConfigStream = new InputStreamReader(Objects.requireNonNull(this.getResource("messages.yml")), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        messages.setDefaults(defConfig);
    }
}
