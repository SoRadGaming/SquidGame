package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.utils.gameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Game4 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    private static ArrayList<UUID> playerList;
    private static ArrayList<UUID> team1; //Red
    private static ArrayList<UUID> team2; //Blue
    private static Team team1RedBukkit;
    private static Team team2BlueBukkit;
    private static boolean Started = false;
    private static final BukkitScheduler gameTimer = Bukkit.getScheduler();

    public static void startGame4(ArrayList<UUID> input) {
        playerList = input;
        Started = true;
        onExplainStart("fourth");
        teamGenerator();
        for (UUID uuid:playerList) {
            Player player = Bukkit.getPlayer(uuid);
            //TODO give item (wooden sword) add teams
        }
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            gameTimer.runTaskLater(plugin, Game4::endGame4, 20L * (plugin.getConfig().getInt("Game4.timer") + 1));
            //START
            //TODO Set PVP ON
        }, 20L * 15);
    }

    private static void teamGenerator() {
        Collections.shuffle(playerList);
        for (int i = 0;(playerList.size() / 2) > i;i++) {
            team1.add(playerList.get(i));
            team1RedBukkit.addEntry(Bukkit.getPlayer(playerList.get(i)).getName());
        }
        for (int i = team1.size();playerList.size() >= i;i++) {
            team2.add(playerList.get(i));
            team2BlueBukkit.addEntry(Bukkit.getPlayer(playerList.get(i)).getName());
        }
        team1RedBukkit.setColor(ChatColor.RED);
        team1RedBukkit.setAllowFriendlyFire(false);
        team1RedBukkit.setDisplayName(ChatColor.RED + "Red Team");
        team2BlueBukkit.setColor(ChatColor.BLUE);
        team2BlueBukkit.setAllowFriendlyFire(false);
        team2BlueBukkit.setDisplayName(ChatColor.BLUE + "Blue Team");
    }

    public static void endGame4() {
        gameTimer.cancelTasks(plugin);
        if (Started) {
            //TODO Set PVP off
            for (UUID uuid: playerList) {
                Player player = Bukkit.getPlayer(uuid);
                Objects.requireNonNull(player).setHealth(20);
                player.setFoodLevel(20);
            }
            for (UUID uuid : gameManager.getAlivePlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                Objects.requireNonNull(player).sendTitle(gameManager.formatMessage(player,"events.game-pass.title") , gameManager.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            }
            //TODO end code
        }
    }

    public static void broadcastTitle(final String title, final String subtitle , int time) {
        for (final UUID uuid : playerList) {
            Player player = Bukkit.getPlayer(uuid);
            Objects.requireNonNull(player).sendTitle(gameManager.formatMessage(player,title) , gameManager.formatMessage(player,subtitle),10, time * 20,10);
        }
    }

    public static void broadcastTitleAfterSeconds(int seconds, final String title, final String subtitle) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> broadcastTitle(title, subtitle, 2), seconds * 20L);
    }

    public static void onExplainStart(String input) {
        final String key = "games." + input + ".tutorial";
        broadcastTitleAfterSeconds(3, key + ".1.title", key + ".1.subtitle");
        broadcastTitleAfterSeconds(6, key + ".2.title", key + ".2.subtitle");
        broadcastTitleAfterSeconds(9, key + ".3.title", key + ".3.subtitle");
        broadcastTitleAfterSeconds(12, key + ".4.title", key + ".4.subtitle");
        broadcastTitleAfterSeconds(15, "events.game-start.title", "events.game-start.subtitle");
    }

    public static ArrayList<ItemStack> getArmour(Color colour) {
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
        list.add(helmet);
        list.add(chest);
        list.add(pants);
        list.add(boots);
        return list;
    }
}
