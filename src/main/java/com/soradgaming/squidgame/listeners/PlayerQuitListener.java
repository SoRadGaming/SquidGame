package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.managment.gameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        Player player = e.getPlayer();
        gameManager.removePlayer(player);
    }
}