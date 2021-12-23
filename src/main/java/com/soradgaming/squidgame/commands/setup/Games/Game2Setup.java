package com.soradgaming.squidgame.commands.setup.Games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Status;
import com.soradgaming.squidgame.commands.setup.CommandHandlerInterface;
import com.soradgaming.squidgame.games.Games;
import com.soradgaming.squidgame.utils.playerWand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Game2Setup implements CommandHandlerInterface {
    private SquidGame plugin;

    public Game2Setup(SquidGame plugin) {
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
        if (!args[1].equalsIgnoreCase("game2")) {
            return true;
        }
        if (!args[2].equals("spawn")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cYou don't have an region wand, use /sq wand to get it."));
            return true;
        } else if (!playerWand.isComplete(player) && !args[2].equals("spawn")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cYou need to set area with your region wand first."));
            return true;
        }
        switch (args[2]) {
            case "spawn_red" -> {
                arena.getStructureManager().setSpawnGame2("spawn_red",player.getLocation());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "spawn_red" + "&a set to your location &7(" + player.getLocation().toVector() + ")"));
            }
            case "spawn_blue" -> {
                arena.getStructureManager().setSpawnGame2("spawn_blue",player.getLocation());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "spawn_blue" + "&a set to your location &7(" + player.getLocation().toVector() + ")"));
            }
            case "spawn_green" -> {
                arena.getStructureManager().setSpawnGame2("spawn_green",player.getLocation());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "spawn_green" + "&a set to your location &7(" + player.getLocation().toVector() + ")"));
            }
            case "spawn_yellow" -> {
                arena.getStructureManager().setSpawnGame2("spawn_yellow",player.getLocation());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "spawn_yellow" + "&a set to your location &7(" + player.getLocation().toVector() + ")"));
            }
            case "BuildZone1" -> {
                arena.getStructureManager().setConfigVectors("Game2.BuildZone1", playerWand.getFirstPoint(player), playerWand.getSecondPoint(player));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "BuildZone1" + "&a set with your location wand &7("
                        + playerWand.getFirstPoint(player).toString() + ") (" + playerWand.getSecondPoint(player).toString() + ")"));
            }
            case "BuildZone2" -> {
                arena.getStructureManager().setConfigVectors("Game2.BuildZone2", playerWand.getFirstPoint(player), playerWand.getSecondPoint(player));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "BuildZone2" + "&a set with your location wand &7("
                        + playerWand.getFirstPoint(player).toString() + ") (" + playerWand.getSecondPoint(player).toString() + ")"));
            }
            case "BuildZone3" -> {
                arena.getStructureManager().setConfigVectors("Game2.BuildZone3", playerWand.getFirstPoint(player), playerWand.getSecondPoint(player));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "BuildZone3" + "&a set with your location wand &7("
                        + playerWand.getFirstPoint(player).toString() + ") (" + playerWand.getSecondPoint(player).toString() + ")"));
            }
            case "BuildZone4" -> {
                arena.getStructureManager().setConfigVectors("Game2.BuildZone4", playerWand.getFirstPoint(player), playerWand.getSecondPoint(player));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "BuildZone4" + "&a set with your location wand &7("
                        + playerWand.getFirstPoint(player).toString() + ") (" + playerWand.getSecondPoint(player).toString() + ")"));
            }
            case "DisplayZone1" -> {
                arena.getStructureManager().setConfigVectors("Game2.DisplayZone1", playerWand.getFirstPoint(player), playerWand.getSecondPoint(player));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "DisplayZone1" + "&a set with your location wand &7("
                        + playerWand.getFirstPoint(player).toString() + ") (" + playerWand.getSecondPoint(player).toString() + ")"));
            }
            case "DisplayZone2" -> {
                arena.getStructureManager().setConfigVectors("Game2.DisplayZone2", playerWand.getFirstPoint(player), playerWand.getSecondPoint(player));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "DisplayZone2" + "&a set with your location wand &7("
                        + playerWand.getFirstPoint(player).toString() + ") (" + playerWand.getSecondPoint(player).toString() + ")"));
            }
            case "DisplayZone3" -> {
                arena.getStructureManager().setConfigVectors("Game2.DisplayZone3", playerWand.getFirstPoint(player), playerWand.getSecondPoint(player));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "DisplayZone3" + "&a set with your location wand &7("
                        + playerWand.getFirstPoint(player).toString() + ") (" + playerWand.getSecondPoint(player).toString() + ")"));
            }
            case "DisplayZone4" -> {
                arena.getStructureManager().setConfigVectors("Game2.DisplayZone4", playerWand.getFirstPoint(player), playerWand.getSecondPoint(player));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "DisplayZone4" + "&a set with your location wand &7("
                        + playerWand.getFirstPoint(player).toString() + ") (" + playerWand.getSecondPoint(player).toString() + ")"));
            }
        }
        return true;
    }

    @Override
    public int getMinArgsLength() {
        return 3;
    }
}
