package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropEvent implements Listener {
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Arena arena = Arena.getPlayerArena(player);
        if (arena != null) {
            event.setCancelled(true);
        }
    }
}
