package com.soradgaming.squidgame.commands.setup.Games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Status;
import com.soradgaming.squidgame.commands.setup.CommandHandlerInterface;
import com.soradgaming.squidgame.games.Games;
import com.soradgaming.squidgame.utils.playerWand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Game6Setup implements CommandHandlerInterface {
    private SquidGame plugin;

    public Game6Setup(SquidGame plugin) {
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
        if (!args[2].equalsIgnoreCase("game6")) {
            return true;
        }
        if (!playerWand.isComplete(player) && !args[3].equals("spawn")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cYou need to set area with your region wand first."));
            return true;
        }
        switch (args[3]) {
            case "spawn" -> {
                arena.getStructureManager().setSpawnPoint(Games.Game6, player.getLocation());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&Sixth game " + "Spawn" + "&a set to your location &7(" + player.getLocation().toVector() + ")"));
                arena.getStructureManager().saveToConfig();
            }
            case "glass" -> {
                arena.getStructureManager().setConfigVectors("Game6.glass", playerWand.getFirstPoint(player), playerWand.getSecondPoint(player));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSixth game " + "glass" + "&a set with your location wand &7("
                        + playerWand.getFirstPoint(player).toString() + ") (" + playerWand.getSecondPoint(player).toString() + ")"));
                arena.getStructureManager().saveToConfig();
            }
            case "goal" -> {
                arena.getStructureManager().setConfigVectors("Game6.goal", playerWand.getFirstPoint(player), playerWand.getSecondPoint(player));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSixth game " + "goal" + "&a set with your location wand &7("
                        + playerWand.getFirstPoint(player).toString() + ") (" + playerWand.getSecondPoint(player).toString() + ")"));
                arena.getStructureManager().saveToConfig();
            }
            case "barrier" -> {
                arena.getStructureManager().setConfigVectors("Game6.barrier", playerWand.getFirstPoint(player), playerWand.getSecondPoint(player));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSixth game " + "barrier" + "&a set with your location wand &7("
                        + playerWand.getFirstPoint(player).toString() + ") (" + playerWand.getSecondPoint(player).toString() + ")"));
                arena.getStructureManager().saveToConfig();
            }
            case "bridgeBlock" -> {
                arena.getStructureManager().setBridgeBlock(Material.getMaterial(args[4]));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSixth game " + "Bridge Block" + "&a set to &7("
                        + Integer.parseInt(args[4]) + ")"));
            }
            case "time" -> {
                arena.getStructureManager().setTimeLimit(Games.Game6, Integer.parseInt(args[4]));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSixth game " + "Time" + "&a set to &7("
                        + Integer.parseInt(args[4]) + ")"));
            }
            case "countdown" -> {
                arena.getStructureManager().setCountdown(Games.Game6, Integer.parseInt(args[4]));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSixth game " + "Countdown" + "&a set to &7("
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
