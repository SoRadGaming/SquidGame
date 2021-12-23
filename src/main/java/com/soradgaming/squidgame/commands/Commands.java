package com.soradgaming.squidgame.commands;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Messages;
import com.soradgaming.squidgame.utils.playerWand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {

    private final SquidGame plugin = SquidGame.plugin;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        final Player plot = (Player) sender;
        final Location loc = plot.getLocation();
        if (args.length == 0) {
            sender.sendMessage(ChatColor.BLUE + "=============={" + ChatColor.GREEN + "SquidGame" + ChatColor.BLUE + "}==============");
            sender.sendMessage(ChatColor.BLUE + "Plugin developed by:" + ChatColor.GREEN + " SoRadGaming");
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
            sender.sendMessage(ChatColor.GREEN + "Plugin made by: " + ChatColor.BLUE + "SoRadGaming");
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
                Arena arena = Arena.getPlayerArena(((Player) sender).getPlayer());
                if (!arena.getGameHandler().checkStart()) {
                    sender.sendMessage(Messages.formatMessage(((Player) sender).getPlayer(),"arena.no-enough-players"));
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                return true;
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("end")) {
            if (sender.isOp()) {
                Arena arena = Arena.getPlayerArena(((Player) sender).getPlayer());
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                return true;
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("join")) {
            Player player = ((Player) sender).getPlayer();
            Arena arena = Arena.getPlayerArena(player);
            if (arena.getPlayerHandler().playerJoin(player)) {
                return true;
            } else {
                sender.sendMessage(Messages.formatMessage(player,"arena.already-in-game"));
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
            Player player = ((Player) sender).getPlayer();
            Arena arena = Arena.getPlayerArena(player);
            if (arena.getPlayerHandler().playerLeave(player)) {
                return true;
            } else {
                sender.sendMessage(Messages.formatMessage(player,"arena.not-in-game"));
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("wand")) {
            if (sender.isOp()) {
                Player player = (Player) sender;
                ItemStack wand = new playerWand().getWand();
                player.getInventory().addItem(wand);
                player.updateInventory();
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                return true;
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            if (args[1].equalsIgnoreCase("lobby")) {
                plugin.getConfig().set("Lobby",loc);
                plugin.getConfig().set("Game3.world", loc.getWorld().getName());
                plugin.saveConfig();
                sender.sendMessage("Lobby set to "  + loc);
            } else return false;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            if (sender.isOp()) {
                Player player = (Player) sender;
                playerWand playerWandInstance = new playerWand();
                ItemStack wand = playerWandInstance.getWand();
                if (wand == null && !args[2].equals("spawn")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cYou don't have an region wand, use /sq wand to get it."));
                    return false;
                } else if (!playerWandInstance.isComplete() && !args[2].equals("spawn")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cYou need to set area with your region wand first."));
                    return false;
                }
                Arena arena = Arena.getArenaByName(args[1]);
                switch (args[1]) {
                    case "game1":
                        switch (args[2]) {
                            case "spawn" -> {
                                if (args[1].equalsIgnoreCase("game1")) {
                                    plugin.getConfig().set("Game1.spawn", loc);
                                    plugin.getConfig().set("Game1.world", loc.getWorld().getName());
                                    plugin.saveConfig();
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFirst game " + "Spawn" + "&a set to your location &7(" + loc.toVector() + ")"));
                                }
                            }
                            case "barrier" -> {
                                arena.getStructureManager().setConfigVectors("Game1.barrier", playerWandInstance.getFirstPoint(), playerWandInstance.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFirst game " + "barrier" + "&a set with your location wand &7("
                                        + playerWandInstance.getFirstPoint().toString() + ") (" + playerWandInstance.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                            }
                            case "killzone" -> {
                                arena.getStructureManager().setConfigVectors("Game1.killzone", playerWandInstance.getFirstPoint(), playerWandInstance.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFirst game " + "killzone" + "&a set with your location wand &7("
                                        + playerWandInstance.getFirstPoint().toString() + ") (" + playerWandInstance.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                            }
                            case "goal" -> {
                                arena.getStructureManager().setConfigVectors("Game1.goal", playerWandInstance.getFirstPoint(), playerWandInstance.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFirst game " + "goal" + "&a set with your location wand &7("
                                        + playerWandInstance.getFirstPoint().toString() + ") (" + playerWandInstance.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                            }
                        }
                    case "game2":
                        switch (args[2]) {
                            case "spawn_red" -> {
                                if (args[1].equalsIgnoreCase("game2")) {
                                    plugin.getConfig().set("Game2.spawn_red", loc);
                                    plugin.getConfig().set("Game2.world", loc.getWorld().getName());
                                    plugin.saveConfig();
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "spawn_red" + "&a set to your location &7(" + loc.toVector() + ")"));
                                }
                            }
                            case "spawn_blue" -> {
                                if (args[1].equalsIgnoreCase("game2")) {
                                    plugin.getConfig().set("Game2.spawn_blue", loc);
                                    plugin.saveConfig();
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "spawn_blue" + "&a set to your location &7(" + loc.toVector() + ")"));
                                }
                            }
                            case "spawn_green" -> {
                                if (args[1].equalsIgnoreCase("game2")) {
                                    plugin.getConfig().set("Game2.spawn_green", loc);
                                    plugin.saveConfig();
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "spawn_green" + "&a set to your location &7(" + loc.toVector() + ")"));
                                }
                            }
                            case "spawn_yellow" -> {
                                if (args[1].equalsIgnoreCase("game2")) {
                                    plugin.getConfig().set("Game2.spawn_yellow", loc);
                                    plugin.saveConfig();
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "spawn_yellow" + "&a set to your location &7(" + loc.toVector() + ")"));
                                }
                            }
                            case "BuildZone1" -> {
                                arena.getStructureManager().setConfigVectors("Game2.BuildZone1", playerWandInstance.getFirstPoint(), playerWandInstance.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "BuildZone1" + "&a set with your location wand &7("
                                        + playerWandInstance.getFirstPoint().toString() + ") (" + playerWandInstance.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                            }
                            case "BuildZone2" -> {
                                arena.getStructureManager().setConfigVectors("Game2.BuildZone2", playerWandInstance.getFirstPoint(), playerWandInstance.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "BuildZone2" + "&a set with your location wand &7("
                                        + playerWandInstance.getFirstPoint().toString() + ") (" + playerWandInstance.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                            }
                            case "BuildZone3" -> {
                                arena.getStructureManager().setConfigVectors("Game2.BuildZone3", playerWandInstance.getFirstPoint(), playerWandInstance.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "BuildZone3" + "&a set with your location wand &7("
                                        + playerWandInstance.getFirstPoint().toString() + ") (" + playerWandInstance.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                            }
                            case "BuildZone4" -> {
                                arena.getStructureManager().setConfigVectors("Game2.BuildZone4", playerWandInstance.getFirstPoint(), playerWandInstance.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "BuildZone4" + "&a set with your location wand &7("
                                        + playerWandInstance.getFirstPoint().toString() + ") (" + playerWandInstance.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                            }
                            case "DisplayZone1" -> {
                                arena.getStructureManager().setConfigVectors("Game2.DisplayZone1", playerWandInstance.getFirstPoint(), playerWandInstance.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "DisplayZone1" + "&a set with your location wand &7("
                                        + playerWandInstance.getFirstPoint().toString() + ") (" + playerWandInstance.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                            }
                            case "DisplayZone2" -> {
                                arena.getStructureManager().setConfigVectors("Game2.DisplayZone2", playerWandInstance.getFirstPoint(), playerWandInstance.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "DisplayZone2" + "&a set with your location wand &7("
                                        + playerWandInstance.getFirstPoint().toString() + ") (" + playerWandInstance.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                            }
                            case "DisplayZone3" -> {
                                arena.getStructureManager().setConfigVectors("Game2.DisplayZone3", playerWandInstance.getFirstPoint(), playerWandInstance.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "DisplayZone3" + "&a set with your location wand &7("
                                        + playerWandInstance.getFirstPoint().toString() + ") (" + playerWandInstance.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                            }
                            case "DisplayZone4" -> {
                                arena.getStructureManager().setConfigVectors("Game2.DisplayZone4", playerWandInstance.getFirstPoint(), playerWandInstance.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSecond game " + "DisplayZone4" + "&a set with your location wand &7("
                                        + playerWandInstance.getFirstPoint().toString() + ") (" + playerWandInstance.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                            }
                        }
                    case "game4":
                        switch (args[2]) {
                            case "spawn_red" -> {
                                if (args[1].equalsIgnoreCase("game4")) {
                                    plugin.getConfig().set("Game4.spawn_red", loc);
                                    plugin.getConfig().set("Game4.world", loc.getWorld().getName());
                                    plugin.saveConfig();
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFourth game " + "spawn_red" + "&a set to your location &7(" + loc.toVector() + ")"));
                                }
                            }
                            case "spawn_blue" -> {
                                if (args[1].equalsIgnoreCase("game4")) {
                                    plugin.getConfig().set("Game4.spawn_blue", loc);
                                    plugin.saveConfig();
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFourth game " + "spawn_blue" + "&a set to your location &7(" + loc.toVector() + ")"));
                                }
                            }
                            case "barrier" -> {
                                arena.getStructureManager().setConfigVectors("Game4.barrier", playerWandInstance.getFirstPoint(), playerWandInstance.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eFourth game " + "barrier" + "&a set with your location wand &7("
                                        + playerWandInstance.getFirstPoint().toString() + ") (" + playerWandInstance.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                            }
                        }
                    case "game5":
                    case "game6":
                        switch (args[2]) {
                            case "spawn" -> {
                                if (args[1].equalsIgnoreCase("game6")) {
                                    plugin.getConfig().set("Game6.spawn", loc);
                                    plugin.getConfig().set("Game6.world", loc.getWorld().getName());
                                    plugin.saveConfig();
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSixth game " + "Spawn" + "&a set to your location &7(" + loc.toVector() + ")"));
                                }
                            }
                            case "glass" -> {
                                arena.getStructureManager().setConfigVectors("Game6.glass", playerWandInstance.getFirstPoint(), playerWandInstance.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSixth game " + "glass" + "&a set with your location wand &7("
                                        + playerWandInstance.getFirstPoint().toString() + ") (" + playerWandInstance.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                            }
                            case "goal" -> {
                                arena.getStructureManager().setConfigVectors("Game6.goal", playerWandInstance.getFirstPoint(), playerWandInstance.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSixth game " + "goal" + "&a set with your location wand &7("
                                        + playerWandInstance.getFirstPoint().toString() + ") (" + playerWandInstance.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                            }
                            case "barrier" -> {
                                arena.getStructureManager().setConfigVectors("Game6.barrier", playerWandInstance.getFirstPoint(), playerWandInstance.getSecondPoint());
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSixth game " + "barrier" + "&a set with your location wand &7("
                                        + playerWandInstance.getFirstPoint().toString() + ") (" + playerWandInstance.getSecondPoint().toString() + ")"));
                                plugin.saveConfig();
                            }
                        }
                    case "game7":
                        if ("spawn".equals(args[2])) {
                            if (args[1].equalsIgnoreCase("game7")) {
                                plugin.getConfig().set("Game7.spawn", loc);
                                plugin.getConfig().set("Game7.world", loc.getWorld().getName());
                                plugin.saveConfig();
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSeventh game " + "spawn" + "&a set to your location &7(" + loc.toVector() + ")"));
                            }
                        }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that");
                return true;
            }
        }
        return false;
    }
}
