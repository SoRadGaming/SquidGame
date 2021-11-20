package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.games.Game4;
import com.soradgaming.squidgame.games.Game6;
import com.soradgaming.squidgame.utils.gameManager;
import org.bukkit.GameMode;
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
            if (player != null && gameManager.getAllPlayers().contains(player.getUniqueId())) {
                if (e.getCause() == DamageCause.ENTITY_ATTACK && !gameManager.isPvPAllowed()) {
                    if (Game4.isStarted()) {
                        if (Game4.getTeam1().contains(e.getEntity().getUniqueId()) && Game4.getTeam2().contains(player.getUniqueId())
                                ||Game4.getTeam2().contains(e.getEntity().getUniqueId()) && Game4.getTeam1().contains(player.getUniqueId()) ) {
                            e.setCancelled(true);
                        }
                    } else {
                        e.setCancelled(true);
                    }
                }
                if (e.getCause() == DamageCause.FALL && (Game4.isStarted()|| Game6.isStarted())) {
                    e.setCancelled(true);
                }
                if (!e.isCancelled() && player.getHealth() - e.getDamage() <= 0) {
                    gameManager.killPlayer(player);
                    e.setCancelled(true);
                }
            }
        }
    }
}
