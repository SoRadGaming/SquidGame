package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.utils.gameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent e) {
        final Player player = e.getPlayer();
        if (gameManager.getAllPlayers().contains(player.getUniqueId()) && !gameManager.isBlockAllowed()) {
            e.setCancelled(true);
        }
    }
}