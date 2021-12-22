package com.soradgaming.squidgame.math;

import com.soradgaming.squidgame.games.GlassSteppingStones;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class Generator {
    private GlassSteppingStones glassSteppingStones;
    private ArrayList<Cuboid> fakeCuboids = new ArrayList<>();
    private ArrayList<Block> fakeBlocks = new ArrayList<>();
    private boolean xtoPos;
    private boolean ztoPos;
    private ArrayList<Cuboid> leftBlocks = new ArrayList<>();
    private ArrayList<Cuboid> rightBlocks = new ArrayList<>();
    private World world;
    private BlockVector first;
    private BlockVector second;
    private int sizeZ;
    private int sizeX;

    public Generator(GlassSteppingStones glassSteppingStones) {
        this.glassSteppingStones = glassSteppingStones;
    }

    public ArrayList<Block> getFakeBlocks() {return fakeBlocks;}

    public ArrayList<Cuboid> getFakeCuboids() {
        return fakeCuboids;
    }

    private Vector iterateZ(Vector FirstBlock) {
        Vector SecondBlock;
        if (ztoPos) {
            SecondBlock = FirstBlock.add(new Vector(0,0,4));
        } else {
            SecondBlock = FirstBlock.add(new Vector(0,0,-4));
        }
        return SecondBlock;
    }

    private Vector iterateX(Vector FirstBlock) {
        Vector SecondBlock;
        if (xtoPos) {
            SecondBlock = FirstBlock.add(new Vector(4,0,0));
        } else {
            SecondBlock = FirstBlock.add(new Vector(-4,0,0));
        }
        return SecondBlock;
    }

    public void generateTiles(Material material, int count) {
        leftBlocks.clear();
        rightBlocks.clear();
        fakeCuboids.clear();
        fakeBlocks.clear();
        world = glassSteppingStones.getGlassZone().getWorld();
        first = glassSteppingStones.getGlassZone().getFirstPoint();
        second = glassSteppingStones.getGlassZone().getSecondPoint();
        sizeX = glassSteppingStones.getGlassZone().getSizeX();
        sizeZ = glassSteppingStones.getGlassZone().getSizeZ();
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

        Bukkit.getConsoleSender().sendMessage("Platforms " + rightBlocks.size());

        //Modify Cuboid Size
        for (int i = 0; i < leftBlocks.size(); i++) {
            Cuboid newCuboid = moveCubeFromTopRight(leftBlocks.get(i));
            leftBlocks.set(i, newCuboid);
        }
        for (int i = 0; i < rightBlocks.size(); i++) {
            Cuboid newCuboid = moveCubeFromTopRight(rightBlocks.get(i));
            rightBlocks.set(i, newCuboid);
        }


        //Generate Tiles (cannot run async // to improve performance make this work without API)
        for (Cuboid cuboid : leftBlocks) {
            for (Block block : cuboid.getBlocks()) {
                block.setType(material);
            }
        }
        for (Cuboid cuboid : rightBlocks) {
            for (Block block : cuboid.getBlocks()) {
                block.setType(material);
            }
        }

        //Change Breakable Blocks to Player Count
        int playerCount;
        if (count <= rightBlocks.size() && count > 0) {
            playerCount = count;
        } else {
            playerCount = rightBlocks.size();
        }

        //Make Breakable Blocks
        if (leftBlocks.size() == rightBlocks.size()) {
            for (int i = 0; i < playerCount; i++) {
                Random random = new Random();
                boolean isFirstFake = random.nextBoolean();
                if (isFirstFake) {
                    fakeCuboids.add(leftBlocks.get(i));
                } else {
                    fakeCuboids.add(rightBlocks.get(i));
                }
            }
        }
        Bukkit.getConsoleSender().sendMessage("Fake Platforms " + fakeCuboids.size());
        //Add Random Data to List of block (May not be needed)
        for (Cuboid fakeCuboid : fakeCuboids) {
            fakeBlocks.addAll(fakeCuboid.getBlocks());
        }
    }

    private void getRightX() {
        for (Cuboid leftBlock : leftBlocks) {
            Cuboid newCuboid = moveCubeToRightSide(leftBlock);
            rightBlocks.add(newCuboid);
        }
    }

    private void getLeftX() {
        Vector lFirstBlock = first;
        Cuboid cuboidInfile = new Cuboid(world,lFirstBlock, lFirstBlock);
        leftBlocks.add(cuboidInfile);
        //Iterate X
        lFirstBlock = iterateX(first);
        if (xtoPos) {
            while (Math.abs(lFirstBlock.getBlockX()) >= Math.abs(second.getBlockX())) {
                //Add Cuboid
                leftBlocks.add(new Cuboid(world,lFirstBlock, lFirstBlock));
                //Iterate X
                lFirstBlock = iterateX(lFirstBlock);
            }
        } else {
            while (Math.abs(lFirstBlock.getBlockX()) <= Math.abs(second.getBlockX())) {
                //Add Cuboid
                leftBlocks.add(new Cuboid(world,lFirstBlock, lFirstBlock));
                //Iterate X
                lFirstBlock = iterateX(lFirstBlock);
            }
        }
    }

    private void getLeftZ() {
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

    private void getRightZ() {
        for (Cuboid leftBlock : leftBlocks) {
            Cuboid newCuboid = moveCubeToRightSide(leftBlock);
            rightBlocks.add(newCuboid);
        }
    }

    private Cuboid moveCubeFromTopRight(Cuboid cuboid) {
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

    private Cuboid moveCubeToRightSide(Cuboid cuboid) {
        Cuboid newCuboid;
        if (Math.abs(sizeZ) > Math.abs(sizeX)) {
            //Z is Index
            if (xtoPos) {
                Cuboid tempCuboid = cuboid.expand(Cuboid.CuboidDirection.North,-4);
                newCuboid = tempCuboid.expand(Cuboid.CuboidDirection.North,-4);
            } else {
                Cuboid tempCuboid = cuboid.expand(Cuboid.CuboidDirection.South,-4);
                newCuboid = tempCuboid.expand(Cuboid.CuboidDirection.South,-4);
            }
        } else {
            //X is Indexed
            if (ztoPos) {
                Cuboid tempCuboid = cuboid.expand(Cuboid.CuboidDirection.East,-4);
                newCuboid = tempCuboid.expand(Cuboid.CuboidDirection.East,-4);
            } else {
                Cuboid tempCuboid = cuboid.expand(Cuboid.CuboidDirection.West,-4);
                newCuboid = tempCuboid.expand(Cuboid.CuboidDirection.West,-4);
            }
        }
        return newCuboid;
    }
}
