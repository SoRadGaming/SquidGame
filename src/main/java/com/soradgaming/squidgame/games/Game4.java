package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.math.Cuboid;
import com.soradgaming.squidgame.utils.gameManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BlockVector;

import java.util.*;

public class Game4 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    private static ArrayList<UUID> playerList;
    private static ArrayList<UUID> team1 = new ArrayList<>(); //Red
    private static ArrayList<UUID> team2 = new ArrayList<>(); //Blue
    private static Team team1RedBukkit;
    private static Team team2BlueBukkit;
    private static boolean Started = false;
    private static final BukkitScheduler gameTimer = Bukkit.getScheduler();
    private static final BukkitScheduler bossBarProgress = Bukkit.getScheduler();
    private static BossBar bossBar;
    private static double timerInterval;
    public static int timeGlobal;
    private static Cuboid goalRed;
    private static Cuboid goalBlue;
    private static Cuboid barrierZone;

    public static void startGame4(ArrayList<UUID> input) {
        playerList = input;
        Started = true;
        team1.clear();
        team2.clear();
        for (Block block : getBarrier().getBlocks()) {
            if (block.getType() == Material.AIR) {
                block.setType(Material.BARRIER);
            }
        }
        for (UUID uuid:playerList) {
            Player player = Bukkit.getPlayer(uuid);
            bossBar.addPlayer(Objects.requireNonNull(player));
        }
        timeGlobal = plugin.getConfig().getInt("Game4.timer");
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBar = Bukkit.createBossBar(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds , BarColor.BLUE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(0);
        timerInterval = (1 / (double) timeGlobal);
        //team1RedBukkit.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
        onExplainStart("fourth");
        teamGenerator();
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            gameTimer.runTaskLater(plugin, Game4::endGame4, 20L * (plugin.getConfig().getInt("Game4.timer") + 1));
            bossBarProgress.runTaskTimer(plugin, Game4::bossBarProgress, 20L, 20L);
            //START
            for (Block block : getBarrier().getBlocks()) {
                if (block.getType() == Material.BARRIER) {
                    block.setType(Material.AIR);
                }
            }
            for (UUID uuid:playerList) {
                Player player = Bukkit.getPlayer(uuid);
                player.getInventory().setItemInMainHand(new ItemStack(Material.STICK));
            }
            //TODO Set PVP ON
        }, 20L * 15);
    }

    public static void bossBarProgress() {
        double bossBarProgress = bossBar.getProgress();
        if (bossBarProgress + timerInterval < 1) {
            bossBar.setProgress(bossBarProgress + timerInterval);
        }
        timeGlobal = timeGlobal - 1;
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBar.setTitle(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds);
    }

    private static void teamGenerator() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        team1RedBukkit = board.registerNewTeam("Red Team");
        team2BlueBukkit = board.registerNewTeam("Blue Team");
        Collections.shuffle(playerList);
        for (int i = 0;(playerList.size() / 2) > i;i++) {
            UUID uuid = playerList.get(i);
            Player player = Bukkit.getPlayer(uuid);
            team1.add(uuid);
            team1RedBukkit.addEntry(player.getName());
            player.getInventory().setArmorContents(getArmour(Color.RED));
            player.teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Game4.spawn_red")));
        }
        for (int i = team1.size();playerList.size() > i;i++) {
            UUID uuid = playerList.get(i);
            Player player = Bukkit.getPlayer(uuid);
            team2.add(uuid);
            team2BlueBukkit.addEntry(player.getName());
            player.getInventory().setArmorContents(getArmour(Color.BLUE));
            player.teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Game4.spawn_blue")));
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
            team2BlueBukkit.unregister();
            team1RedBukkit.unregister();
            //Set winning team and revive team players
            for (UUID uuid : gameManager.getAlivePlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                Objects.requireNonNull(player).setHealth(20);
                player.setFoodLevel(20);
                player.getInventory().setArmorContents(null);
                player.sendTitle(gameManager.formatMessage(player,"events.game-pass.title") , gameManager.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            }
            //TODO end code
            Game6.startGame6(gameManager.getAllPlayers());
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

    public static Cuboid getBarrier() {
        if (barrierZone == null) {
            BlockVector vector1 = gameManager.configToVectors("Game4.barrier.first_point");
            BlockVector vector2 = gameManager.configToVectors("Game4.barrier.second_point");
            World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game4.world")));
            barrierZone = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
        }
        return barrierZone;
    }
}
