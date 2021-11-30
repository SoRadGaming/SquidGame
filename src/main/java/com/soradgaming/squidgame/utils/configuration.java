package com.soradgaming.squidgame.utils;

import com.soradgaming.squidgame.SquidGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class configuration {
    private static final SquidGame plugin = SquidGame.plugin;
    private static final FileConfiguration config = SquidGame.plugin.getConfig();

    public static void updateConfig() {
        if (!config.contains("Config-Version")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Error]: No Config Version Found");
            return;
        }

        if (config.getDouble("Config-Version") == 1.0) {
            //add 1.0 Settings (None as it first generated one)
        } else if (config.getDouble("Config-Version") == 1.1) {
            //add new config
            //config.set("Test", false);
        }
    }
}
