package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Status;
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
            if (player == null) {
                return;
            }
            Arena arena = Arena.getPlayerArena(player);
            if (arena == null) {
                return;
            }
            if (arena.getGameHandler().getStatus() == Status.Online || arena.getGameHandler().getStatus() == Status.Starting) {
                e.setCancelled(true);
            }
        }
    }
}
