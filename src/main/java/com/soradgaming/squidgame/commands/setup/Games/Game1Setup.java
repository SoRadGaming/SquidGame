package com.soradgaming.squidgame.commands.setup.Games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Messages;
import com.soradgaming.squidgame.arena.Status;
import com.soradgaming.squidgame.commands.setup.CommandHandlerInterface;
import com.soradgaming.squidgame.games.Games;
import com.soradgaming.squidgame.utils.playerWand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Game1Setup implements CommandHandlerInterface {
    private SquidGame plugin;

    public Game1Setup(SquidGame plugin) {
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
        if (!args[1].equalsIgnoreCase("game1")) {
            return true;
        }
        if (!playerWand.isComplete(player) && !args[2].equals("spawn")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cYou need to set area with your region wand first."));
            return true;
        }
        switch (args[2]) {
            case "spawn" -> {
                if (!arena.getStructureManager().isSpawnSet(Games.Game1)) {
                    arena.getStructureManager().setSpawnPoint(Games.Game1,player.getLocation());
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFirst game " + "Spawn" + "&a set to your location &7(" + player.getLocation().toVector() + ")"));
                } else {
                    arena.getStructureManager().addSpawnPoint(Games.Game1,player.getLocation());
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFirst game " + "Spawn" + "&a set to your location &7(" + player.getLocation().toVector() + ")"));
                }
            }
            case "barrier" -> {
                arena.getStructureManager().setConfigVectors("Game1.barrier", playerWand.getFirstPoint(player), playerWand.getSecondPoint(player));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFirst game " + "barrier" + "&a set with your location wand &7("
                        + playerWand.getFirstPoint(player).toString() + ") (" + playerWand.getSecondPoint(player).toString() + ")"));
            }
            case "killzone" -> {
                arena.getStructureManager().setConfigVectors("Game1.killzone", playerWand.getFirstPoint(player), playerWand.getSecondPoint(player));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFirst game " + "killzone" + "&a set with your location wand &7("
                        + playerWand.getFirstPoint(player).toString() + ") (" + playerWand.getSecondPoint(player).toString() + ")"));
            }
            case "goal" -> {
                arena.getStructureManager().setConfigVectors("Game1.goal", playerWand.getFirstPoint(player), playerWand.getSecondPoint(player));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFirst game " + "goal" + "&a set with your location wand &7("
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
