package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.math.CalculateCuboid;
import com.soradgaming.squidgame.utils.gameManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;


public class Game2 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    private static boolean Started = false;
    private static ArrayList<Player> team1 = new ArrayList<>();
    private static ArrayList<Player> team2 = new ArrayList<>();
    private static ArrayList<Player> team3 = new ArrayList<>();
    private static ArrayList<Player> team4 = new ArrayList<>();
    private static boolean winTeam1 = false;
    private static boolean winTeam2 = false;
    private static boolean winTeam3 = false;
    private static boolean winTeam4 = false;

    public static void startGame2() throws IOException {
        winTeam1 = false;
        winTeam2 = false;
        winTeam3 = false;
        winTeam4 = false;
        Started = true;
        gameManager.setPvPAllowed(false);
        CalculateCuboid.start();
        generateTeams();
        gameManager.onExplainStart("second");
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            //START
            for (UUID uuid:gameManager.getAlivePlayers()) {
                Bukkit.getPlayer(uuid).setGameMode(GameMode.CREATIVE);
            }
            gameManager.setBlockAllowed(true);
            gameManager.setPvPAllowed(false);
        }, 20L * 15);
    }

    public static void endGame2() {
        if (Started) {
            gameManager.setPvPAllowed(false);
            gameManager.setBlockAllowed(false);
            if (!winTeam1) {
                for (Player player:team1) {
                    player.sendTitle(gameManager.formatMessage(player,"events.game-timeout-died.title") ,
                            gameManager.formatMessage(player,"events.game-timeout-died.subtitle"),10, 30,10);
                    gameManager.killPlayer(player);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }
            if (!winTeam2) {
                for (Player player:team2) {
                    player.sendTitle(gameManager.formatMessage(player,"events.game-timeout-died.title") ,
                            gameManager.formatMessage(player,"events.game-timeout-died.subtitle"),10, 30,10);
                    gameManager.killPlayer(player);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }
            if (!winTeam3) {
                for (Player player:team3) {
                    player.sendTitle(gameManager.formatMessage(player,"events.game-timeout-died.title") ,
                            gameManager.formatMessage(player,"events.game-timeout-died.subtitle"),10, 30,10);
                    gameManager.killPlayer(player);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }
            if (!winTeam4) {
                for (Player player:team4) {
                    player.sendTitle(gameManager.formatMessage(player,"events.game-timeout-died.title") ,
                            gameManager.formatMessage(player,"events.game-timeout-died.subtitle"),10, 30,10);
                    gameManager.killPlayer(player);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }
            //End Code
            Bukkit.getScheduler().runTaskLater(plugin, () -> gameManager.intermission(Games.Game3), 20L * plugin.getConfig().getInt("endgame-time"));
        }
    }

    private static void generateTeams() {
        Collections.shuffle(gameManager.getAlivePlayers());
        for (int i = 0;(gameManager.getAlivePlayers().size() / 2) > i;i++) {
            UUID uuid = gameManager.getAlivePlayers().get(i);
            Player player = Bukkit.getPlayer(uuid);
            team1.add(player);
            player.getInventory().setArmorContents(getArmour(Color.RED));
            player.teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Game2.spawn_red")));
        }
        for (int i = team1.size();gameManager.getAlivePlayers().size() > i;i++) {
            UUID uuid = gameManager.getAlivePlayers().get(i);
            Player player = Bukkit.getPlayer(uuid);
            team2.add(player);
            player.getInventory().setArmorContents(getArmour(Color.BLUE));
            player.teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Game2.spawn_blue")));
        }
        Collections.shuffle(team1);
        for (int i = 0;(team1.size() / 2) > i;i++) {
            Player player = team1.get(i);
            team1.remove(player);
            team3.add(player);
            player.getInventory().setArmorContents(getArmour(Color.GREEN));
            player.teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Game2.spawn_green")));
        }
        Collections.shuffle(team2);
        for (int i = 0;(team2.size()/ 2) > i;i++) {
            Player player = team2.get(i);
            team2.remove(player);
            team4.add(player);
            player.getInventory().setArmorContents(getArmour(Color.YELLOW));
            player.teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Game2.spawn_yellow")));
        }
    }

    private static ItemStack[] getArmour(Color colour) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(colour);
        helmet.setItemMeta(meta);
        LeatherArmorMeta meta1 = (LeatherArmorMeta) chest.getItemMeta();
        meta1.setColor(colour);
        chest.setItemMeta(meta1);
        LeatherArmorMeta meta2 = (LeatherArmorMeta) pants.getItemMeta();
        meta2.setColor(colour);
        pants.setItemMeta(meta2);
        LeatherArmorMeta meta3 = (LeatherArmorMeta) boots.getItemMeta();
        meta3.setColor(colour);
        boots.setItemMeta(meta3);

        ArrayList<ItemStack> list = new ArrayList<>();
        list.add(boots);
        list.add(pants);
        list.add(chest);
        list.add(helmet);
        return list.toArray(new ItemStack[0]);
    }

    private static void checkGameComplete() {
        if ((team1.size() > 0?1:0) + (team2.size() > 0?1:0) + (team3.size() > 0?1:0) + (team4.size() > 0?1:0) == 4) {
            if ((winTeam1?1:0) + (winTeam2?1:0) + (winTeam3?1:0) + (winTeam4?1:0) == 3) {
                endGame2();
            }
        } else if ((team1.size() > 0?1:0) + (team2.size() > 0?1:0) + (team3.size() > 0?1:0) + (team4.size() > 0?1:0) == 3) {
            if ((winTeam1?1:0) + (winTeam2?1:0) + (winTeam3?1:0) + (winTeam4?1:0) == 2) {
                endGame2();
            }
        } else if ((team1.size() > 0?1:0) + (team2.size() > 0?1:0) + (team3.size() > 0?1:0) + (team4.size() > 0?1:0) == 2) {
            if ((winTeam1?1:0) + (winTeam2?1:0) + (winTeam3?1:0) + (winTeam4?1:0) == 1) {
                endGame2();
            }
        }
    }

    public static void completeTeam1() {
        winTeam1 = true;
        for (Player player: team1) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle(gameManager.formatMessage(player,"events.game-pass.title") , gameManager.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            //send message saying team 1 finished
        }
        checkGameComplete();
    }

    public static void completeTeam2() {
        winTeam2 = true;
        for (Player player: team2) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle(gameManager.formatMessage(player,"events.game-pass.title") , gameManager.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            //send message saying team 1 finished
        }
        checkGameComplete();
    }

    public static void completeTeam3() {
        winTeam3 = true;
        for (Player player: team3) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle(gameManager.formatMessage(player,"events.game-pass.title") , gameManager.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            //send message saying team 1 finished
        }
        checkGameComplete();
    }

    public static void completeTeam4() {
        winTeam4 = true;
        for (Player player: team4) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle(gameManager.formatMessage(player,"events.game-pass.title") , gameManager.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            //send message saying team 1 finished
        }
        checkGameComplete();
    }

    public static boolean getWinTeam1() {
        return winTeam1;
    }

    public static boolean getWinTeam2() {
        return winTeam2;
    }

    public static boolean getWinTeam3() {
        return winTeam3;
    }

    public static boolean getWinTeam4() {
        return winTeam4;
    }

    public static ArrayList<Player> getTeam1() {
        return team1;
    }

    public static ArrayList<Player> getTeam2() {
        return team2;
    }

    public static ArrayList<Player> getTeam3() {
        return team3;
    }

    public static ArrayList<Player> getTeam4() {
        return team4;
    }

    public static boolean isStarted() {
        return Started;
    }

    public static void reloadConfig() {
        CalculateCuboid.reloadCuboids();
    }
}
