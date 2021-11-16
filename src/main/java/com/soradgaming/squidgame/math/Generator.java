package com.soradgaming.squidgame.math;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.games.Game6;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class Generator {
    private static final SquidGame plugin = SquidGame.plugin;
    private static final ArrayList<Cuboid> fakeCuboids = new ArrayList<>();
    private static final ArrayList<Block> fakeBlocks = new ArrayList<>();
    private static boolean xtoPos;
    private static boolean ztoPos;
    private static final ArrayList<Cuboid> leftBlocks = new ArrayList<>();
    private static final ArrayList<Cuboid> rightBlocks = new ArrayList<>();
    private static World world;
    private static BlockVector first;
    private static BlockVector second;
    private static int sizeZ;
    private static int sizeX;


    public static ArrayList<Block> getFakeBlocks() {
        return fakeBlocks;
    }

    public static ArrayList<Cuboid> getFakeCuboids() {
        return fakeCuboids;
    }

    private static Vector iterateZ(Vector FirstBlock) {
        Vector SecondBlock;
        if (ztoPos) {
            SecondBlock = FirstBlock.add(new Vector(0,0,4));
        } else {
            SecondBlock = FirstBlock.add(new Vector(0,0,-4));
        }
        return SecondBlock;
    }

    private static Vector iterateX(Vector FirstBlock) {
        Vector SecondBlock;
        if (xtoPos) {
            SecondBlock = FirstBlock.add(new Vector(0,0,4));
        } else {
            SecondBlock = FirstBlock.add(new Vector(0,0,-4));
        }
        return SecondBlock;
    }

    public static void generateTiles(Material material) {
        leftBlocks.clear();
        rightBlocks.clear();
        fakeCuboids.clear();
        fakeBlocks.clear();
        world = Game6.getGlassZone().getWorld();
        first = Game6.getGlassZone().getFirstPoint();
        second = Game6.getGlassZone().getSecondPoint();
        sizeX = Game6.getGlassZone().getSizeX();
        sizeZ = Game6.getGlassZone().getSizeZ();
        xtoPos = second.getBlockX() - first.getBlockX() > 0;
        ztoPos = second.getBlockZ() - first.getBlockZ() > 0;

        //Use sizeZ is long side of rectangle
        if (Math.abs(sizeZ) > Math.abs(sizeX)) {
            //Left
            getLeftZ();
            //Right
            getRightZ();
        } else { //Use sizeX is long side of rectangle
            //Left
            getLeftX();
            //Right
            getRightX();
        }

        Bukkit.getConsoleSender().sendMessage("Right Platforms " + rightBlocks.size());
        Bukkit.getConsoleSender().sendMessage("Left Platforms " + leftBlocks.size());

        //Modify Cuboid Size
        for (int i = 0; i < leftBlocks.size(); i++) {
            Cuboid newCuboid = moveCubeFromTopRight(leftBlocks.get(i));
            leftBlocks.set(i, newCuboid);
        }
        for (int i = 0; i < rightBlocks.size(); i++) {
            Cuboid newCuboid = moveCubeFromTopRight(rightBlocks.get(i));
            rightBlocks.set(i, newCuboid);
        }

        if (leftBlocks.size() == rightBlocks.size()) {
            for (int i = 0; i < rightBlocks.size(); i++) {
                Cuboid rightPlatforms = rightBlocks.get(i);
                Cuboid leftPlatforms = leftBlocks.get(i);
                Random random = new Random();
                boolean isFirstFake = random.nextBoolean();
                if (isFirstFake) {
                    fakeCuboids.add(leftPlatforms);
                } else {
                    fakeCuboids.add(rightPlatforms);
                }
                Bukkit.getConsoleSender().sendMessage("fakeCuboids size "+ fakeCuboids.size());
            }
        }
        //Add Random Data to List of block (May not be needed)
        for (Cuboid fakeCuboid : fakeCuboids) {
            fakeBlocks.addAll(fakeCuboid.getBlocks());
        }


        //Generate Tiles
        Bukkit.getConsoleSender().sendMessage("Changing Left Blocks to Config Block");
        for (Cuboid cuboid : leftBlocks) {
            for (Block block : cuboid.getBlocks()) {
                block.setType(material);
            }
        }
        Bukkit.getConsoleSender().sendMessage("Changing Right Blocks to Config Block");
        for (Cuboid cuboid : rightBlocks) {
            for (Block block : cuboid.getBlocks()) {
                block.setType(material);
            }
        }
    }

    private static void getRightX() {
        for (Cuboid leftBlock : leftBlocks) {
            Cuboid newCuboid = moveCubeToRightSide(leftBlock);
            rightBlocks.add(newCuboid);
        }
    }

    private static void getLeftX() {
        Vector lFirstBlock = first;
        Cuboid cuboidInfile = new Cuboid(world,lFirstBlock, lFirstBlock);
        leftBlocks.add(cuboidInfile);
        //Iterate Z
        lFirstBlock = iterateX(first);
        if (ztoPos) {
            while (Math.abs(lFirstBlock.getBlockX()) <= Math.abs(second.getBlockX())) {
                //Add Cuboid
                leftBlocks.add(new Cuboid(world,lFirstBlock, lFirstBlock));
                //Iterate Z
                lFirstBlock = iterateX(lFirstBlock);
            }
        } else {
            while (Math.abs(lFirstBlock.getBlockZ()) >= Math.abs(second.getBlockX())) {
                //Add Cuboid
                leftBlocks.add(new Cuboid(world,lFirstBlock, lFirstBlock));
                //Iterate Z
                lFirstBlock = iterateX(lFirstBlock);
            }
        }
    }

    public static void getLeftZ() {
        Vector lFirstBlock = first;
        Cuboid cuboidInfile = new Cuboid(world,lFirstBlock, lFirstBlock);
        leftBlocks.add(cuboidInfile);
        //Iterate Z
        lFirstBlock = iterateZ(first);
        if (ztoPos) {
            while (Math.abs(lFirstBlock.getBlockZ()) <= Math.abs(second.getBlockZ())) {
                //Add Cuboid
                leftBlocks.add(new Cuboid(world,lFirstBlock, lFirstBlock));
                //Iterate Z
                lFirstBlock = iterateZ(lFirstBlock);
            }
        } else {
            while (Math.abs(lFirstBlock.getBlockZ()) >= Math.abs(second.getBlockZ())) {
                //Add Cuboid
                leftBlocks.add(new Cuboid(world,lFirstBlock, lFirstBlock));
                //Iterate Z
                lFirstBlock = iterateZ(lFirstBlock);
            }
        }
    }

    public static void getRightZ() {
        for (Cuboid leftBlock : leftBlocks) {
            Cuboid newCuboid = moveCubeToRightSide(leftBlock);
            rightBlocks.add(newCuboid);
        }
    }

    private static Cuboid moveCubeFromTopRight(Cuboid cuboid) {
        Cuboid newCuboid;
        if (xtoPos && ztoPos) {
            Cuboid newCuboidX = cuboid.expand(Cuboid.CuboidDirection.South,1);
            newCuboid = newCuboidX.expand(Cuboid.CuboidDirection.West,1);
        } else if (!xtoPos && ztoPos) {
            Cuboid newCuboidX = cuboid.expand(Cuboid.CuboidDirection.South,-1);
            newCuboid = newCuboidX.expand(Cuboid.CuboidDirection.West,1);
        } else if (xtoPos && !ztoPos) {
            Cuboid newCuboidX = cuboid.expand(Cuboid.CuboidDirection.South,1);
            newCuboid = newCuboidX.expand(Cuboid.CuboidDirection.West,-1);
        } else {
            Cuboid newCuboidX = cuboid.expand(Cuboid.CuboidDirection.South,-1);
            newCuboid = newCuboidX.expand(Cuboid.CuboidDirection.West,-1);
        }
        return newCuboid;
    }

    private static Cuboid moveCubeToRightSide(Cuboid cuboid) {
        Cuboid newCuboid;
        if (Math.abs(sizeZ) > Math.abs(sizeX)) {
            //Z is Index
            if (xtoPos) {
                Cuboid tempCuboid = cuboid.expand(Cuboid.CuboidDirection.North,-4);
                newCuboid = tempCuboid.expand(Cuboid.CuboidDirection.North,-4);
            } else {
                Cuboid tempCuboid = cuboid.expand(Cuboid.CuboidDirection.North,4);
                newCuboid = tempCuboid.expand(Cuboid.CuboidDirection.North,4);
            }
        } else {
            if (ztoPos) {
                Cuboid tempCuboid = cuboid.expand(Cuboid.CuboidDirection.North,-4);
                newCuboid = tempCuboid.expand(Cuboid.CuboidDirection.North,-4);
            } else {
                Cuboid tempCuboid = cuboid.expand(Cuboid.CuboidDirection.North,4);
                newCuboid = tempCuboid.expand(Cuboid.CuboidDirection.North,4);
            }
        }
        return newCuboid;
    }
}
