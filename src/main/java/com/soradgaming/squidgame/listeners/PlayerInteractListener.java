package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Status;
import com.soradgaming.squidgame.utils.playerWand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Arena arena = Arena.getPlayerArena(player);
        if (e.getItem() != null && e.getItem().isSimilar(playerWand.getWand())) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                playerWand.setFirstPoint(e.getClickedBlock().getLocation(), player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&aSet &dfirst &apoint &7(&e" +
                        playerWand.getFirstPoint(player).toString() + "&7)"));
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                playerWand.setSecondPoint(e.getClickedBlock().getLocation(), player);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&aSet &bsecond &apoint &7(&e" +
                        playerWand.getSecondPoint(player).toString() + "&7)"));
            }
            e.setCancelled(true);
        }
        if (arena == null) {
            return;
        }
        if (!arena.getGameHandler().isBlockAllowed() && arena.getGameHandler().getStatus().equals(Status.Online) || arena.getGameHandler().getStatus().equals(Status.Starting)) {
            e.setCancelled(true);
        }
    }
}
