package com.soradgaming.squidgame.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class playerWand {
    private static HashMap<Player, BlockVector> firstPoint;
    private static HashMap<Player, BlockVector> secondPoint;
    private static HashMap<Player, World> world;
    private static ItemStack wand = null;

    public static BlockVector getFirstPoint(Player player) {
        return firstPoint.get(player);
    }

    public static BlockVector getSecondPoint(Player player) {
        return secondPoint.get(player);
    }

    public static World getWorld(Player player) {
        return world.get(player);
    }

    public static void setFirstPoint(final Location loc, Player player) {
        firstPoint.put(player, new BlockVector(loc.getX(), loc.getY(), loc.getZ()));
        world.put(player, loc.getWorld());
    }

    public static void setSecondPoint(final Location loc, Player player) {
        secondPoint.put(player, new BlockVector(loc.getX(), loc.getY(), loc.getZ()));
        world.put(player, loc.getWorld());
    }

    public static boolean isComplete(Player player) {
        return firstPoint.get(player) != null && secondPoint.get(player) != null;
    }

    public static ItemStack getWand() {
        if (wand == null) {
            final ItemStack item = new ItemStack(Material.BLAZE_ROD);
            final ItemMeta meta = item.getItemMeta();
            final List<String> lore = new ArrayList<>();

            lore.add(ChatColor.translateAlternateColorCodes('&',"&7"));
            lore.add(ChatColor.translateAlternateColorCodes('&',"&aLeft-click: &eSet first point."));
            lore.add(ChatColor.translateAlternateColorCodes('&',"&aRight-click: &eSet second point."));
            lore.add(ChatColor.translateAlternateColorCodes('&',"&7"));
            Objects.requireNonNull(meta).setLore(lore);

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&dRegion wand &7(Left/Right click)"));
            item.setItemMeta(meta);
            wand = item;
        }
        return wand;
    }
}
