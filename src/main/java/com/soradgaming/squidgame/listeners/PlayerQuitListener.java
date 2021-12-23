package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Arena arena = Arena.getPlayerArena(player);
        if (arena != null) {
            arena.getPlayerHandler().playerLeave(player);
        }
    }
}