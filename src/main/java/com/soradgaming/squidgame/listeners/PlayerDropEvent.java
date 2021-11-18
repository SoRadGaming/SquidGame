package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.utils.gameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropEvent implements Listener {
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (gameManager.getAllPlayers().contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
