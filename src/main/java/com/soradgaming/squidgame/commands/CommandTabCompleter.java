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
                completions = new ArrayList<>(Arrays.asList("join", "help", "reload", "leave", "end", "start", "wand"));
                completions = getApplicableTabCompletes(args[0], completions);
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
