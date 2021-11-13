package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Game7 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;

    @EventHandler(ignoreCancelled = true)
    public void PlayerMoveEvent(@NotNull PlayerMoveEvent e) {
        if (e.getFrom().distance(Objects.requireNonNull(e.getTo())) <= 0.015) {
            return;
        } else if (e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
            return;
        }
        /*
        Player player = e.getPlayer();
        if (arena.getCurrentGame() instanceof G7SquidGame) {
            final Location loc = e.getTo().clone();
            final String killBlock = arena.getConfig().getString("games.seventh.kill-block", "sand");

            loc.subtract(0, 1, 0);

            if (loc.getBlock() != null && loc.getBlock().getType() != null
                    && loc.getBlock().getType().toString().equalsIgnoreCase(killBlock)) {
                arena.killPlayer(player);
            }
        }

         */
    }
}
