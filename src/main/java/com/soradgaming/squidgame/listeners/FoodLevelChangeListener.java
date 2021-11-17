package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.utils.gameManager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onFoodLevelChange(final FoodLevelChangeEvent e) {
        final HumanEntity entity = e.getEntity();
        if (entity instanceof Player) {
            final Player player = entity.getKiller();
            if (player != null && gameManager.getAllPlayers() != null && gameManager.getAllPlayers().contains(player.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }
}
