package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Messages;
import com.soradgaming.squidgame.arena.Status;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Sumo implements Runnable {
    private SquidGame plugin;
    private Arena arena;
    private boolean Started = false;

    public Sumo(SquidGame plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @Override
    public void run() {
        Started = true;
        Messages.onExplainStart(arena.getPlayerHandler().getAllPlayers(),"seventh");
        for (Player player:arena.getPlayerHandler().getAllPlayers()) {
            player.teleport(plugin.getConfig().getLocation("Game7.spawn"));
        }
        // With BukkitScheduler
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            arena.getGameHandler().setPvPAllowed(true);
            //Repeat Till all players dead
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                if (arena.getPlayerHandler().getAlivePlayers().size() == 1) {
                    Player player = arena.getPlayerHandler().getAlivePlayers().get(0).getPlayer();
                    plugin.data.set("winner", player.getName());
                    UUID uuid = player.getUniqueId();
                    int wins = plugin.data.getInt(uuid + ".wins");
                    plugin.data.set(uuid + ".wins", wins + 1);
                    arena.getGameHandler().setGameStatus(Status.Ending);
                    endGame7();
                }
            }, 20L, 20L);
        },20L * 15);
    }

    public Runnable endGame7() {
        if (Started) {
            arena.getGameHandler().setPvPAllowed(false);
            Started = false;
            List<Player> playersList = arena.getPlayerHandler().getAllPlayers();
            //WIN Commands
            List<String> commands =  plugin.getConfig().getStringList("rewards");
            for (String cmd:commands) {
                String command = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(plugin.data.getString("winner")))), cmd);
                Bukkit.getServer().dispatchCommand(Bukkit.getPlayer(UUID.fromString(Objects.requireNonNull(plugin.data.getString("winner")))),command);
            }
            for (Player player: playersList) {
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                arena.getPlayerHandler().playerLeave(player);
                player.sendTitle(Messages.formatMessage(player,"events.finish.winner.title"),Messages.formatMessage(player,"events.finish.winner.subtitle"),10,30,10);
                arena.getGameHandler().setGameStatus(Status.Offline);
            }
        }
        return null;
    }

    public void onPlayerDeath(Player player) {
        if (Started && arena.getPlayerHandler().getAllPlayers().contains(player) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
            if (player.getKiller() != null) {
                plugin.data.set(player.getKiller().getUniqueId() + ".kills", plugin.data.getInt(player.getKiller().getUniqueId() + ".kills") + 1);
            }
            if (plugin.getConfig().getBoolean("eliminate-players")) {
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(plugin.getConfig().getLocation("Game7.spawn"));
                arena.getPlayerHandler().killPlayer(player);
            } else {
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(plugin.getConfig().getLocation("Game7.spawn"));
            }
        }
    }

    private ItemStack[] getArmour() {
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

    public boolean isStarted() {
        return Started;
    }
}
