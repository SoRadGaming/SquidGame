package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.utils.gameManager;
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
            if (player != null && gameManager.getAllPlayers() != null && gameManager.getAllPlayers().contains(player.getUniqueId())) {
                if (e.getCause() == DamageCause.ENTITY_ATTACK && !gameManager.isPvPAllowed()) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
