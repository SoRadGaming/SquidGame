package com.soradgaming.squidgame.math;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.utils.gameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class WorldEditHook {
    private static BlockVector3 pos1;
    private static BlockVector3 pos2;
    private static World world;
    private static Clipboard clipboard;


    public static void cuboidToBlockVector3(Cuboid cuboid) {
        Location loc1 = cuboid.getFirstPoint().toLocation(cuboid.getWorld());
        Location loc2 = cuboid.getSecondPoint().toLocation(cuboid.getWorld());
        pos1 = BukkitAdapter.asBlockVector(loc1);
        pos2 = BukkitAdapter.asBlockVector(loc2);
        world = BukkitAdapter.adapt(cuboid.getWorld());
    }

    public static void load(File file) throws IOException {
        //Load
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        }
        /* use the clipboard here */
        paste();
    }

    private static void paste() {
        //Paste (Load before this)
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                    .to(BlockVector3.at(pos1.getBlockX(),pos1.getBlockY(),pos1.getBlockZ()))
                    // configure here
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    public static Cuboid getClipboardCuboid() {
        CuboidRegion region = new ClipboardHolder(clipboard).getClipboard().getRegion().getBoundingBox();
        BlockVector3 pos1 = region.getPos1();
        BlockVector3 pos2 = region.getPos2();
        return BlockVector3ToCuboid(pos1,pos2);
    }

    private static Cuboid BlockVector3ToCuboid(BlockVector3 pos1, BlockVector3 pos2) {
        return new Cuboid(BukkitAdapter.adapt(world),pos1.getBlockX(),pos1.getBlockY(),pos1.getBlockZ(),pos2.getBlockX(),pos2.getBlockY(),pos2.getBlockZ());
    }

    public static void copy() throws WorldEditException {
        //Copy (Save after this)
        CuboidRegion region = new CuboidRegion(pos1, pos2);
        BlockArrayClipboard clipboardArray = new BlockArrayClipboard(region);

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                    editSession, region, clipboardArray, region.getMinimumPoint()
            );
            // configure here
            Operations.complete(forwardExtentCopy);
        }

    }

    public static void save(File file) {
        //Save
        try (ClipboardWriter writer = BuiltInClipboardFormat.MCEDIT_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
