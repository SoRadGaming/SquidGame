package com.soradgaming.squidgame.commands.setup.Games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.commands.setup.CommandHandlerInterface;
import org.bukkit.entity.Player;

public class Save implements CommandHandlerInterface {
    private SquidGame plugin;

    public Save(SquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handleCommand(Player player, String[] args) {
        //TODO check if setup is complete
        Arena.getArenaByName(args[0]).getStructureManager().saveToConfig();
        return true;
    }

    @Override
    public int getMinArgsLength() {
        return 1;
    }
}
