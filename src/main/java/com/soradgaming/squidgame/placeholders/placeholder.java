package com.soradgaming.squidgame.placeholders;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.utils.gameManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class placeholder extends PlaceholderExpansion {
    private static final SquidGame plugin = SquidGame.plugin;

    @Override
    public @NotNull String getIdentifier() {
        return "squidgame";
    }

    @Override
    public @NotNull String getAuthor() {
        return "SoRadGaming & Shinx";
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
            Set<UUID> leadBoardSet = scoreboard.grabData().keySet();
            List<UUID> leadBoard = new ArrayList<>(leadBoardSet);
            String[] args = params.split("_");
            if(args[0].equalsIgnoreCase("arena")){
                if (args[1].equalsIgnoreCase("joined")) {
                    return Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(plugin.data.getString("join"))))).getName();
                } else if (args[1].equalsIgnoreCase("players")) {
                    return String.valueOf(gameManager.getAlivePlayers().size() + gameManager.getDeadPlayers().size());
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
                if (leadBoard.size() > 0) {
                    return Objects.requireNonNull(Bukkit.getPlayer(leadBoard.get(leadBoard.size() - 1))).getName();
                } else return null;
            }

            if(params.equalsIgnoreCase("second")) {
                if (leadBoard.size() > 1) {
                    return Objects.requireNonNull(Bukkit.getPlayer(leadBoard.get(leadBoard.size() - 2))).getName();
                } else return null;
            }

            if(params.equalsIgnoreCase("third")) {
                if (leadBoard.size() > 2) {
                    return Objects.requireNonNull(Bukkit.getPlayer(leadBoard.get(leadBoard.size() - 3))).getName();
                } else return null;
            }

            if(params.equalsIgnoreCase("fourth")) {
                if (leadBoard.size() > 3) {
                    return Objects.requireNonNull(Bukkit.getPlayer(leadBoard.get(leadBoard.size() - 4))).getName();
                } else return null;
            }

            if(params.equalsIgnoreCase("fifth")) {
                if (leadBoard.size() > 4) {
                    return Objects.requireNonNull(Bukkit.getPlayer(leadBoard.get(leadBoard.size() - 5))).getName();
                } else return null;
            }

            return null; // Placeholder is unknown by the Expansion
        }
    }
}
