package com.soradgaming.squidgame.commands.setup.Games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Status;
import com.soradgaming.squidgame.commands.setup.CommandHandlerInterface;
import com.soradgaming.squidgame.games.Games;
import com.soradgaming.squidgame.utils.playerWand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Game3Setup implements CommandHandlerInterface {
    private SquidGame plugin;

    public Game3Setup(SquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handleCommand(Player player, String[] args) {
        Arena arena = Arena.getArenaByName(args[0]);
        if (arena == null) {
            return true;
        }
        if (arena.getGameHandler().getStatus().equals(Status.Online)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cDisable Arena First"));
            return true;
        }
        if (!args[1].equalsIgnoreCase("games")) {
            return true;
        }
        if (!args[2].equalsIgnoreCase("game3")) {
            return true;
        }
        if (args[3].equals("spawn")) {
            arena.getStructureManager().setSpawnPoint(Games.Game3, player.getLocation());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eThird game " + "Spawn" + "&a set to your location &7(" + player.getLocation().toVector() + ")"));
            arena.getStructureManager().saveToConfig();
        } else if (args[3].equals("lightSwitchOn")) {
            arena.getStructureManager().setLightSwitchOn(Integer.parseInt(args[4]));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eThird game " + "Light Switch On interval" + "&a set to &7("
                    + Integer.parseInt(args[4]) + ")"));
        } else if (args[3].equals("lightSwitchOff")) {
            arena.getStructureManager().setLightSwitchOff(Integer.parseInt(args[4]));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eThird game " + "Light Switch Off interval" + "&a set to &7("
                    + Integer.parseInt(args[4]) + ")"));
        } else if (args[3].equals("time")) {
            arena.getStructureManager().setTimeLimit(Games.Game3, Integer.parseInt(args[4]));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eThird game " + "Time" + "&a set to &7("
                    + Integer.parseInt(args[4]) + ")"));
        } else if (args[3].equals("countdown")) {
            arena.getStructureManager().setCountdown(Games.Game3, Integer.parseInt(args[4]));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eThird game " + "Countdown" + "&a set to &7("
                    + Integer.parseInt(args[4]) + ")"));
        }
        return true;
    }

    @Override
    public int getMinArgsLength() {
        return 4;
    }
}
