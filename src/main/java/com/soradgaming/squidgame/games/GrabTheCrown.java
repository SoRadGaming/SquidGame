package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Messages;
import com.soradgaming.squidgame.placeholders.PlayerDataType;
import com.soradgaming.squidgame.placeholders.scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GrabTheCrown implements Runnable{
    private SquidGame plugin;
    private Arena arena;
    private boolean Started = false;
    private BukkitScheduler gameTimer = Bukkit.getScheduler();
    private BukkitScheduler bossBarProgress = Bukkit.getScheduler();
    private ArrayList<Player> crownPlayers = new ArrayList<>();
    private ArrayList<Player> noCrownPlayers = new ArrayList<>();
    private BossBar bossBarRed;
    private BossBar bossBarGreen;
    private HashMap<Player,Integer> points = new HashMap<>();
    private double timerInterval;
    private int timeGlobal;
    private int PointThreshold;

    public GrabTheCrown(SquidGame plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @Override
    public void run() {
        Started = true;
        Messages.onExplainStart(arena.getPlayerHandler().getAlivePlayers(), "fourth");
        timeGlobal = arena.getStructureManager().getTimeLimit(Games.Game4);
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBarRed = Bukkit.createBossBar(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds , BarColor.RED, BarStyle.SOLID);
        bossBarGreen = Bukkit.createBossBar(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds , BarColor.GREEN, BarStyle.SOLID);
        bossBarRed.setVisible(true);
        bossBarGreen.setVisible(true);
        bossBarRed.setProgress(0);
        bossBarGreen.setProgress(0);
        timerInterval = (1 / (double) timeGlobal);
        Location spawn = arena.getStructureManager().getSpawn(Games.Game4);
        for (Player player: arena.getPlayerHandler().getAllPlayers()) {
            player.teleport(spawn);
        }
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            gameTimer.runTaskLater(plugin, endGame4(), 20L * (timeGlobal + 1));
            bossBarProgress.runTaskTimer(plugin, bossBarProgress(), 20L, 20L);
            //START
            giveCrowned();
            arena.getGameHandler().setPvPAllowed(true);
        }, 20L * 15);
    }

    public Runnable endGame4() {
        gameTimer.cancelTasks(plugin);
        bossBarProgress.cancelTasks(plugin);
        if (Started) {
            Started = false;
            bossBarRed.removeAll();
            bossBarRed.setVisible(false);
            bossBarGreen.removeAll();
            bossBarGreen.setVisible(false);
            arena.getGameHandler().setPvPAllowed(false);
            //TODO remove crown and check points
            for (Player player : arena.getPlayerHandler().getAlivePlayers()) {
                player.setHealth(20);
                player.getInventory().clear();
                player.setFoodLevel(20);
                player.getActivePotionEffects().clear();
                player.sendTitle(Messages.formatMessage(player,"events.game-pass.title") , Messages.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
            }
            Bukkit.getScheduler().runTaskLater(plugin, () -> arena.getGameHandler().intermission(Games.Game5), 20L * arena.getStructureManager().getEndTime());
        }
        return null;
    }

    public void giveCrowned() {
        List<Player> players = arena.getPlayerHandler().getAlivePlayers();
        double playerCount = players.size();
        double amountOfCrownsPre = (playerCount/100) * 25;
        double amountOfCrowns = Math.round(amountOfCrownsPre);
        Collections.shuffle(players);
        for (int i = 0;amountOfCrowns > i ;i++) {
            crownPlayers.add(players.get(i));
            bossBarGreen.addPlayer(players.get(i));
            players.get(i).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, timeGlobal, 1, false, false, false));
            //TODO Give Crown
        }
        for (Player player:players) {
            if (!crownPlayers.contains(player)) {
                noCrownPlayers.add(player);
                bossBarRed.addPlayer(player);
            }
        }
        for (Player player: players) {
            points.put(player, 0);
        }
        PointThreshold = (int) (playerCount - Math.round((playerCount/100) * 75));
    }

    private Runnable bossBarProgress() {
        //Red
        double bossBarProgress = bossBarGreen.getProgress();
        if (bossBarProgress + timerInterval < 1) {
            bossBarGreen.setProgress(bossBarProgress + timerInterval);
            bossBarRed.setProgress(bossBarProgress + timerInterval);
        }
        timeGlobal = timeGlobal - 1;
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBarGreen.setTitle(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds);
        bossBarRed.setTitle(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds);
        // Crown Counter
        for (Player player:crownPlayers) {
            points.put(player, points.get(player) + 1);
        }
        return null;
    }

    public void onGrabEvent(Player player, Player killer) {
        if (!player.getGameMode().equals(GameMode.SPECTATOR) && Started) {
            if (crownPlayers.contains(player) && noCrownPlayers.contains(killer)) {
                crownPlayers.remove(player);
                crownPlayers.add(killer);
                noCrownPlayers.remove(killer);
                noCrownPlayers.add(player);
                player.removePotionEffect(PotionEffectType.GLOWING);
                killer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, timeGlobal, 1, false, false, false));
            }
        }
    }

    public void checkCrownPoints() {
        HashMap<UUID, Integer> playerList = new HashMap<>();
        List<Player> players = new ArrayList<>();
        players.addAll(noCrownPlayers);
        players.addAll(crownPlayers);

        for (Player player: players) {
            playerList.put(player.getUniqueId(), points.get(player));
        }
        playerList = sortHashMapByValues(playerList);

        Set<UUID> leadBoardSetPoints = playerList.keySet();
        List<UUID> leadBoardPoints = new ArrayList<>(leadBoardSetPoints);
        for (int i = 0; i < PointThreshold; i++) {
            Player player = Bukkit.getPlayer(leadBoardPoints.get(i));
            bossBarRed.addPlayer(player);
        }
    }

    //Sort
    public @NotNull LinkedHashMap<UUID, Integer> sortHashMapByValues(@NotNull HashMap<UUID, Integer> passedMap) {
        List<UUID> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<UUID, Integer> sortedMap = new LinkedHashMap<>();

        for (Integer val : mapValues) {
            Iterator<UUID> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                UUID key = keyIt.next();
                Integer comp1 = passedMap.get(key);

                if (comp1.equals(val)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }
}
