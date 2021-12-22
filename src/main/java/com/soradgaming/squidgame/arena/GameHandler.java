package com.soradgaming.squidgame.arena;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.games.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GameHandler {
    private SquidGame plugin;
    private Arena arena;
    //Arena Status
    private Status gameStatus;
    //Global
    private boolean pvp = false;
    private boolean block = false;

    public GameHandler(SquidGame plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    public void setGameStatus(Status status){
        this.gameStatus = status;
    }

    public Status getStatus() {
        return gameStatus;
    }

    public boolean isPvPAllowed() { return pvp; }

    public void setPvPAllowed(boolean TorF) { pvp = TorF; }

    public boolean isBlockAllowed() { return block; }

    public void setBlockAllowed(boolean TorF) { block = TorF; }

    private BukkitScheduler game = Bukkit.getScheduler();

    public void checkStart() {
        int min = arena.getStructureManager().getMinPlayers();
        List<Player> playerList = arena.getPlayerHandler().getAllPlayers();
        BukkitScheduler gameStartTask = Bukkit.getScheduler();

        if (playerList.size() >= min) {
            if (arena.getGameHandler().getStatus().equals(Status.Offline)) {
                gameStartTask.runTaskLater(plugin, () -> {
                    arena.getPlayerHandler().Initialise();
                    intermission(Games.Game1);
                    for (Player player : playerList) {
                        Objects.requireNonNull(player).sendMessage(Messages.formatMessage(player, "arena.started"));
                    }
                    setGameStatus(Status.Online);
                }, 20L * arena.getStructureManager().getCountdown(Games.Game1));
                //Message Starting
                for (Player player : playerList) {
                    Objects.requireNonNull(player).sendMessage(Messages.formatMessage(player, "arena.starting"));
                }
                setGameStatus(Status.Starting);
            }
        } else {
            gameStartTask.cancelTasks(plugin);
        }
    }

    public void checkEnoughPlayersLeft() {
        //TODO add check
        //End Arena as not enough players left
        List<Player> players  = arena.getPlayerHandler().getAllPlayers();
        for (Player player: players) {
            arena.getPlayerHandler().playerQuit(player);
            player.sendTitle(Messages.formatMessage(player,"events.finish.draw.title"), Messages.formatMessage(player,"events.finish.draw.subtitle"),10,30,10);
        }
        setGameStatus(Status.Offline);
    }

    public void intermission(Games games) { //TODO
        if (arena.getPlayerHandler().getAllPlayers().size() < 2) {
            //End Game
            checkEnoughPlayersLeft();
            return;
        }
        if (!games.equals(Games.Game3) && !games.equals(Games.Game1) && !games.equals(Games.Game4)) {
            for (Player player: arena.getPlayerHandler().getAllPlayers()) {
                player.teleport(plugin.getConfig().getLocation("Lobby"));
            }
            Messages.broadcastTitle(arena.getPlayerHandler().getAllPlayers(),"events.intermission.title", "events.intermission.subtitle" , 3);
        }
        if (games.equals(Games.Game1)) {
            //No Delay on Game 1 as players already in lobby
            new RedLightGreenLight(plugin,arena).run();
        } else if (games.equals(Games.Game2)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Game2.startGame2();
            }, 20L * arena.getStructureManager().getIntermissionTime());
        } else if (games.equals(Games.Game3)) {
            //No Delay on Game 3 as lobby is the arena
            Game3.startGame3();
        } else if (games.equals(Games.Game4)) {
            //No Delay on Game 4 as players just finished fighting in lobby
            Game4.startGame4();
        } else if (games.equals(Games.Game5)) {
            Bukkit.getScheduler().runTaskLater(plugin, Game5::startGame5, 20L * arena.getStructureManager().getIntermissionTime());
        } else if (games.equals(Games.Game6)) {
            Bukkit.getScheduler().runTaskLater(plugin, Game6::startGame6, 20L * arena.getStructureManager().getIntermissionTime());
        } else if (games.equals(Games.Game7)) {
            Bukkit.getScheduler().runTaskLater(plugin, Game7::startGame7, 20L * arena.getStructureManager().getIntermissionTime());
        }
    }
}
