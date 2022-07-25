package com.soradgaming.squidgame.commands.setup.Games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Status;
import com.soradgaming.squidgame.commands.setup.CommandHandlerInterface;
import com.soradgaming.squidgame.games.Games;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Game4Setup implements CommandHandlerInterface {
    private SquidGame plugin;

    public Game4Setup(SquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handleCommand(Player player, String[] args) {
        Arena arena = Arena.getArenaByName(args[0]);
        if (arena == null) {
            player.sendMessage("Not Valid Arena: " + arena);
            return true;
        }
        if (arena.getGameHandler().getStatus().equals(Status.Online)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cDisable Arena First"));
            return true;
        }
        if (!args[1].equalsIgnoreCase("games")) {
            return true;
        }
        if (!args[2].equalsIgnoreCase("game4")) {
            return true;
        }
        switch (args[3]) {
            case "spawn" -> {
                arena.getStructureManager().setSpawnPoint(Games.Game4, player.getLocation());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFourth game " + "Spawn" + "&a set to your location &7(" + player.getLocation().toVector() + ")"));
                arena.getStructureManager().saveToConfig();
            }
            case "time" -> {
                arena.getStructureManager().setTimeLimit(Games.Game4, Integer.parseInt(args[4]));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFourth game " + "Time" + "&a set to &7("
                        + Integer.parseInt(args[4]) + ")"));
            }
            case "countdown" -> {
                arena.getStructureManager().setCountdown(Games.Game4, Integer.parseInt(args[4]));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFourth game " + "Countdown" + "&a set to &7("
                        + Integer.parseInt(args[4]) + ")"));
            }
        }
        return true;
    }

    @Override
    public int getMinArgsLength() {
        return 4;
    }
}
