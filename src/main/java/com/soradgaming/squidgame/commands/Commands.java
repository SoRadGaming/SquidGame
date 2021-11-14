package com.soradgaming.squidgame.commands;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.managment.gameManager;
import com.soradgaming.squidgame.managment.playerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Commands implements CommandExecutor {

    private static final SquidGame plugin = SquidGame.plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        Player plot = (Player) sender;
        Location loc = plot.getLocation();
        if (args.length == 0) {
            sender.sendMessage(ChatColor.BLUE + "=============={" + ChatColor.GREEN + "SquidGame" + ChatColor.BLUE + "}==============");
            sender.sendMessage(ChatColor.BLUE + "Plugin developed by:" + ChatColor.GREEN + " SoRadGaming & Shinx");
            sender.sendMessage(ChatColor.BLUE + "Version: " + ChatColor.GREEN + String.format("%s", plugin.getDescription().getVersion()));
            sender.sendMessage(ChatColor.BLUE + "Plugin:" + ChatColor.GREEN + " https://github.com/SoRadGaming/SquidGame");
            sender.sendMessage(ChatColor.BLUE + "Do " + ChatColor.GREEN + "/sq help " + ChatColor.BLUE + "for the list of commands!");
            sender.sendMessage(ChatColor.BLUE + "=============={" + ChatColor.GREEN + "SquidGame" + ChatColor.BLUE + "}==============");

        } else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            if (!sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                return true;
            }
            sender.sendMessage(ChatColor.BLUE + "-----------------=[" + ChatColor.GREEN + "SquidGame" + ChatColor.BLUE + "]=-----------------");
            sender.sendMessage(ChatColor.GREEN + "/sq help" + ChatColor.BLUE + "  The help command.");
            sender.sendMessage(ChatColor.GREEN + "/sq reload" + ChatColor.BLUE + "  To reload the plugin");
            sender.sendMessage(ChatColor.GREEN + "/sq join" + ChatColor.BLUE + " Join Game");
            sender.sendMessage(ChatColor.GREEN + "/sq leave" + ChatColor.BLUE + " Leave Game");
            sender.sendMessage(ChatColor.GREEN + "/sq list" + ChatColor.BLUE + " See all players in Game");
            sender.sendMessage(ChatColor.GREEN + "/sq Initialise" + ChatColor.BLUE + " Start Data Base Creation " + ChatColor.RED + "REQUIRED");
            sender.sendMessage(ChatColor.GREEN + "/sq start" + ChatColor.BLUE + " Start the Plugin " + ChatColor.RED + "REQUIRED");
            sender.sendMessage(ChatColor.GREEN + "/sq start minigames group" + ChatColor.BLUE + " start a MiniGame with certain group of players");
            sender.sendMessage(ChatColor.GREEN + "/sq end minigames" + ChatColor.BLUE + " End test MiniGame");
            sender.sendMessage(ChatColor.GREEN + "/sq data player remove/add/set points" + ChatColor.BLUE + " Modify Point Values ");
            sender.sendMessage(ChatColor.GREEN + "Plugin made by: " + ChatColor.BLUE + "SoRadGaming & Shinx");
            sender.sendMessage(ChatColor.BLUE + "---------------------------------------------------");

        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.isOp()) {
                plugin.reloadConfig();
                plugin.reloadMessages();
                plugin.getLogger().info("Reloaded");
                sender.sendMessage(ChatColor.GREEN + "Reloaded");
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                return true;
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("start")) {
            if (sender.isOp()) {
                //Star
                sender.sendMessage(ChatColor.GREEN + "Started");
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                return true;
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            if (sender.isOp()) {
                List<UUID> playerList = gameManager.getPlayerList();
                for (UUID uuid : playerList) {
                    sender.sendMessage(ChatColor.BLUE + Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName());
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                return true;
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("join")) {
            Player player = ((Player) sender).getPlayer();
            if (playerManager.playerJoin(player)) {
                sender.sendMessage(ChatColor.BLUE + Objects.requireNonNull(player).getName() + ChatColor.GREEN + " Joined");
                return true;
            } else {
                sender.sendMessage(ChatColor.BLUE + Objects.requireNonNull(player).getName() + ChatColor.RED + " Can't Join");
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
            Player player = ((Player) sender).getPlayer();
            if (playerManager.playerLeave(player)) {
                sender.sendMessage(ChatColor.BLUE + Objects.requireNonNull(player).getName() + ChatColor.GREEN + " Leafed");
                return true;
            } else {
                sender.sendMessage(ChatColor.BLUE + Objects.requireNonNull(player).getName() + ChatColor.RED + " Not in a Game");
            }
        }  else if (args.length == 4 && args[0].equalsIgnoreCase("data")) {
            if (sender.isOp()) {
                Player player = Bukkit.getServer().getPlayer(args[1]);

                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "Player can not be null!");
                    return true;
                }
                if (args[2].equals("remove")) {
                    int oldPoints = plugin.data.getInt(player.getUniqueId() + ".points");
                    int points = oldPoints - Integer.parseInt(args[3]);
                    plugin.data.set(player.getUniqueId() + ".points",points);
                    sender.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " has " + points + " points");
                    return true;
                }if (args[2].equals("add")) {
                    int oldPoints = plugin.data.getInt(player.getUniqueId() + ".points");
                    int points = oldPoints + Integer.parseInt(args[3]);
                    plugin.data.set(player.getUniqueId() + ".points",points);
                    sender.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " has " + points + " points");
                    return true;
                } else if (args[2].equals("set")) {
                    int points = Integer.parseInt(args[3]);
                    plugin.data.set(player.getUniqueId() + ".points",points);
                    sender.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.GREEN + " has " + points + " points");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                return true;
            }
        }
        return false;
    }
}
