package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.math.Cuboid;
import com.soradgaming.squidgame.utils.gameManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BlockVector;

import java.util.*;

public class Game4 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    private static ArrayList<UUID> team1 = new ArrayList<>(); //Red
    private static ArrayList<UUID> team2 = new ArrayList<>(); //Blue
    private static ArrayList<UUID> team1Clone = new ArrayList<>(); //Red
    private static ArrayList<UUID> team2Clone = new ArrayList<>(); //Blue
    private static Team team1RedBukkit;
    private static Team team2BlueBukkit;
    private static boolean Started = false;
    private static Cuboid barrierZone;

    public static void startGame4() {
        Started = true;
        team1.clear();
        team2.clear();
        for (Block block : getBarrier().getBlocks()) {
            if (block.getType() == Material.AIR) {
                block.setType(Material.BARRIER);
            }
        }
        teamGenerator();
        for (UUID uuid:gameManager.getDeadPlayers()) {
            Random random = new Random();
            boolean isFirstFake = random.nextBoolean();
            if (isFirstFake) {
                Bukkit.getPlayer(uuid).teleport(plugin.getConfig().getLocation("Game4.spawn_red"));
            } else {
                Bukkit.getPlayer(uuid).teleport(plugin.getConfig().getLocation("Game4.spawn_blue"));
            }
        }
        gameManager.onExplainStart("fourth");
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            //START
            for (Block block : getBarrier().getBlocks()) {
                if (block.getType() == Material.BARRIER) {
                    block.setType(Material.AIR);
                }
            }
            ItemStack stickItem =  new ItemStack(Material.STICK);
            stickItem.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
            for (UUID uuid:gameManager.getAlivePlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                player.getInventory().setItemInMainHand(stickItem);
            }
            gameManager.setPvPAllowed(true);
            //Repeat Till all players dead
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                if (team1.size() == 0 || team2.size() == 0) {
                    endGame4();
                }
            }, 20L, 20L);
        }, 20L * 15);
    }

    public static void endGame4() {
        if (Started) {
            Started = false;
            gameManager.setPvPAllowed(false);
            team2BlueBukkit.unregister();
            team1RedBukkit.unregister();
            //Set winning team and revive team players
            if (team1.size() > 0) {
                //Team 1 has won
                for (UUID uuid: team1Clone) {
                    Player player = Bukkit.getPlayer(uuid);
                    gameManager.revivePlayer(player);
                    player.teleport(plugin.getConfig().getLocation("Game4.spawn_red"));
                    player.setGameMode(GameMode.ADVENTURE);
                }
            } else if (team2.size() > 0) {
                //Team 2 has won
                for (UUID uuid: team2Clone) {
                    Player player = Bukkit.getPlayer(uuid);
                    gameManager.revivePlayer(player);
                    player.teleport(plugin.getConfig().getLocation("Game4.spawn_blue"));
                    player.setGameMode(GameMode.ADVENTURE);
                }
            }
            for (UUID uuid : gameManager.getAllPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                player.getInventory().setArmorContents(null);
                player.getInventory().clear();
            }
            for (UUID uuid : gameManager.getAlivePlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                Objects.requireNonNull(player).setHealth(20);
                player.setFoodLevel(20);
                player.sendTitle(gameManager.formatMessage(player,"events.game-pass.title") , gameManager.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> gameManager.intermission(Games.Game6), 20L * plugin.getConfig().getInt("endgame-time"));
        }
    }

    public static void onPlayerDeathFall(Player player) {
        if (!player.getGameMode().equals(GameMode.SPECTATOR) && Started && gameManager.getAllPlayers().contains(player.getUniqueId())) {
            if (team1.contains(player.getUniqueId())) {
                team1.remove(player.getUniqueId());
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(plugin.getConfig().getLocation("Game4.spawn_red"));
            } else {
                team2.remove(player.getUniqueId());
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(plugin.getConfig().getLocation("Game4.spawn_blue"));
            }
            gameManager.killPlayer(player);
        }
    }

    private static void teamGenerator() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        team1RedBukkit = board.registerNewTeam("Red Team");
        team2BlueBukkit = board.registerNewTeam("Blue Team");
        Collections.shuffle(gameManager.getAlivePlayers());
        for (int i = 0;(gameManager.getAlivePlayers().size() / 2) > i;i++) {
            UUID uuid = gameManager.getAlivePlayers().get(i);
            Player player = Bukkit.getPlayer(uuid);
            team1.add(uuid);
            team1RedBukkit.addEntry(player.getName());
            player.getInventory().setArmorContents(getArmour(Color.RED));
            player.teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Game4.spawn_red")));
        }
        for (int i = team1.size();gameManager.getAlivePlayers().size() > i;i++) {
            UUID uuid = gameManager.getAlivePlayers().get(i);
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
        team1Clone = team1;
        team2Clone = team2;
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

    public static ArrayList<UUID> getTeam1() {
        return team1;
    }

    public static ArrayList<UUID> getTeam2() {
        return team2;
    }

    public static boolean isStarted() {
        return Started;
    }
}
