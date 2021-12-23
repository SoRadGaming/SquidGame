package com.soradgaming.squidgame.listeners;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.games.Games;
import com.soradgaming.squidgame.games.RedLightGreenLight;
import com.soradgaming.squidgame.math.BlockUtils;
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

public class PlayerMoveListener implements Listener {
    private final SquidGame plugin = SquidGame.plugin;

    @EventHandler(ignoreCancelled = true)
    public void PlayerMoveEvent(@NotNull PlayerMoveEvent e) {
        if (e.getFrom().distance(Objects.requireNonNull(e.getTo())) <= 0.015) {
            return;
        }
        Player player = e.getPlayer();
        Arena arena = Arena.getPlayerArena(player);
        if (arena == null) {
            return;
        }
        Games games = arena.getGameHandler().getCurrentGame();
        if (games == null) {
            return;
        }
        if (games.equals(Games.Game1)) {
            RedLightGreenLight redLightGreenLight = arena.getGameHandler().redLightGreenLight;
            if (!redLightGreenLight.isCanWalk()) {
                final Location location = e.getPlayer().getLocation();
                if (redLightGreenLight.getKillZone().isBetween(location)) {
                    Location spawn = arena.getStructureManager().getSpawn(Games.Game1);
                    if (plugin.getConfig().getBoolean("eliminate-players")) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(spawn);
                        arena.getPlayerHandler().killPlayer(player);
                    } else {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.teleport(spawn);
                    }
                }
            }
        } else if (games.equals(Games.Game6) && arena.getGameHandler().glassSteppingStones.isStarted()) {
            final Location location = Objects.requireNonNull(e.getTo()).clone().subtract(0, 1, 0);
            final Block block = location.getBlock();
            if (block != null && block.getType() == Material.valueOf(plugin.getConfig().getString("Game6.material"))) {
                if (arena.getGameHandler().glassSteppingStones.getFakeBlocks().contains(block)) {
                    BlockUtils.destroyBlockGroup(location.getBlock(), true);
                }
            }
        } else if (games.equals(Games.Game7) && arena.getGameHandler().sumo.isStarted()) {
            final Location location = e.getTo().clone();
            final String killBlock = plugin.getConfig().getString("Game7.kill-block", "sand");
            location.subtract(0, 1, 0);

            if (location.getBlock() != null && location.getBlock().getType() != null
                    && location.getBlock().getType().toString().equalsIgnoreCase(killBlock)) {
                arena.getPlayerHandler().killPlayer(player);
            }
        }
    }
}
