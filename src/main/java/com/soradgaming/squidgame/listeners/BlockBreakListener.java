package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.games.Game2;
import com.soradgaming.squidgame.games.Zones;
import com.soradgaming.squidgame.math.CalculateCuboid;
import com.soradgaming.squidgame.utils.gameManager;
import com.soradgaming.squidgame.utils.playerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import static com.soradgaming.squidgame.math.CalculateCuboid.loop;

public class BlockBreakListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        final Player player = e.getPlayer();
            if (Game2.isStarted()) {
                Location loc = e.getBlock().getLocation();
                if (Game2.getTeam1().contains(player) && CalculateCuboid.getZones(Zones.BuildZone1).contains(loc)) {
                    //Allow Build
                    loop();
                    e.setCancelled(false);
                    return;
                } else if (Game2.getTeam2().contains(player) && CalculateCuboid.getZones(Zones.BuildZone2).contains(loc)) {
                    //Allow Build
                    loop();
                    e.setCancelled(false);
                    return;
                } else if (Game2.getTeam3().contains(player) && CalculateCuboid.getZones(Zones.BuildZone3).contains(loc)) {
                    //Allow Build
                    loop();
                    e.setCancelled(false);
                    return;
                } else if (Game2.getTeam4().contains(player) && CalculateCuboid.getZones(Zones.BuildZone4).contains(loc)) {
                    //Allow Build
                    loop();
                    e.setCancelled(false);
                    return;
                } else {
                    loop();
                    e.setCancelled(true);
                }
                loop();
            } else if (gameManager.getAllPlayers().contains(player.getUniqueId()) && !gameManager.isBlockAllowed()) {
                e.setCancelled(true);
            }
    }
}