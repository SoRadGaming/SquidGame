package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.games.Games;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class EntityDamageListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent e) {
        final Entity entity = e.getEntity();
        if (entity instanceof Player) {
            final Player player = ((Player) entity).getPlayer();
            if (player == null) {
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
            if (e.getCause() == DamageCause.ENTITY_ATTACK && !arena.getGameHandler().isPvPAllowed()) {
                e.setCancelled(true);
            }
            if (games.equals(Games.Game1)) {
                e.setDamage(0);
                return;
            }

            //Player Dies
            if (player.getHealth() - e.getDamage() <= 0) {
                if (e.getCause() == DamageCause.ENTITY_ATTACK) {
                    if (games.equals(Games.Game7)) {
                        arena.getGameHandler().sumo.onPlayerDeath(player);
                        e.setDamage(0);
                        e.setCancelled(true);
                        return;
                    } else if (games.equals(Games.Game3)) {
                        arena.getGameHandler().dormsBattle.onPlayerDeathKilled(player);
                        e.setDamage(0);
                        e.setCancelled(true);
                    }
                }
                if (e.getCause() == DamageCause.FALL) {
                    if (games.equals(Games.Game6)) {
                        e.setDamage(0);
                        arena.getGameHandler().glassSteppingStones.onPlayerDeathFall(player);
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
