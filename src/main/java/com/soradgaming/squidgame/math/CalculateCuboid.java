package com.soradgaming.squidgame.math;

import com.soradgaming.squidgame.arena.Arena;
import com.soradgaming.squidgame.arena.Zones;
import com.soradgaming.squidgame.games.Games;
import com.soradgaming.squidgame.games.SpeedBuilders;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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

public class CalculateCuboid implements Runnable {
    private SpeedBuilders speedBuilders;
    private Arena arena;
    private final File folder = new File("plugins/SquidGame/schematics/");
    private ArrayList<File> schematics = new ArrayList<>();
    private Cuboid BuildZone1;
    private Cuboid DisplayZone1;
    private Cuboid BuildZone2;
    private Cuboid DisplayZone2;
    private Cuboid BuildZone3;
    private Cuboid DisplayZone3;
    private Cuboid BuildZone4;
    private Cuboid DisplayZone4;
    private Cuboid Schematic1;
    private Cuboid Schematic2;
    private Cuboid Schematic3;
    private Cuboid Schematic4;

    public CalculateCuboid(SpeedBuilders speedBuilders, Arena arena) {
        this.speedBuilders = speedBuilders;
        this.arena = arena;
    }

    @Override
    public void run() {
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
            WorldEditHook worldEditHook = new WorldEditHook();

            worldEditHook.cuboidToBlockVector3(getZones(Zones.DisplayZone1));
            try {
                worldEditHook.load(schematics.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Schematic1 = worldEditHook.getClipboardCuboid();
            worldEditHook.cuboidToBlockVector3(getZones(Zones.DisplayZone2));
            try {
                worldEditHook.load(schematics.get(1));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Schematic2 = worldEditHook.getClipboardCuboid();
            worldEditHook.cuboidToBlockVector3(getZones(Zones.DisplayZone3));
            try {
                worldEditHook.load(schematics.get(2));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Schematic3 = worldEditHook.getClipboardCuboid();
            worldEditHook.cuboidToBlockVector3(getZones(Zones.DisplayZone4));
            try {
                worldEditHook.load(schematics.get(3));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Schematic4 = worldEditHook.getClipboardCuboid();
        } else {
            Bukkit.getConsoleSender().sendMessage("[ERROR]: Not Enough Schematics, 4 Required");
        }
    }

    public Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public void loop() {
        if (!speedBuilders.getWinTeam1()) {
            ArrayList<Material> answerBlocks1 = new ArrayList<>();
            ArrayList<Material> questionBlocks1 = new ArrayList<>();
            for (int i=0; i < Schematic1.volume(); i++) {
                answerBlocks1.add(Schematic1.getBlocks().get(i).getType());
                questionBlocks1.add(getZones(Zones.BuildZone1).getBlocks().get(i).getType());
            }
            if (questionBlocks1.equals(answerBlocks1)) {
                //they finished building
                speedBuilders.completeTeam1();
            }
        }
        if (!speedBuilders.getWinTeam2()) {
            ArrayList<Material> answerBlocks2 = new ArrayList<>();
            ArrayList<Material> questionBlocks2 = new ArrayList<>();
            for (int i=0; i < Schematic2.volume(); i++) {
                answerBlocks2.add(Schematic2.getBlocks().get(i).getType());
                questionBlocks2.add(getZones(Zones.BuildZone2).getBlocks().get(i).getType());
            }
            if (questionBlocks2.equals(answerBlocks2)) {
                //they finished building
                speedBuilders.completeTeam2();
            }
        }
        if (!speedBuilders.getWinTeam3()) {
            ArrayList<Material> answerBlocks3 = new ArrayList<>();
            ArrayList<Material> questionBlocks3 = new ArrayList<>();
            for (int i=0; i < Schematic3.volume(); i++) {
                answerBlocks3.add(Schematic3.getBlocks().get(i).getType());
                questionBlocks3.add(getZones(Zones.BuildZone3).getBlocks().get(i).getType());
            }
            if (questionBlocks3.equals(answerBlocks3)) {
                //they finished building
                speedBuilders.completeTeam3();
            }
        }
        if (!speedBuilders.getWinTeam4()) {
            ArrayList<Material> answerBlocks4 = new ArrayList<>();
            ArrayList<Material> questionBlocks4 = new ArrayList<>();
            for (int i=0; i < Schematic4.volume(); i++) {
                answerBlocks4.add(Schematic4.getBlocks().get(i).getType());
                questionBlocks4.add(getZones(Zones.BuildZone4).getBlocks().get(i).getType());
            }
            if (questionBlocks4.equals(answerBlocks4)) {
                //they finished building
                speedBuilders.completeTeam4();
            }
        }
    }

    public Cuboid getZones(Zones zone) {
        if (zone == Zones.BuildZone1) {
            if (BuildZone1 == null) {
                BlockVector vector1 = arena.getStructureManager().configToVectors("Game2.BuildZone1.first_point");
                BlockVector vector2 = arena.getStructureManager().configToVectors("Game2.BuildZone1.second_point");
                World world = arena.getStructureManager().getSpawn(Games.Game2).getWorld();
                BuildZone1 = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
                return BuildZone1;
            }
            return BuildZone1;
        } else if (zone == Zones.BuildZone2) {
            if (BuildZone2 == null) {
                BlockVector vector1 = arena.getStructureManager().configToVectors("Game2.BuildZone2.first_point");
                BlockVector vector2 = arena.getStructureManager().configToVectors("Game2.BuildZone2.second_point");
                World world = arena.getStructureManager().getSpawn(Games.Game2).getWorld();
                BuildZone2 = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
                return BuildZone2;
            }
            return BuildZone2;
        } else if (zone == Zones.BuildZone3) {
            if (BuildZone3 == null) {
                BlockVector vector1 = arena.getStructureManager().configToVectors("Game2.BuildZone3.first_point");
                BlockVector vector2 = arena.getStructureManager().configToVectors("Game2.BuildZone3.second_point");
                World world = arena.getStructureManager().getSpawn(Games.Game2).getWorld();
                BuildZone3 = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
                return BuildZone3;
            }
            return BuildZone3;
        } else if (zone == Zones.BuildZone4) {
            if (BuildZone4 == null) {
                BlockVector vector1 = arena.getStructureManager().configToVectors("Game2.BuildZone4.first_point");
                BlockVector vector2 = arena.getStructureManager().configToVectors("Game2.BuildZone4.second_point");
                World world = arena.getStructureManager().getSpawn(Games.Game2).getWorld();
                BuildZone4 = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
                return BuildZone4;
            }
            return BuildZone4;
        } else if (zone == Zones.DisplayZone1) {
            if (DisplayZone1 == null) {
                BlockVector vector1 = arena.getStructureManager().configToVectors("Game2.DisplayZone1.first_point");
                BlockVector vector2 = arena.getStructureManager().configToVectors("Game2.DisplayZone1.second_point");
                World world = arena.getStructureManager().getSpawn(Games.Game2).getWorld();
                DisplayZone1 = new Cuboid(Objects.requireNonNull(world), vector1.getBlockX(), vector1.getBlockY(), vector1.getBlockZ(), vector2.getBlockX(), vector2.getBlockY(), vector2.getBlockZ());
                return DisplayZone1;
            }
            return DisplayZone1;
        } else if (zone == Zones.DisplayZone2) {
            if (DisplayZone2 == null) {
                BlockVector vector1 = arena.getStructureManager().configToVectors("Game2.DisplayZone2.first_point");
                BlockVector vector2 = arena.getStructureManager().configToVectors("Game2.DisplayZone2.second_point");
                World world = arena.getStructureManager().getSpawn(Games.Game2).getWorld();
                DisplayZone2 = new Cuboid(Objects.requireNonNull(world), vector1.getBlockX(), vector1.getBlockY(), vector1.getBlockZ(), vector2.getBlockX(), vector2.getBlockY(), vector2.getBlockZ());
                return DisplayZone2;
            }
            return DisplayZone2;
        } else if (zone == Zones.DisplayZone3) {
            if (DisplayZone3 == null) {
                BlockVector vector1 = arena.getStructureManager().configToVectors("Game2.DisplayZone3.first_point");
                BlockVector vector2 = arena.getStructureManager().configToVectors("Game2.DisplayZone3.second_point");
                World world = arena.getStructureManager().getSpawn(Games.Game2).getWorld();
                DisplayZone3 = new Cuboid(Objects.requireNonNull(world), vector1.getBlockX(), vector1.getBlockY(), vector1.getBlockZ(), vector2.getBlockX(), vector2.getBlockY(), vector2.getBlockZ());
                return DisplayZone3;
            }
            return DisplayZone3;
        } else if (zone == Zones.DisplayZone4) {
            if (DisplayZone4 == null) {
                BlockVector vector1 = arena.getStructureManager().configToVectors("Game2.DisplayZone4.first_point");
                BlockVector vector2 = arena.getStructureManager().configToVectors("Game2.DisplayZone4.second_point");
                World world = arena.getStructureManager().getSpawn(Games.Game2).getWorld();
                DisplayZone4 = new Cuboid(Objects.requireNonNull(world), vector1.getBlockX(), vector1.getBlockY(), vector1.getBlockZ(), vector2.getBlockX(), vector2.getBlockY(), vector2.getBlockZ());
                return DisplayZone4;
            }
            return DisplayZone4;
        }
        return null;
    }

    public void reloadCuboids() {
        BuildZone1 = null;
        BuildZone2 = null;
        BuildZone3 = null;
        BuildZone4 = null;
        DisplayZone1 = null;
        DisplayZone2 = null;
        DisplayZone3 = null;
        DisplayZone4 = null;
    }
}
