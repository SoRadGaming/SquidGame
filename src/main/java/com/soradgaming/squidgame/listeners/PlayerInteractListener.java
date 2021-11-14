package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.utils.PlayerWand;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class PlayerInteractListener implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        PlayerWand player = (PlayerWand) e.getPlayer();

        if (player.getWand() != null && e.getItem() != null && e.getItem().getType().equals(Material.BLAZE_ROD)) {
            final PlayerWand wand = player.getWand();

            if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                wand.setFirstPoint(Objects.requireNonNull(e.getClickedBlock()).getLocation(),e.getClickedBlock().getLocation().getWorld());
                e.getPlayer().sendMessage("§aSet §dfirst §apoint §7(§e" + wand.getFirstPoint().toString() + "§7)");
            } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                wand.setSecondPoint(Objects.requireNonNull(e.getClickedBlock()).getLocation());
                e.getPlayer().sendMessage("§aSet §bsecond §apoint §7(§e" + wand.getSecondPoint().toString() + "§7)");
            }
            e.setCancelled(true);
        }
    }
}
