package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.games.Games;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


public class EntityDamageByEntityListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageByEntityEvent e) {
        final Entity entity = e.getEntity();
        final Entity killerEntity = e.getDamager();
        if (entity instanceof Player && killerEntity instanceof Player) {
            final Player player = ((Player) entity).getPlayer();
            final Player killer = ((Player) killerEntity).getPlayer();
            if (player == null || killer == null) {
                return;
            }
            Arena arena = Arena.getPlayerArena(player);
            if (arena == null) {
                return;
            }
            Games games = arena.getGameHandler().getCurrentGame();
            if (games == null) {
                return;
            }
            if (!arena.getGameHandler().isPvPAllowed()) {
                e.setCancelled(true);
            } else if (games.equals(Games.Game1)) {
                e.setDamage(0);
            } else if (games.equals(Games.Game3)) {
                //Player Dies
                if (player.getHealth() - e.getDamage() <= 0) {
                    e.setDamage(0);
                    arena.getGameHandler().dormsBattle.onPlayerDeathKilled(player);
                }
            } else if (games.equals(Games.Game4)) {
                e.setDamage(0);
                arena.getGameHandler().grabTheCrown.onGrabEvent(player, killer);
            } else if (games.equals(Games.Game6)) {
                e.setDamage(0);
            } else if (games.equals(Games.Game7)) {
                //Player Dies
                if (player.getHealth() - e.getDamage() <= 0) {
                    e.setDamage(0);
                    arena.getGameHandler().sumo.onPlayerDeath(player);
                }
            }
        }

    }
}
