package com.soradgaming.squidgame.commands.setup.Games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Status;
import com.soradgaming.squidgame.commands.setup.CommandHandlerInterface;
import com.soradgaming.squidgame.games.Games;
import com.soradgaming.squidgame.utils.playerWand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Game3Setup implements CommandHandlerInterface {
    private SquidGame plugin;

    public Game3Setup(SquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handleCommand(Player player, String[] args) {
        Arena arena = Arena.getArenaByName(args[0]);
        if (arena == null) {
            return true;
        }
        if (arena.getGameHandler().getStatus().equals(Status.Online)) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cDisable Arena First"));
            return true;
        }
        if (!args[1].equalsIgnoreCase("game3")) {
            return true;
        }
        if (args[2].equals("spawn")) {
            arena.getStructureManager().setSpawnPoint(Games.Game3, player.getLocation());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&Third game " + "Spawn" + "&a set to your location &7(" + player.getLocation().toVector() + ")"));
            arena.getStructureManager().saveToConfig();
        }
        return true;
    }

    @Override
    public int getMinArgsLength() {
        return 3;
    }
}
