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
                completions = new ArrayList<>(Arrays.asList("join", "help", "reload", "leave", "list", "end", "start", "set", "wand", "test"));
                completions = getApplicableTabCompletes(args[0], completions);
            } else if (args.length == 2) {
                if (args[0].equals("set")) {
                    completions = new ArrayList<>(Arrays.asList("lobby", "game1", "game2", "game4", "game5", "game6", "game7"));
                    completions = getApplicableTabCompletes(args[1], completions);
                } else if (args[0].equals("test")) {
                    completions = new ArrayList<>(List.of("playerCount"));
                    completions = getApplicableTabCompletes(args[1], completions);
                } else {
                    return null;
                }
            } else if (args.length == 3) {
                switch (args[1]) {
                    case "game1" -> {
                        completions = new ArrayList<>(Arrays.asList("spawn", "barrier", "killzone", "goal"));
                        completions = getApplicableTabCompletes(args[2], completions);
                    }
                    case "game2", "game4" -> {
                        completions = new ArrayList<>(Arrays.asList("spawn_blue", "spawn_red", "barrier"));
                        completions = getApplicableTabCompletes(args[2], completions);
                    }
                    case "game5", "game6" -> {
                        completions = new ArrayList<>(Arrays.asList("spawn", "glass", "goal", "barrier"));
                        completions = getApplicableTabCompletes(args[2], completions);
                    }
                    case "game7" -> {
                        completions = new ArrayList<>(Arrays.asList("spawn_1", "spawn_2", "spawn_spectator"));
                        completions = getApplicableTabCompletes(args[2], completions);
                    }
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
