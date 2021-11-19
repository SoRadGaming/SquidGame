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
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.io.*;
import java.util.Objects;

public class WorldEditHook {
    private static SquidGame plugin = SquidGame.plugin;
    private static BlockVector3 pos1;
    private static BlockVector3 pos2;
    private static World world;
    private static Clipboard clipboard;
    private static Cuboid cuboid;

    public static void cuboidToBlockVector3(Cuboid cuboid, String key) throws IOException {
        Location loc1 = cuboid.getFirstPoint().toLocation(cuboid.getWorld());
        Location loc2 = cuboid.getSecondPoint().toLocation(cuboid.getWorld());
        pos1 = BukkitAdapter.asBlockVector(loc1);
        pos2 = BukkitAdapter.asBlockVector(loc2);
        world = BukkitAdapter.adapt(cuboid.getWorld());
        //TODO REMOVE AFTER TEST
        File schematic = new File("plugins/SquidGame/schematics/" + key);
        load(schematic);
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

    public static void paste() {
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

    public static void verifyBuild() {
        EditSession editSession = WorldEdit.getInstance().newEditSession(world);
        CuboidRegion region = new ClipboardHolder(clipboard).getClipboard().getRegion().getBoundingBox();
        //turn region into cuboid
        BlockVector3 pos1 = region.getPos1();
        BlockVector3 pos2 = region.getPos2();
        Cuboid displayCube =  BlockVector3ToCuboid(pos1,pos2);
        //now you can compare display cuboid to build one using own code
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
        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Cuboid getCuboid() {
        if (cuboid == null) {
            BlockVector vector1 = gameManager.configToVectors("Game2.cuboid.first_point");
            BlockVector vector2 = gameManager.configToVectors("Game2.cuboid.second_point");
            org.bukkit.World world = Bukkit.getWorld(Objects.requireNonNull(plugin.getConfig().getString("Game2.world")));
            cuboid = new Cuboid(Objects.requireNonNull(world),vector1.getBlockX(),vector1.getBlockY(),vector1.getBlockZ(),vector2.getBlockX(),vector2.getBlockY(),vector2.getBlockZ());
        }
        return cuboid;
    }

    public static void reloadCuboids() {
        cuboid = null;
    }

}
