package com.soradgaming.squidgame.commands.setup.Games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.commands.setup.CommandHandlerInterface;
import org.bukkit.entity.Player;

public class Create implements CommandHandlerInterface {
    private SquidGame plugin;

    public Create(SquidGame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handleCommand(Player player, String[] args) {
        //TODO check if name is taken
        if (Arena.getArenaByName(args[0]) == null) {
            player.sendMessage("Name Taken");
            return false;
        }
        Arena arena = new Arena(args[0],plugin);
        Arena.registerArena(arena);
        return true;
    }

    @Override
    public int getMinArgsLength() {
        return 1;
    }
}
