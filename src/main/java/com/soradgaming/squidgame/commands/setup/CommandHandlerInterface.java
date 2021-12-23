package com.soradgaming.squidgame.commands.setup;

import org.bukkit.entity.Player;

public interface CommandHandlerInterface {
    int getMinArgsLength();

    boolean handleCommand(Player player, String[] args);
}
