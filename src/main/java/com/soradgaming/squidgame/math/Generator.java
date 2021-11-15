package com.soradgaming.squidgame.math;

import com.soradgaming.squidgame.games.Game6;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class Generator {
    public static void generateTiles(final Material material) {
        final World world = Game6.getGlassZone().getWorld();
        final BlockVector first = Game6.getGlassZone().getFirstPoint();
        final BlockVector second = Game6.getGlassZone().getSecondPoint();
        final int sizeX = Game6.getGlassZone().getSizeX();
        final int sizeZ = Game6.getGlassZone().getSizeZ();
        final boolean xtoPos = second.getBlockX() - first.getBlockX() > 0;
        final boolean ztoPos = second.getBlockZ() - first.getBlockZ() > 0;
        final ArrayList<Cuboid> leftBlocks = new ArrayList<>();
        final ArrayList<Cuboid> rightBlocks = new ArrayList<>();

        //Check Cube is Correct
        if (Game6.getGlassZone().volume() == sizeZ * sizeX) {
            if (sizeZ > sizeX) {
                //use sizeZ is long side of rectangle
                //Check all X then All y then z next(); iterator
                //Get 2x2 Platforms
                Vector lFirstBlock = first;
                Vector lSecondBlock;
                if (xtoPos && ztoPos) {
                    lSecondBlock = first.add(new Vector(1,0,1));
                    leftBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),lSecondBlock.toBlockVector()));
                } else if (!xtoPos && ztoPos) {
                    lSecondBlock = first.add(new Vector(-1,0,1));
                    leftBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),lSecondBlock.toBlockVector()));
                } else if (xtoPos) {
                    lSecondBlock = first.add(new Vector(1,0,-1));
                    leftBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),lSecondBlock.toBlockVector()));
                } else {
                    lSecondBlock = first.add(new Vector(-1,0,-1));
                    leftBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),lSecondBlock.toBlockVector()));
                }
                //Iterate Z
                if (ztoPos) {
                    lFirstBlock = first.add(new Vector(0,0,4));
                } else {
                    lFirstBlock = first.add(new Vector(0,0,-4));
                }
                while (lSecondBlock.getBlockZ() <= second.getBlockZ()) {
                    if (xtoPos && ztoPos) {
                        lSecondBlock = first.add(new Vector(1,0,1));
                        leftBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),lSecondBlock.toBlockVector()));
                    } else if (!xtoPos && ztoPos) {
                        lSecondBlock = first.add(new Vector(-1,0,1));
                        leftBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),lSecondBlock.toBlockVector()));
                    } else if (xtoPos) {
                        lSecondBlock = first.add(new Vector(1,0,-1));
                        leftBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),lSecondBlock.toBlockVector()));
                    } else {
                        lSecondBlock = first.add(new Vector(-1,0,-1));
                        leftBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),lSecondBlock.toBlockVector()));
                    }
                    //Iterate Z
                    if (ztoPos) {
                        lFirstBlock = lFirstBlock.add(new Vector(0,0,4));
                    } else {
                        lFirstBlock = lFirstBlock.add(new Vector(0,0,-4));
                    }
                }
                //TODO get right row in Z
                Vector rFirstBlock;
                if (ztoPos) {
                    rFirstBlock = second.add(new Vector(0,0,-sizeZ));
                } else {
                    rFirstBlock = second.add(new Vector(0,0,+sizeZ));
                }
                Vector rSecondBlock;
                if (xtoPos && ztoPos) {
                    rSecondBlock = first.add(new Vector(1,0,1));
                    rightBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),rSecondBlock.toBlockVector()));
                } else if (!xtoPos && ztoPos) {
                    rSecondBlock = first.add(new Vector(-1,0,1));
                    rightBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),rSecondBlock.toBlockVector()));
                } else if (xtoPos) {
                    rSecondBlock = first.add(new Vector(1,0,-1));
                    rightBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),rSecondBlock.toBlockVector()));
                } else {
                    rSecondBlock = first.add(new Vector(-1,0,-1));
                    rightBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),rSecondBlock.toBlockVector()));
                }
                //Iterate Z TODO
                if (ztoPos) {
                    rFirstBlock = first.add(new Vector(0,0,4));
                } else {
                    rFirstBlock = first.add(new Vector(0,0,-4));
                }
                while (rSecondBlock.getBlockZ() <= second.getBlockZ()) {
                    if (xtoPos && ztoPos) {
                        rSecondBlock = first.add(new Vector(1,0,1));
                        rightBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),rSecondBlock.toBlockVector()));
                    } else if (!xtoPos && ztoPos) {
                        rSecondBlock = first.add(new Vector(-1,0,1));
                        rightBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),rSecondBlock.toBlockVector()));
                    } else if (xtoPos) {
                        rSecondBlock = first.add(new Vector(1,0,-1));
                        rightBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),rSecondBlock.toBlockVector()));
                    } else {
                        rSecondBlock = first.add(new Vector(-1,0,-1));
                        rightBlocks.add(new Cuboid(world,lFirstBlock.toBlockVector(),rSecondBlock.toBlockVector()));
                    }
                    //Iterate Z
                    if (ztoPos) {
                        rFirstBlock = first.add(new Vector(0,0,4));
                    } else {
                        rFirstBlock = first.add(new Vector(0,0,-4));
                    }
                }
            } else {
                //use sizeX is long side of rectangle
            }
        }
        Random random = new Random();
        boolean isFirstFake = random.nextBoolean();
    }
}
