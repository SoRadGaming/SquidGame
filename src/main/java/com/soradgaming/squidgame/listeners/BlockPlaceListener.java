package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Zones;
import com.soradgaming.squidgame.games.Games;
import com.soradgaming.squidgame.games.SpeedBuilders;
import com.soradgaming.squidgame.math.CalculateCuboid;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        final Player player = e.getPlayer();
        Arena arena = Arena.getPlayerArena(player);
        if (arena == null) {
            return;
        }
        Games games = arena.getGameHandler().getCurrentGame();
        if (games == null) {
            return;
        }
        if (games.equals(Games.Game2)) {
            SpeedBuilders speedBuilders = arena.getGameHandler().speedBuilders;
            if (speedBuilders == null) {
                return;
            }
            CalculateCuboid calculateCuboid = speedBuilders.calculateCuboid;
            Location loc = e.getBlock().getLocation();
            if (speedBuilders.getTeam1().contains(player) && calculateCuboid.getZones(Zones.BuildZone1).contains(loc)) {
                //Allow Build
                calculateCuboid.loop();
                e.setCancelled(false);
            } else if (speedBuilders.getTeam2().contains(player) && calculateCuboid.getZones(Zones.BuildZone2).contains(loc)) {
                //Allow Build
                calculateCuboid.loop();
                e.setCancelled(false);
            } else if (speedBuilders.getTeam3().contains(player) && calculateCuboid.getZones(Zones.BuildZone3).contains(loc)) {
                //Allow Build
                calculateCuboid.loop();
                e.setCancelled(false);
            } else if (speedBuilders.getTeam4().contains(player) && calculateCuboid.getZones(Zones.BuildZone4).contains(loc)) {
                //Allow Build
                calculateCuboid.loop();
                e.setCancelled(false);
            } else if (speedBuilders.getTeam1().contains(player) || speedBuilders.getTeam2().contains(player) ||
                    speedBuilders.getTeam3().contains(player) || speedBuilders.getTeam4().contains(player)) {
                e.setCancelled(true);
            }
        } else if (arena.getPlayerHandler().getAllPlayers().contains(player) && !arena.getGameHandler().isBlockAllowed()) {
            e.setCancelled(true);
        }
    }
}
