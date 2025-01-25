package com.soradgaming.squidgame.placeholders;

import com.soradgaming.squidgame.SquidGame;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        try {
            Set<UUID> leadBoardSetPoints = new Scoreboard(plugin, PlayerDataType.Points).getPlayerList().keySet();
            List<UUID> leadBoardPoints = new ArrayList<>(leadBoardSetPoints);

            Set<UUID> leadBoardSetWins = new Scoreboard(plugin, PlayerDataType.Wins).getPlayerList().keySet();
            List<UUID> leadBoardWins = new ArrayList<>(leadBoardSetWins);
            String[] args = params.split("_");

            if (args[0].equalsIgnoreCase("arena")) {
                return switch (args[1].toLowerCase()) {
                    case "joined" ->
                            Optional.ofNullable(Bukkit.getPlayer(UUID.fromString(plugin.data.getString("join"))))
                                    .map(Player::getName)
                                    .orElse("Unknown Player");
                    case "maxplayers" -> Optional.ofNullable(plugin.getConfig().getString("max-players"))
                            .orElse("Unknown");
                    case "leaved" ->
                            Optional.ofNullable(Bukkit.getPlayer(UUID.fromString(plugin.data.getString("leave"))))
                                    .map(Player::getName)
                                    .orElse("Unknown Player");
                    case "time" -> String.valueOf(plugin.getConfig().getInt("start-time"));
                    case "death" ->
                            Optional.ofNullable(Bukkit.getPlayer(UUID.fromString(plugin.data.getString("dead"))))
                                    .map(Player::getName)
                                    .orElse("Unknown Player");
                    case "required" -> String.valueOf(plugin.getConfig().getInt("min-players"));
                    case "winner" ->
                            Optional.ofNullable(Bukkit.getPlayer(UUID.fromString(plugin.data.getString("winner"))))
                                    .map(Player::getName)
                                    .orElse("Unknown Player");
                    default -> null;
                };
            }
            return switch (params.toLowerCase()) {
                case "wins" -> String.valueOf(plugin.data.getInt(player.getUniqueId() + ".wins"));
                case "first" ->
                        leadBoardWins.isEmpty() ? null : Optional.ofNullable(Bukkit.getPlayer(leadBoardWins.getLast()))
                                .map(Player::getName)
                                .orElse("Unknown Player");
                case "second" ->
                        leadBoardWins.size() > 1 ? Optional.ofNullable(Bukkit.getPlayer(leadBoardWins.get(leadBoardWins.size() - 2)))
                                .map(Player::getName)
                                .orElse("Unknown Player") : null;
                case "third" ->
                        leadBoardWins.size() > 2 ? Optional.ofNullable(Bukkit.getPlayer(leadBoardWins.get(leadBoardWins.size() - 3)))
                                .map(Player::getName)
                                .orElse("Unknown Player") : null;
                case "fourth" ->
                        leadBoardWins.size() > 3 ? Optional.ofNullable(Bukkit.getPlayer(leadBoardWins.get(leadBoardWins.size() - 4)))
                                .map(Player::getName)
                                .orElse("Unknown Player") : null;
                case "fifth" ->
                        leadBoardWins.size() > 4 ? Optional.ofNullable(Bukkit.getPlayer(leadBoardWins.get(leadBoardWins.size() - 5)))
                                .map(Player::getName)
                                .orElse("Unknown Player") : null;
                case "points" -> String.valueOf(plugin.data.getInt(player.getUniqueId() + ".points"));
                case "points1" ->
                        leadBoardPoints.isEmpty() ? null : Optional.ofNullable(Bukkit.getPlayer(leadBoardPoints.getLast()))
                                .map(Player::getName)
                                .orElse("Unknown Player");
                case "points2" ->
                        leadBoardPoints.size() > 1 ? Optional.ofNullable(Bukkit.getPlayer(leadBoardPoints.get(leadBoardPoints.size() - 2)))
                                .map(Player::getName)
                                .orElse("Unknown Player") : null;
                case "points3" ->
                        leadBoardPoints.size() > 2 ? Optional.ofNullable(Bukkit.getPlayer(leadBoardPoints.get(leadBoardPoints.size() - 3)))
                                .map(Player::getName)
                                .orElse("Unknown Player") : null;
                case "points4" ->
                        leadBoardPoints.size() > 3 ? Optional.ofNullable(Bukkit.getPlayer(leadBoardPoints.get(leadBoardPoints.size() - 4)))
                                .map(Player::getName)
                                .orElse("Unknown Player") : null;
                case "points5" ->
                        leadBoardPoints.size() > 4 ? Optional.ofNullable(Bukkit.getPlayer(leadBoardPoints.get(leadBoardPoints.size() - 5)))
                                .map(Player::getName)
                                .orElse("Unknown Player") : null;
                default -> null; // Placeholder is unknown by the Expansion
            };
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing placeholder";
        }
    }
}
