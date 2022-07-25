package com.soradgaming.squidgame.commands.setup.Games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Status;
import com.soradgaming.squidgame.commands.setup.CommandHandlerInterface;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class minPlayers implements CommandHandlerInterface {
    private SquidGame plugin;

    public minPlayers(SquidGame plugin) {
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
        if (!args[1].equalsIgnoreCase("minplayers")) {
            return true;
        }
        arena.getStructureManager().setMinPlayers(Integer.parseInt(args[2]));
        return true;
    }

    @Override
    public int getMinArgsLength() {
        return 3;
    }
}
