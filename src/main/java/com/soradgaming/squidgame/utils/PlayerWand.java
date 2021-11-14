package com.soradgaming.squidgame.utils;

import com.soradgaming.squidgame.SquidGame;
import com.soradgaming.squidgame.math.Cuboid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerWand {
    private static final SquidGame plugin = SquidGame.plugin;
    private BlockVector firstPoint;
    private BlockVector secondPoint;
    private World world;
    private PlayerWand wand = null;

    public BlockVector getFirstPoint() {
        return this.firstPoint;
    }

    public BlockVector getSecondPoint() {
        return this.secondPoint;
    }

    public Cuboid getCuboid() {
        return new Cuboid(this.world, this.firstPoint, this.secondPoint);
    }

    public void setFirstPoint(final BlockVector vector) {
        this.firstPoint = vector;
    }

    public void setSecondPoint(final BlockVector vector) {
        this.secondPoint = vector;
    }

    public void setFirstPoint(final Location loc, final World world) {
        this.firstPoint = new BlockVector(loc.getX(), loc.getY(), loc.getZ());
        this.world = world;
    }

    public void setSecondPoint(final Location loc) {
        this.secondPoint = new BlockVector(loc.getX(), loc.getY(), loc.getZ());
    }

    public boolean isComplete() {
        return this.firstPoint != null && this.secondPoint != null;
    }

    public PlayerWand getWand() {
        return this.wand;
    }

    public PlayerWand createWand(final PlayerWand wand) {
        this.wand = wand;
        return this.wand;
    }

    public static void wandGive(Player player) {
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

        player.getInventory().addItem(item);
        player.updateInventory();

        PlayerWand squidPlayer = (PlayerWand) player;
        squidPlayer.createWand(new PlayerWand());
    }
}
