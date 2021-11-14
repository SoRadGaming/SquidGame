package com.soradgaming.squidgame.utils;

import com.soradgaming.squidgame.SquidGame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerWand {
    private static BlockVector firstPoint;
    private static BlockVector secondPoint;
    private static ItemStack wand = null;

    public static BlockVector getFirstPoint() {
        return firstPoint;
    }

    public static BlockVector getSecondPoint() {
        return secondPoint;
    }

    public static void setFirstPoint(final Location loc) {
        firstPoint = new BlockVector(loc.getX(), loc.getY(), loc.getZ());
    }

    public static void setSecondPoint(final Location loc) {
        secondPoint = new BlockVector(loc.getX(), loc.getY(), loc.getZ());
    }

    public static boolean isComplete() {
        return firstPoint != null && secondPoint != null;
    }

    public static ItemStack getWand() {
        if (wand == null) {
            final ItemStack item = new ItemStack(Material.BLAZE_ROD);
            final ItemMeta meta = item.getItemMeta();
            final List<String> lore = new ArrayList<>();

            lore.add("§7");
            lore.add("§aLeft-click: §eSet first point.");
            lore.add("§aRight-click: §eSet second point.");
            lore.add("§7");
            Objects.requireNonNull(meta).setLore(lore);

            meta.setDisplayName("§dRegion wand §7(Left/Right click)");
            item.setItemMeta(meta);
            wand = item;
        }
        return wand;
    }
}
