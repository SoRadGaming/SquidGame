package com.soradgaming.squidgame.placeholders;

import com.soradgaming.squidgame.SquidGame;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class placeholder extends PlaceholderExpansion {
    private final SquidGame plugin = SquidGame.plugin;

    @Override
    public @NotNull String getIdentifier() {
        return "squidgame";
    }

    @Override
    public @NotNull String getAuthor() {
        return "SoRadGaming";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params)  {
        {
            Set<UUID> leadBoardSetPoints = new scoreboard(plugin,PlayerDataType.Points).getPlayerList().keySet();
            List<UUID> leadBoardPoints = new ArrayList<>(leadBoardSetPoints);

            Set<UUID> leadBoardSetWins = new scoreboard(plugin,PlayerDataType.Wins).getPlayerList().keySet();
            List<UUID> leadBoardWins = new ArrayList<>(leadBoardSetWins);
            String[] args = params.split("_");
            if(args[0].equalsIgnoreCase("arena")){
                if (args[1].equalsIgnoreCase("joined")) {
                    return Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(plugin.data.getString("join"))))).getName();
                } else if (args[1].equalsIgnoreCase("maxplayers")) {
                    return String.valueOf(plugin.getConfig().getInt("max-players"));
                } else if (args[1].equalsIgnoreCase("leaved")) {
                    return Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(plugin.data.getString("leave"))))).getName();
                } else if (args[1].equalsIgnoreCase("time")) {
                    return String.valueOf(plugin.getConfig().getInt("start-time"));
                } else if (args[1].equalsIgnoreCase("death")) {
                    return Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(plugin.data.getString("dead"))))).getName();
                } else if (args[1].equalsIgnoreCase("required")) {
                    return String.valueOf(plugin.getConfig().getInt("min-players"));
                } else if (args[1].equalsIgnoreCase("winner")) {
                    return Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(plugin.data.getString("winner"))))).getName();
                } else {
                    return null;
                }
            }

            if(params.equalsIgnoreCase("wins")){
                return String.valueOf(plugin.data.getInt(player.getUniqueId() + ".wins"));
            }

            if(params.equalsIgnoreCase("first")) {
                if (leadBoardWins.size() > 0) {
                    return Objects.requireNonNull(Bukkit.getPlayer(leadBoardWins.get(leadBoardWins.size() - 1))).getName();
                } else return null;
            }

            if(params.equalsIgnoreCase("second")) {
                if (leadBoardWins.size() > 1) {
                    return Objects.requireNonNull(Bukkit.getPlayer(leadBoardWins.get(leadBoardWins.size() - 2))).getName();
                } else return null;
            }

            if(params.equalsIgnoreCase("third")) {
                if (leadBoardWins.size() > 2) {
                    return Objects.requireNonNull(Bukkit.getPlayer(leadBoardWins.get(leadBoardWins.size() - 3))).getName();
                } else return null;
            }

            if(params.equalsIgnoreCase("fourth")) {
                if (leadBoardWins.size() > 3) {
                    return Objects.requireNonNull(Bukkit.getPlayer(leadBoardWins.get(leadBoardWins.size() - 4))).getName();
                } else return null;
            }

            if(params.equalsIgnoreCase("fifth")) {
                if (leadBoardWins.size() > 4) {
                    return Objects.requireNonNull(Bukkit.getPlayer(leadBoardWins.get(leadBoardWins.size() - 5))).getName();
                } else return null;
            }

            if(params.equalsIgnoreCase("points")){
                return String.valueOf(plugin.data.getInt(player.getUniqueId() + ".points"));
            }

            if(params.equalsIgnoreCase("points1")) {
                if (leadBoardPoints.size() > 0) {
                    return Objects.requireNonNull(Bukkit.getPlayer(leadBoardPoints.get(leadBoardPoints.size() - 1))).getName();
                } else return null;
            }

            if(params.equalsIgnoreCase("points2")) {
                if (leadBoardPoints.size() > 1) {
                    return Objects.requireNonNull(Bukkit.getPlayer(leadBoardPoints.get(leadBoardPoints.size() - 2))).getName();
                } else return null;
            }

            if(params.equalsIgnoreCase("points3")) {
                if (leadBoardPoints.size() > 2) {
                    return Objects.requireNonNull(Bukkit.getPlayer(leadBoardPoints.get(leadBoardPoints.size() - 3))).getName();
                } else return null;
            }

            if(params.equalsIgnoreCase("points4")) {
                if (leadBoardPoints.size() > 3) {
                    return Objects.requireNonNull(Bukkit.getPlayer(leadBoardPoints.get(leadBoardPoints.size() - 4))).getName();
                } else return null;
            }

            if(params.equalsIgnoreCase("points5")) {
                if (leadBoardPoints.size() > 4) {
                    return Objects.requireNonNull(Bukkit.getPlayer(leadBoardPoints.get(leadBoardPoints.size() - 5))).getName();
                } else return null;
            }

            return null; // Placeholder is unknown by the Expansion
        }
    }
}
