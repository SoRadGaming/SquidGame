package com.soradgaming.squidgame.commands;

import com.soradgaming.squidgame.SquidGame;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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
                completions = new ArrayList<>(Arrays.asList("join", "help", "reload", "leave", "list", "end", "start", "set", "wand"));
                completions = getApplicableTabCompletes(args[0], completions);
            } else if (args.length == 2) {
                if (args[0].equals("set")) {
                    completions = new ArrayList<>(Arrays.asList("lobby", "Game1", "Game2", "Game3", "Game4", "Game5", "Game6", "Game7"));
                    completions = getApplicableTabCompletes(args[1], completions);
                } else {
                    return null;
                }
            } else if (args.length == 3) {
                switch (args[1]) {
                    case "Game1":
                        completions = new ArrayList<>(Arrays.asList("spawn","barrier","killzone","goal"));
                        completions = getApplicableTabCompletes(args[2], completions);
                        break;
                    case "Game2":
                    case "Game3":
                    case "Game4":
                    case "Game5":
                    case "Game6":
                    case "Game7":
                        completions = new ArrayList<>(Arrays.asList("TODO","TODO","TODO","TODO"));
                        completions = getApplicableTabCompletes(args[2], completions);
                        break;
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
