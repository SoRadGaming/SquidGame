package com.soradgaming.squidgame.placeholders;

import com.soradgaming.squidgame.SquidGame;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Scoreboard {
    private final SquidGame plugin;
    private final PlayerDataType type;
    public HashMap<UUID, Integer> playerList;

    public Scoreboard(SquidGame squidGame, PlayerDataType type) {
        this.plugin = squidGame;
        this.type = type;
        this.playerList = grabData();
    }

    public LinkedHashMap<UUID, Integer> grabData() {
        //Grab All player to have ever join the server
        List<OfflinePlayer> offlinePlayers = Arrays.stream(Bukkit.getOfflinePlayers()).toList();
        List<UUID> players = new ArrayList<>();
        //Convert from OfflinePLayers to UUID
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            players.add(offlinePlayer.getUniqueId());
        }
        if (type.equals(PlayerDataType.Points)) {
            for (UUID uuid : players) {
                int points = plugin.data.getInt(uuid + ".points");
                if (playerList != null) {
                    playerList.put(uuid, points);
                }
            }
        } else {
            for (UUID uuid : players) {
                int points = plugin.data.getInt(uuid + ".wins");
                if (playerList != null) {
                    playerList.put(uuid, points);
                }
            }
        }
        if (playerList != null) {
            return sortHashMapByValues(playerList);
        } else {
            return null;
        }
    }

    public HashMap<UUID, Integer> getPlayerList() {
        return playerList;
    }

    public @NotNull LinkedHashMap<UUID, Integer> sortHashMapByValues(@NotNull HashMap<UUID, Integer> passedMap) {
        List<UUID> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<UUID, Integer> sortedMap = new LinkedHashMap<>();

        for (Integer val : mapValues) {
            Iterator<UUID> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                UUID key = keyIt.next();
                Integer comp1 = passedMap.get(key);

                if (comp1.equals(val)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }
}
