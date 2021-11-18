package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Game7 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;

    public static void startGame7(ArrayList<UUID> input) {
    }

    public static void endGame7() {



        //WIN
        List<String> commands =  plugin.getConfig().getStringList("rewards");
        for (String cmd:commands) {
           String command = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(plugin.data.getString("winner")), cmd);
           Bukkit.getServer().dispatchCommand(Bukkit.getPlayer(plugin.data.getString("winner")),command);
        }
    }

}
