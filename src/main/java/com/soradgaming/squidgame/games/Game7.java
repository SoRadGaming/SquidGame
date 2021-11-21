package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.placeholders.PlayerDataType;
import com.soradgaming.squidgame.placeholders.scoreboard;
import com.soradgaming.squidgame.utils.gameManager;
import com.soradgaming.squidgame.utils.playerManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Game7 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    private static boolean Started = false;

    public static void startGame7() {
        Started = true;
        gameManager.onExplainStart("seventh");
        for (UUID uuid:gameManager.getAllPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            player.teleport(plugin.getConfig().getLocation("Game7.spawn"));
        }
        gameManager.setPvPAllowed(true);
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            //Repeat Till all players dead
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                if (gameManager.getAlivePlayers().size() == 1) {
                    plugin.data.set("winner", gameManager.getAlivePlayers().get(0).toString());
                    endGame7();
                }
            }, 20L, 20L);
        },20L * 15);
    }

    public static void endGame7() {
        if (Started) {
            gameManager.setPvPAllowed(false);
            Started = false;
            //WIN Commands
            List<String> commands =  plugin.getConfig().getStringList("rewards");
            for (String cmd:commands) {
                String command = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(plugin.data.getString("winner")))), cmd);
                Bukkit.getServer().dispatchCommand(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(plugin.data.getString("winner")))),command);
            }
            for (UUID uuid: gameManager.getAllPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                playerManager.gameStarted = false;
                playerManager.playerLeave(player);
                player.sendTitle(gameManager.formatMessage(player,"events.finish.winner.title"),gameManager.formatMessage(player,"events.finish.winner.subtitle"),10,30,10);
            }
        }

    }

    @EventHandler(ignoreCancelled = true)
    private void PlayerMoveEvent(PlayerMoveEvent event) {
        if (event.getFrom().distance(Objects.requireNonNull(event.getTo())) <= 0.015) {
            return;
        }
        Player player = event.getPlayer();
        if (!gameManager.getAlivePlayers().contains(player.getUniqueId()) || !Started) {
            return;
        }
        final Location location = event.getTo().clone();
        final String killBlock = plugin.getConfig().getString("Game7.kill-block", "sand");
        location.subtract(0, 1, 0);

        if (location.getBlock() != null && location.getBlock().getType() != null
                && location.getBlock().getType().toString().equalsIgnoreCase(killBlock)) {
            gameManager.killPlayer(player);
        }
    }

    //TODO Override Death
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Player player = e.getEntity();

        if (Started && gameManager.getAllPlayers().contains(player.getUniqueId()) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
            if (player.getKiller() != null) {
                plugin.data.set(player.getKiller().getUniqueId() + ".kills", plugin.data.getInt(player.getKiller().getUniqueId() + ".kills") + 1);
            }
            if (plugin.getConfig().getBoolean("eliminate-players")) {
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(plugin.getConfig().getLocation("Lobby"));
                gameManager.killPlayer(player);
            } else {
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(plugin.getConfig().getLocation("Lobby"));
            }
        }
    }

    private static ItemStack[] getArmour() {
        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE);
        ItemStack pants = new ItemStack(Material.IRON_LEGGINGS);
        ItemStack boots = new ItemStack(Material.IRON_BOOTS);

        ArrayList<ItemStack> list = new ArrayList<>();
        list.add(boots);
        list.add(pants);
        list.add(chest);
        list.add(helmet);
        return list.toArray(new ItemStack[0]);
    }

    public static boolean isStarted() {
        return Started;
    }

}
