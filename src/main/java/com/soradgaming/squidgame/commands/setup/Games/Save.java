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
        Arena arena = Arena.getArenaByName(args[0]);
        if (arena == null) {
            player.sendMessage("Not an Arena");
            return true;
        }
        if (arena.getStructureManager().checkSetupDone()) {
            player.sendMessage("Saving");
            arena.getStructureManager().saveToConfig();
        } else {
            player.sendMessage("Setup Not Complete");
        }
        return true;
    }

    @Override
    public int getMinArgsLength() {
        return 1;
    }
}
