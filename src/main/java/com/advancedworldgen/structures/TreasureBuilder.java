package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.util.Random;

public class TreasureBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + 4 + rand.nextInt(8);
        int z = chunk.getZ() * 16 + 4 + rand.nextInt(8);

        int surfaceY = world.getHighestBlockYAt(x, z);
        int treasureY = surfaceY - 3 - rand.nextInt(5);
        if (treasureY < 5) return;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy <= 2; dy++)
                    setBlock(world, x + dx, treasureY + dy, z + dz, Material.AIR);
                setBlock(world, x + dx, treasureY - 1, z + dz, Material.STONE_BRICKS);
                setBlock(world, x + dx, treasureY + 3, z + dz, Material.STONE_BRICKS);
            }
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = 0; dy <= 2; dy++) {
                setBlock(world, x + dx, treasureY + dy, z - 1, Material.STONE_BRICKS);
                setBlock(world, x + dx, treasureY + dy, z + 1, Material.STONE_BRICKS);
            }
        }
        for (int dz = -1; dz <= 1; dz++) {
            for (int dy = 0; dy <= 2; dy++) {
                setBlock(world, x - 1, treasureY + dy, z + dz, Material.STONE_BRICKS);
                setBlock(world, x + 1, treasureY + dy, z + dz, Material.STONE_BRICKS);
            }
        }

        setBlock(world, x, treasureY, z, Material.CHEST);
        Block b = world.getBlockAt(x, treasureY, z);
        if (b.getState() instanceof Chest chest) {
            fillTreasure(chest.getInventory(), rand, world, x, z);
        }

        if (rand.nextInt(4) == 0)
            setBlock(world, x, treasureY + 2, z - 1, Material.TNT);

        setBlock(world, x + 1, treasureY + 1, z, Material.TORCH);
    }

    private void fillTreasure(Inventory inv, Random rand, World world, int x, int z) {
        Material[] epic = {
            Material.DIAMOND, Material.DIAMOND, Material.EMERALD,
            Material.NETHERITE_SCRAP, Material.GOLDEN_APPLE,
            Material.ENCHANTED_GOLDEN_APPLE, Material.DIAMOND_SWORD,
            Material.DIAMOND_CHESTPLATE, Material.TOTEM_OF_UNDYING,
            Material.EXPERIENCE_BOTTLE, Material.ENDER_PEARL
        };
        for (int i = 0; i < 6 + rand.nextInt(6); i++) {
            Material m = epic[rand.nextInt(epic.length)];
            int amount = m == Material.DIAMOND ? 1 + rand.nextInt(6) : 1;
            inv.setItem(rand.nextInt(27), new ItemStack(m, amount));
        }

        try {
            MapView mapView = Bukkit.createMap(world);
            mapView.setScale(MapView.Scale.NORMAL);
            mapView.setCenterX(x + rand.nextInt(500) - 250);
            mapView.setCenterZ(z + rand.nextInt(500) - 250);
            mapView.setTrackingPosition(true);

            ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
            MapMeta meta = (MapMeta) mapItem.getItemMeta();
            if (meta != null) {
                meta.setMapView(mapView);
                meta.setDisplayName("§6✦ Mapa del Tesoro ✦");
                mapItem.setItemMeta(meta);
            }
            inv.setItem(0, mapItem);
        } catch (Exception ignored) {}
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }
}
