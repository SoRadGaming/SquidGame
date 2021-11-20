package com.soradgaming.squidgame.math;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.games.Game2;
import com.soradgaming.squidgame.games.Zones;
import com.soradgaming.squidgame.utils.gameManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.BlockVector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * This is legit just SpeedBuilders Code (Going to make this plugin latter)
 */

public class CalculateCuboid implements Listener {
    private static final SquidGame plugin = SquidGame.plugin;
    private static final File folder = new File("plugins/SquidGame/schematics/");
    private static ArrayList<File> schematics = new ArrayList<>();
    private static Cuboid BuildZone1;
    private static Cuboid DisplayZone1;
    private static Cuboid BuildZone2;
    private static Cuboid DisplayZone2;
    private static Cuboid BuildZone3;
    private static Cuboid DisplayZone3;
    private static Cuboid BuildZone4;
    private static Cuboid DisplayZone4;
    private static Cuboid Schematic1;
    private static Cuboid Schematic2;
    private static Cuboid Schematic3;
    private static Cuboid Schematic4;

    public static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public static void start() throws IOException {
        schematics.clear();
        File[] listOfFiles = folder.listFiles();
        for (File fileIterator:listOfFiles) {
            Optional<String> extension = getExtensionByStringHandling(fileIterator.toString());
            if (extension.toString().equals("Optional[schematic]")) {
                schematics.add(fileIterator);
            }
        }

        Collections.shuffle(schematics);
        if (schematics.size() >= 4) { //are there enough schematics
            //Clear Platforms
            for (Block block:getZones(Zones.BuildZone1)) {
                block.setType(Material.AIR);
            }
            for (Block block:getZones(Zones.BuildZone2)) {
                block.setType(Material.AIR);
            }
            for (Block block:getZones(Zones.BuildZone3)) {
                block.setType(Material.AIR);
            }
            for (Block block:getZones(Zones.BuildZone4)) {
                block.setType(Material.AIR);
            }
            //Random File from list (Load Schematics)
            WorldEditHook.cuboidToBlockVector3(getZones(Zones.DisplayZone1));
            WorldEditHook.load(schematics.get(0));
            Schematic1 = WorldEditHook.getClipboardCuboid();
            WorldEditHook.cuboidToBlockVector3(getZones(Zones.DisplayZone2));
            WorldEditHook.load(schematics.get(1));
            Schematic2 = WorldEditHook.getClipboardCuboid();
            WorldEditHook.cuboidToBlockVector3(getZones(Zones.DisplayZone3));
            WorldEditHook.load(schematics.get(2));
            Schematic3 = WorldEditHook.getClipboardCuboid();
            WorldEditHook.cuboidToBlockVector3(getZones(Zones.DisplayZone4));
            WorldEditHook.load(schematics.get(3));
            Schematic4 = WorldEditHook.getClipboardCuboid();
        } else {
            Bukkit.getConsoleSender().sendMessage("[ERROR]: Not Enough Schematics, 4 Required");
        }
    }

    @EventHandler
    private static void onBlockBreak(BlockPlaceEvent e) {
        loop();
    }

    @EventHandler
    private static void BlockPlaceEvent(BlockBreakEvent e) {
        loop();
    }

    public static void loop() {
        if (!Game2.getWinTeam1()) {
            ArrayList<Material> answerBlocks1 = new ArrayList<>();
            ArrayList<Material> questionBlocks1 = new ArrayList<>();
            for (int i=0; i < Schematic1.volume(); i++) {
                answerBlocks1.add(Schematic1.getBlocks().get(i).getType());
                questionBlocks1.add(getZones(Zones.BuildZone1).getBlocks().get(i).getType());
            }
            if (questionBlocks1.equals(answerBlocks1)) {
                //they finished building
                Game2.completeTeam1();
            }
        }
        if (!Game2.getWinTeam2()) {
            ArrayList<Material> answerBlocks2 = new ArrayList<>();
            ArrayList<Material> questionBlocks2 = new ArrayList<>();
            for (int i=0; i < Schematic2.volume(); i++) {
                answerBlocks2.add(Schematic2.getBlocks().get(i).getType());
                questionBlocks2.add(getZones(Zones.BuildZone2).getBlocks().get(i).getType());
            }
            if (questionBlocks2.equals(answerBlocks2)) {
                //they finished building
                Game2.completeTeam2();
            }
        }
        if (!Game2.getWinTeam3()) {
            ArrayList<Material> answerBlocks3 = new ArrayList<>();
            ArrayList<Material> questionBlocks3 = new ArrayList<>();
            for (int i=0; i < Schematic3.volume(); i++) {
                answerBlocks3.add(Schematic3.getBlocks().get(i).getType());
                questionBlocks3.add(getZones(Zones.BuildZone3).getBlocks().get(i).getType());
            }
            if (questionBlocks3.equals(answerBlocks3)) {
                //they finished building
                Game2.completeTeam3();
            }
        }
        if (!Game2.getWinTeam4()) {
            ArrayList<Material> answerBlocks4 = new ArrayList<>();
            ArrayList<Material> questionBlocks4 = new ArrayList<>();
            for (int i=0; i < Schematic4.volume(); i++) {
                answerBlocks4.add(Schematic4.getBlocks().get(i).getType());
                questionBlocks4.add(getZones(Zones.BuildZone4).getBlocks().get(i).getType());
            }
            if (questionBlocks4.equals(answerBlocks4)) {
                //they finished building
                Game2.completeTeam4();
            }
        }
    }

    public static Cuboid getZones(Zones zone) {
        if (zone == Zones.BuildZone1) {
            if (BuildZone1 == null) {
                BlockVector vector1 = gameManager.configToVectors("Game2.BuildZone1.first_point");
                BlockVector vector2 = gameManager.configToVectors("Game2.BuildZone1.second_point");
                org.bukkit.World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game2.world")));
                BuildZone1 = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
                return BuildZone1;
            }
            return BuildZone1;
        } else if (zone == Zones.BuildZone2) {
            if (BuildZone2 == null) {
                BlockVector vector1 = gameManager.configToVectors("Game2.BuildZone2.first_point");
                BlockVector vector2 = gameManager.configToVectors("Game2.BuildZone2.second_point");
                org.bukkit.World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game2.world")));
                BuildZone2 = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
                return BuildZone2;
            }
            return BuildZone2;
        } else if (zone == Zones.BuildZone3) {
            if (BuildZone3 == null) {
                BlockVector vector1 = gameManager.configToVectors("Game2.BuildZone3.first_point");
                BlockVector vector2 = gameManager.configToVectors("Game2.BuildZone3.second_point");
                org.bukkit.World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game2.world")));
                BuildZone3 = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
                return BuildZone3;
            }
            return BuildZone3;
        } else if (zone == Zones.BuildZone4) {
            if (BuildZone4 == null) {
                BlockVector vector1 = gameManager.configToVectors("Game2.BuildZone4.first_point");
                BlockVector vector2 = gameManager.configToVectors("Game2.BuildZone4.second_point");
                org.bukkit.World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game2.world")));
                BuildZone4 = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
                return BuildZone4;
            }
            return BuildZone4;
        } else if (zone == Zones.DisplayZone1) {
            if (DisplayZone1 == null) {
                BlockVector vector1 = gameManager.configToVectors("Game2.DisplayZone1.first_point");
                BlockVector vector2 = gameManager.configToVectors("Game2.DisplayZone1.second_point");
                org.bukkit.World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game2.world")));
                DisplayZone1 = new Cuboid(Objects.requireNonNull(world), vector1.getBlockX(), vector1.getBlockY(), vector1.getBlockZ(), vector2.getBlockX(), vector2.getBlockY(), vector2.getBlockZ());
                return DisplayZone1;
            }
            return DisplayZone1;
        } else if (zone == Zones.DisplayZone2) {
            if (DisplayZone2 == null) {
                BlockVector vector1 = gameManager.configToVectors("Game2.DisplayZone2.first_point");
                BlockVector vector2 = gameManager.configToVectors("Game2.DisplayZone2.second_point");
                org.bukkit.World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game2.world")));
                DisplayZone2 = new Cuboid(Objects.requireNonNull(world), vector1.getBlockX(), vector1.getBlockY(), vector1.getBlockZ(), vector2.getBlockX(), vector2.getBlockY(), vector2.getBlockZ());
                return DisplayZone2;
            }
            return DisplayZone2;
        } else if (zone == Zones.DisplayZone3) {
            if (DisplayZone3 == null) {
                BlockVector vector1 = gameManager.configToVectors("Game2.DisplayZone3.first_point");
                BlockVector vector2 = gameManager.configToVectors("Game2.DisplayZone3.second_point");
                org.bukkit.World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game2.world")));
                DisplayZone3 = new Cuboid(Objects.requireNonNull(world), vector1.getBlockX(), vector1.getBlockY(), vector1.getBlockZ(), vector2.getBlockX(), vector2.getBlockY(), vector2.getBlockZ());
                return DisplayZone3;
            }
            return DisplayZone3;
        } else if (zone == Zones.DisplayZone4) {
            if (DisplayZone4 == null) {
                BlockVector vector1 = gameManager.configToVectors("Game2.DisplayZone4.first_point");
                BlockVector vector2 = gameManager.configToVectors("Game2.DisplayZone4.second_point");
                org.bukkit.World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game2.world")));
                DisplayZone4 = new Cuboid(Objects.requireNonNull(world), vector1.getBlockX(), vector1.getBlockY(), vector1.getBlockZ(), vector2.getBlockX(), vector2.getBlockY(), vector2.getBlockZ());
                return DisplayZone4;
            }
            return DisplayZone4;
        }
        return null;
    }
}
