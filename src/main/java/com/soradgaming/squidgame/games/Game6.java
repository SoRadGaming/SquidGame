package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Game6 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;

    @EventHandler(ignoreCancelled = true)
    public void PlayerMoveEvent(@NotNull PlayerMoveEvent e) {
        if (e.getFrom().distance(Objects.requireNonNull(e.getTo())) <= 0.015) {
            return;
        } else if (e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
            return;
        }
        Player player = e.getPlayer();
        /*
        final Location loc = Objects.requireNonNull(e.getTo()).clone().subtract(0, 1, 0);
        final Block block = loc.getBlock();

        if (block != null && block.getType() == Material.GLASS) {
            final G6GlassesGame game = (G6GlassesGame) arena.getCurrentGame();

            if (game.isFakeBlock(loc.getBlock())) {
                BlockUtils.destroyBlockGroup(loc.getBlock());
                arena.broadcastSound(
                        plugin.getMainConfig().getSound("game-settings.sounds.glass-break", "GLASS"));
            }
        }

         */
    }
/*
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Player player = e.getEntity();
        if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
            gameManager.killPlayer(player);
        }
    }

 */
}
