package com.soradgaming.squidgame.commands;

import com.soradgaming.squidgame.SquidGame;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandTabCompleter implements TabCompleter {

    public CommandTabCompleter() {
        SquidGame plugin = SquidGame.plugin;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if(cmd.getName().equalsIgnoreCase("squidgame")){
            ArrayList<String> completions = new ArrayList<>();
            if (args.length == 1) {
                completions = new ArrayList<>(Arrays.asList("join", "help", "reload", "leave", "list", "data", "Initialise", "start"));
                completions = getApplicableTabCompletes(args[0], completions);
            } else if (args.length == 2) {
                switch (args[0]) {
                    case "add":
                    case "set":
                    case "remove":
                        completions = new ArrayList<>();
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            completions.add(player.getName());
                        }
                        completions = getApplicableTabCompletes(args[1], completions);
                        break;
                    case "start":
                        completions = new ArrayList<>(Arrays.asList("game1", "game2", "game3", "game4", "game5", "game6", "game7"));
                        completions = getApplicableTabCompletes(args[1], completions);
                        break;
                    default:
                        return null;
                }
            }
            Collections.sort(completions);
            return completions;
        }
        return null;
    }

    public ArrayList<String> getApplicableTabCompletes(String arg, ArrayList<String> completions) {
        if (arg == null || arg.equalsIgnoreCase("")) {
            return completions;
        }
        ArrayList<String> valid = new ArrayList<>();
        for (String possibly : completions) {
            if (possibly.startsWith(arg)) {
                valid.add(possibly);
            }
        }
        return valid;
    }
}
