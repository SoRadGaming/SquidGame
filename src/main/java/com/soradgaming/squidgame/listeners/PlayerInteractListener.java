package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.utils.playerManager;
import com.soradgaming.squidgame.utils.playerWand;
import com.soradgaming.squidgame.utils.gameManager;
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
        if (gameManager.getAllPlayers().contains(player.getUniqueId()) && !gameManager.isBlockAllowed() && playerManager.gameStarted) {
            e.setCancelled(true);
        } else if (playerWand.getWand() != null && e.getItem() != null && e.getItem().isSimilar(playerWand.getWand())) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                playerWand.setFirstPoint(Objects.requireNonNull(e.getClickedBlock()).getLocation());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&aSet &dfirst &apoint &7(&e" + playerWand.getFirstPoint().toString() + "&7)"));
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                playerWand.setSecondPoint(Objects.requireNonNull(e.getClickedBlock()).getLocation());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&aSet &bsecond &apoint &7(&e" + playerWand.getSecondPoint().toString() + "&7)"));
            }
            e.setCancelled(true);
        }
    }
}
