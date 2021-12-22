package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Messages;
import com.soradgaming.squidgame.math.CalculateCuboid;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

public class SpeedBuilders implements Runnable, Listener {
    private SquidGame plugin;
    private Arena arena;
    private CalculateCuboid calculateCuboid;
    private boolean Started = false;
    private ArrayList<Player> team1 = new ArrayList<>();
    private ArrayList<Player> team2 = new ArrayList<>();
    private ArrayList<Player> team3 = new ArrayList<>();
    private ArrayList<Player> team4 = new ArrayList<>();
    private boolean winTeam1 = false;
    private boolean winTeam2 = false;
    private boolean winTeam3 = false;
    private boolean winTeam4 = false;

    public SpeedBuilders(SquidGame plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @Override
    public void run() {
        winTeam1 = false;
        winTeam2 = false;
        winTeam3 = false;
        winTeam4 = false;
        Started = true;
        arena.getGameHandler().setPvPAllowed(false);
        calculateCuboid = new CalculateCuboid(this,arena);
        calculateCuboid.run();
        generateTeams();
        Messages.onExplainStart(arena.getPlayerHandler().getAllPlayers(),"second");
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            //START
            for (Player player:arena.getPlayerHandler().getAlivePlayers()) {
                player.setGameMode(GameMode.CREATIVE);
            }
            arena.getGameHandler().setBlockAllowed(true);
            arena.getGameHandler().setPvPAllowed(false);
        }, 20L * 15);
    }

    public void endGame2() {
        if (Started) {
            arena.getGameHandler().setPvPAllowed(false);
            arena.getGameHandler().setBlockAllowed(false);
            if (!winTeam1) {
                for (Player player:team1) {
                    player.sendTitle(Messages.formatMessage(player,"events.game-timeout-died.title") ,
                            Messages.formatMessage(player,"events.game-timeout-died.subtitle"),10, 30,10);
                    arena.getPlayerHandler().killPlayer(player);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }
            if (!winTeam2) {
                for (Player player:team2) {
                    player.sendTitle(Messages.formatMessage(player,"events.game-timeout-died.title") ,
                            Messages.formatMessage(player,"events.game-timeout-died.subtitle"),10, 30,10);
                    arena.getPlayerHandler().killPlayer(player);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }
            if (!winTeam3) {
                for (Player player:team3) {
                    player.sendTitle(Messages.formatMessage(player,"events.game-timeout-died.title") ,
                            Messages.formatMessage(player,"events.game-timeout-died.subtitle"),10, 30,10);
                    arena.getPlayerHandler().killPlayer(player);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }
            if (!winTeam4) {
                for (Player player:team4) {
                    player.sendTitle(Messages.formatMessage(player,"events.game-timeout-died.title") ,
                            Messages.formatMessage(player,"events.game-timeout-died.subtitle"),10, 30,10);
                    arena.getPlayerHandler().killPlayer(player);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }
            //End Code
            Bukkit.getScheduler().runTaskLater(plugin, () -> arena.getGameHandler().intermission(Games.Game3), 20L * arena.getStructureManager().getEndTime());
        }
    }

    private void generateTeams() {
        List<Player> playerList = arena.getPlayerHandler().getAlivePlayers();
        Collections.shuffle(playerList);
        for (int i = 0;(playerList.size() / 2) > i;i++) {
            Player player = playerList.get(i);
            team1.add(player);
            player.getInventory().setArmorContents(getArmour(Color.RED));
            player.teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Game2.spawn_red")));
        }
        for (int i = team1.size();playerList.size() > i;i++) {
            Player player = playerList.get(i);
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

    private ItemStack[] getArmour(Color colour) {
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

    private void checkGameComplete() {
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

    public void completeTeam1() {
        winTeam1 = true;
        for (Player player: team1) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle(Messages.formatMessage(player,"events.game-pass.title") , Messages.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            //send message saying team 1 finished
        }
        checkGameComplete();
    }

    public void completeTeam2() {
        winTeam2 = true;
        for (Player player: team2) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle(Messages.formatMessage(player,"events.game-pass.title") , Messages.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            //send message saying team 1 finished
        }
        checkGameComplete();
    }

    public void completeTeam3() {
        winTeam3 = true;
        for (Player player: team3) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle(Messages.formatMessage(player,"events.game-pass.title") , Messages.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            //send message saying team 1 finished
        }
        checkGameComplete();
    }

    public void completeTeam4() {
        winTeam4 = true;
        for (Player player: team4) {
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.SPECTATOR);
            player.sendTitle(Messages.formatMessage(player,"events.game-pass.title") , Messages.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            //send message saying team 1 finished
        }
        checkGameComplete();
    }

    public boolean getWinTeam1() {return winTeam1;}

    public boolean getWinTeam2() {
        return winTeam2;
    }

    public boolean getWinTeam3() {
        return winTeam3;
    }

    public boolean getWinTeam4() {
        return winTeam4;
    }

    public ArrayList<Player> getTeam1() {
        return team1;
    }

    public ArrayList<Player> getTeam2() {
        return team2;
    }

    public ArrayList<Player> getTeam3() {
        return team3;
    }

    public ArrayList<Player> getTeam4() {
        return team4;
    }

    public boolean isStarted() {
        return Started;
    }

    public void reloadConfig() {calculateCuboid.reloadCuboids();}
}
