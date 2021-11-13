package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.SquidGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        //Nothing to Do
    }
}