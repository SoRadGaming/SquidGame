package com.soradgaming.squidgame.managment;

import com.soradgaming.squidgame.SquidGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class bracketManager implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;

    //Start command
    public static void playerBracket(@NotNull ArrayList<UUID> input) {
        //Active Players

        for (UUID uuid : input) {
            Player p = Bukkit.getPlayer(uuid);
            assert p != null;

            //Data
            plugin.data.set(uuid + ".wins", 0); // RESETS DATA
            //Save Data
            plugin.saveFile();
        }
    }
}
