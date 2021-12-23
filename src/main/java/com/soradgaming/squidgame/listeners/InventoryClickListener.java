package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.arena.Arena;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Arena arena = Arena.getPlayerArena(event.getWhoClicked().getKiller());
        if (arena == null) {
            return;
        }
        if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
            if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
                event.setCancelled(true);
            }
        }
    }
}
