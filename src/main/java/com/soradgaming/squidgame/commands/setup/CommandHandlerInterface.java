package com.soradgaming.squidgame.commands.setup;

import org.bukkit.entity.Player;

public interface CommandHandlerInterface {
    public int getMinArgsLength();

    public boolean handleCommand(Player player, String[] args);
}
