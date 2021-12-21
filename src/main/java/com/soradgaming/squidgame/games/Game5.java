package com.soradgaming.squidgame.games;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.utils.gameManager;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.UUID;

public class Game5 implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    private static boolean Started = false;
    private static ArrayList<UUID> playersList;

    public static void startGame5() {
        playersList = gameManager.getAllPlayers();
        Started = true;
    }
}
