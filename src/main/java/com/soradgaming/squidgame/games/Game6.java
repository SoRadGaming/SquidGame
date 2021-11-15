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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Game6 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    private static ArrayList<UUID> playerList;
    private static boolean Started = false;
    private static final BukkitScheduler gameTimer = Bukkit.getScheduler();
    private static final BukkitScheduler bossBarProgress = Bukkit.getScheduler();
    private static BossBar bossBar;
    private static double timerInterval;
    public static int timeGlobal;
    private static Cuboid glassZone;
    private static Cuboid goalZone;
    private static ArrayList<Block> fakeBlocks = new ArrayList<>();

    public static void startGame6(ArrayList<UUID> input) {
        playerList = input;
        Started = true;
        timeGlobal = plugin.getConfig().getInt("Game6.timer");
        int minutes = (timeGlobal/60);
        int seconds = (timeGlobal - (minutes * 60));
        bossBar = Bukkit.createBossBar(ChatColor.BOLD + "Game Timer : " + ChatColor.GOLD + minutes + ":" + ChatColor.GOLD + seconds , BarColor.BLUE, BarStyle.SOLID);
        bossBar.setVisible(true);
        bossBar.setProgress(0);
        //Generate Glass
        generateTiles(Material.GLASS);
        for (UUID uuid : playerList) {
            Player p = Bukkit.getPlayer(uuid);
            Objects.requireNonNull(p).teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Game6.spawn")));
            bossBar.addPlayer(Objects.requireNonNull(p));
        }
        onExplainStart("sixth");
        timerInterval = (1 / (double) timeGlobal);
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            gameTimer.runTaskLater(plugin, Game6::endGame6, 20L * (timeGlobal + 1));
            bossBarProgress.runTaskTimer(plugin, Game6::bossBarProgress, 20L, 20L);
            //START
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

    public static void endGame6() {
        gameTimer.cancelTasks(plugin);
        bossBarProgress.cancelTasks(plugin);
        if (Started) {
            bossBar.removeAll();
            bossBar.setVisible(false);
            broadcastTitle("events.game-timeout.title", "events.game-timeout.subtitle", 5);
            Started = false;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (final UUID uuid : gameManager.getDeadPlayerList()) {
                    Player player = Bukkit.getPlayer(uuid);
                    Objects.requireNonNull(player).sendTitle(gameManager.formatMessage(player,"events.game-timeout-died.title") , gameManager.formatMessage(player,"events.game-timeout-died.subtitle"),10, 30,10);
                    Objects.requireNonNull(player).teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Lobby")));
                }
                for (final UUID uuid : gameManager.getPlayerList()) {
                    Player player = Bukkit.getPlayer(uuid);
                    Objects.requireNonNull(player).sendTitle(gameManager.formatMessage(player,"events.game-pass.title") , gameManager.formatMessage(player,"events.game-pass.subtitle"),10, 30,10);
                    Objects.requireNonNull(player).teleport(Objects.requireNonNull(plugin.getConfig().getLocation("Lobby")));
                }
            }, 40L);
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

    private static Cuboid getGlassZone() {
        if (glassZone == null) {
            BlockVector vector1 = gameManager.configToVectors("Game6.barrier.first_point");
            BlockVector vector2 = gameManager.configToVectors("Game6.barrier.second_point");
            World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game6.world")));
            glassZone = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());

        }
        return glassZone;
    }

    public Cuboid getGoalZone() {
        if (goalZone == null) {
            BlockVector vector1 = gameManager.configToVectors("Game6.barrier.first_point");
            BlockVector vector2 = gameManager.configToVectors("Game6.barrier.second_point");
            World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game6.world")));
            goalZone = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
        }

        return goalZone;
    }

    @EventHandler(ignoreCancelled = true)
    public void PlayerMoveEvent(@NotNull PlayerMoveEvent e) {
        if (e.getFrom().distance(Objects.requireNonNull(e.getTo())) <= 0.015) {
            return;
        }
        Player player = e.getPlayer();
        if (e.getPlayer().getGameMode().equals(GameMode.SPECTATOR) && !playerList.contains(player.getUniqueId())) {
            return;
        }
        final Location loc = Objects.requireNonNull(e.getTo()).clone().subtract(0, 1, 0);
        final Block block = loc.getBlock();

        if (block.getType() == Material.GLASS) {
            if (fakeBlocks.contains(block)) {
                gameManager.killPlayer(player);
                player.setGameMode(GameMode.SPECTATOR);
            }
            // now we checked that they are in this game
        }
    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Player player = e.getEntity();
        if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
            gameManager.killPlayer(player);
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    private static void generateTiles(final Material material) {
        final World world = getGlassZone().getWorld();

        final BlockVector first = getGlassZone().getFirstPoint();
        final BlockVector second = getGlassZone().getSecondPoint();

        world.getBlockAt(first.toLocation(world)).setType(Material.GLASS);
        world.getBlockAt(second.toLocation(world)).setType(Material.GLASS);

        final int differenceBetweenX = getGlassZone().getSizeX();
        final int differenceBetweenZ = getGlassZone().getSizeZ();

        final boolean useZAsIndex = differenceBetweenZ > differenceBetweenX;
        final boolean shouldIncreaseIndex = useZAsIndex ? first.getZ() < second.getZ() : first.getX() < second.getX();
        final int groundWidth = (useZAsIndex ? differenceBetweenX : differenceBetweenZ) + 1;getGlassZone().getSizeY();
        final int groundHeight = (useZAsIndex ? differenceBetweenZ : differenceBetweenX) + 1;

        // Platform Parameters
        final int size = groundWidth < 5 ? 1 : groundWidth < 7 ? 2 : 3;
        final int spaceXBetweenPlatforms = groundWidth - (size * 2);
        final int spaceZBetweenPlatforms = 3;

        // Indice de bloque (Incrementa o decrementa relativamente a la dirección de
        // generación)
        int blockIndex = (int) (useZAsIndex ? first.getZ() : first.getX());

        // Inicio del X, valor inmutable y absoluto.
        final int xStart = Math.min((int) first.getX(), (int) second.getX());
        // Inicio del Y, valor inmutable y absoluto
        final int yStart = (int) first.getY();
        // Inicio del Z, valor inmutable y absoluto.
        final int zStart = Math.max((int) first.getZ(), (int) second.getZ());

        // Obtener el número de pares de plataformas a generar dependiendo el tamaño
        final int platformGroups = groundHeight / (spaceZBetweenPlatforms + size);

        // Por cada grupo de plataforma (+ 1) iterar:
        for (int i = 0; i <= platformGroups; i++) {
            // Is first pair item a fake block?
            Random random = new Random();
            boolean isFirstFake = random.nextBoolean();

            // Por cada posición relativa X de la plataforma
            for (int xPadding = 0; xPadding < size; xPadding++) {
                // Por cada posición relativa Y de la plataforma
                for (int zPadding = 0; zPadding < size; zPadding++) {
                    // Define blocks
                    Block firstRowBlock, secondRowBlock;

                    // En caso que la coordenada Z deba de usarse como un indice:
                    if (useZAsIndex) {
                        // Sumarle valor relativo x padding al valor absoluto x start
                        int x = xStart + xPadding;
                        // Sumarle el valor del indice Z al valor relativo z padding
                        int z = shouldIncreaseIndex ? blockIndex + zPadding : blockIndex - zPadding;

                        // Generar bloque en las coordenadas dadas X Y Z
                        firstRowBlock = world.getBlockAt(x, yStart, z);

                        // Generar bloque en la misma posicion que el de arriba pero con una separación
                        secondRowBlock = world.getBlockAt(x + spaceXBetweenPlatforms + size, yStart, z);
                    } else {
                        // Sumarle el valor del indice X al valor relativo x padding
                        int x = shouldIncreaseIndex ? blockIndex + xPadding : blockIndex - xPadding;
                        // Sumarle valor relativo z padding al valor absoluto z start
                        int z = zStart + zPadding;

                        // Generar bloque en las coordenadas dadas X Y Z
                        firstRowBlock = world.getBlockAt(x, yStart, z);

                        // Generar bloque en la misma posicion que el de arriba pero con una separación
                        secondRowBlock = world.getBlockAt(x, yStart, z + spaceXBetweenPlatforms + size);
                    }

                    firstRowBlock.setType(material);
                    secondRowBlock.setType(material);

                    if (material != Material.AIR) {
                        if (isFirstFake) {
                            fakeBlocks.add(firstRowBlock);
                        } else {
                            fakeBlocks.add(secondRowBlock);
                        }
                    }
                }
            }

            final int separation = spaceZBetweenPlatforms + size;
            blockIndex = shouldIncreaseIndex ? blockIndex + separation : blockIndex - separation;
        }
    }
}
