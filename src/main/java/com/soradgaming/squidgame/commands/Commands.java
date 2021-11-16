package com.soradgaming.squidgame.commands;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.games.Game1;
import com.soradgaming.squidgame.games.Game6;
import com.soradgaming.squidgame.math.Cuboid;
import com.soradgaming.squidgame.math.Generator;
import com.soradgaming.squidgame.utils.PlayerWand;
import com.soradgaming.squidgame.utils.gameManager;
import com.soradgaming.squidgame.utils.playerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Commands implements CommandExecutor {

    private static final SquidGame plugin = SquidGame.plugin;


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        final Player plot = (Player) sender;
        final Location loc = plot.getLocation();
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
            sender.sendMessage(ChatColor.GREEN + "/sq start" + ChatColor.BLUE + " Start the Plugin ");
            sender.sendMessage(ChatColor.GREEN + "/sq end" + ChatColor.BLUE + " End MiniGame");
            sender.sendMessage(ChatColor.GREEN + "/sq remove/add/set data player" + ChatColor.BLUE + " Modify Values ");
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
                if (!playerManager.checkStart()) {
                    sender.sendMessage(gameManager.formatMessage(((Player) sender).getPlayer(),"arena.no-enough-players"));
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                return true;
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("end")) {
            if (sender.isOp()) {
                if (playerManager.checkStart()) {
                    Game1.endGame1();
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                return true;
            }
        }  else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            if (sender.isOp()) {
                List<UUID> playerList = gameManager.getAlivePlayers();
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
                return true;
            } else {
                sender.sendMessage(gameManager.formatMessage(player,"arena.already-in-game"));
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
            Player player = ((Player) sender).getPlayer();
            if (playerManager.playerLeave(player)) {
                return true;
            } else {
                sender.sendMessage(gameManager.formatMessage(player,"arena.not-in-game"));
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("wand")) {
            if (sender.isOp()) {
                Player player = (Player) sender;
                player.getInventory().addItem(PlayerWand.getWand());
                player.updateInventory();
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                return true;
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("test")) {
            if (sender.isOp()) {
                //QuickTest
                Generator.generateTiles(Material.LIGHT_GRAY_STAINED_GLASS, Integer.parseInt(args[1]));
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                return true;
            }
        }else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            if (args[1].equalsIgnoreCase("lobby")) {
                plugin.getConfig().set("Lobby",loc);
                plugin.saveConfig();
                sender.sendMessage("Lobby set to "  + loc);
            } else return false;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            if (sender.isOp()) {
                Player player = (Player) sender;
                ItemStack wand = PlayerWand.getWand();
                if (wand == null && !args[2].equals("spawn")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cYou don't have an region wand, use /sq wand to get it."));
                    return false;
                } else if (!PlayerWand.isComplete() && !args[2].equals("spawn")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cYou need to set area with your region wand first."));
                    return false;
                }
                switch (args[1]) {
                    case "Game1":
                        switch (args[2]) {
                            case "spawn":
                                plugin.getConfig().set("Game1.spawn",loc);
                                plugin.saveConfig();
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFirst game " + "Spawn" + "&a set to your location &7(" + loc.toVector() + ")"));
                                break;
                            case "barrier":
                                Cuboid.setConfigVectors("Game1.barrier", PlayerWand.getFirstPoint(), PlayerWand.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFirst game " + "barrier" + "&a set with your location wand &7("
                                        + PlayerWand.getFirstPoint().toString() + ") (" + PlayerWand.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                                break;
                            case "killzone":
                                Cuboid.setConfigVectors("Game1.killzone", PlayerWand.getFirstPoint(), PlayerWand.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFirst game " + "killzone" + "&a set with your location wand &7("
                                        + PlayerWand.getFirstPoint().toString() + ") (" + PlayerWand.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                                break;
                            case "goal":
                                Cuboid.setConfigVectors("Game1.goal", PlayerWand.getFirstPoint(), PlayerWand.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFirst game " + "goal" + "&a set with your location wand &7("
                                        + PlayerWand.getFirstPoint().toString() + ") (" + PlayerWand.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                                break;
                        }
                    case "Game2":
                    case "Game3":
                    case "Game4":
                    case "Game5":
                    case "Game6":
                        switch (args[2]) {
                            case "spawn":
                                plugin.getConfig().set("Game6.spawn",loc);
                                plugin.saveConfig();
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSixth game " + "Spawn" + "&a set to your location &7(" + loc.toVector() + ")"));
                                break;
                            case "glass":
                                Cuboid.setConfigVectors("Game6.glass", PlayerWand.getFirstPoint(), PlayerWand.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSixth game " + "glass" + "&a set with your location wand &7("
                                        + PlayerWand.getFirstPoint().toString() + ") (" + PlayerWand.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                                break;
                            case "goal":
                                Cuboid.setConfigVectors("Game6.goal", PlayerWand.getFirstPoint(), PlayerWand.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSixth game " + "goal" + "&a set with your location wand &7("
                                        + PlayerWand.getFirstPoint().toString() + ") (" + PlayerWand.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                                break;
                        }
                    case "Game7":
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                return true;
            }
        }
        return false;
    }
}
