package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class TreeVillageBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + 4;
        int z = chunk.getZ() * 16 + 4;
        int y = world.getHighestBlockYAt(x + 4, z + 4);
        if (y < 63 || y > 180) return;

        // Construir 4 arboles grandes con casas
        int[][] treePositions = {{x, z}, {x + 12, z + 2}, {x + 2, z + 12}, {x + 12, z + 12}};

        for (int t = 0; t < treePositions.length; t++) {
            int tx = treePositions[t][0];
            int tz = treePositions[t][1];
            int ty = world.getHighestBlockYAt(tx, tz);
            if (ty < 63) continue;

            int treeHeight = 18 + rand.nextInt(8);
            buildSupportTree(world, tx, ty + 1, tz, treeHeight, rand);
            buildTreeHouse(world, tx, ty + treeHeight - 2, tz, rand);
        }

        // Puentes entre arboles
        buildRopeBridge(world, x + 1, y + 17, z + 1, x + 13, y + 17, z + 3, rand);
        buildRopeBridge(world, x + 3, y + 17, z + 13, x + 13, y + 17, z + 13, rand);
    }

    private void buildSupportTree(World w, int x, int y, int z, int height, Random rand) {
        // Tronco grueso 2x2
        for (int dy = 0; dy < height; dy++) {
            setBlock(w, x, y + dy, z, Material.JUNGLE_LOG);
            setBlock(w, x + 1, y + dy, z, Material.JUNGLE_LOG);
            setBlock(w, x, y + dy, z + 1, Material.JUNGLE_LOG);
            setBlock(w, x + 1, y + dy, z + 1, Material.JUNGLE_LOG);
        }

        // Copa grande
        for (int dy = height - 6; dy <= height + 2; dy++) {
            int radius = dy < height - 2 ? 6 : dy < height ? 5 : 3;
            for (int dx = -radius; dx <= radius; dx++)
                for (int dz = -radius; dz <= radius; dz++)
                    if (dx * dx + dz * dz <= radius * radius + rand.nextInt(3))
                        setBlockIfAir(w, x + dx, y + dy, z + dz, Material.JUNGLE_LEAVES);
        }

        // Raices
        int[][] roots = {{-1, 0}, {2, 0}, {0, -1}, {0, 2}};
        for (int[] root : roots) {
            for (int dy = 0; dy < 3; dy++)
                setBlock(w, x + root[0], y + dy - 1, z + root[1], Material.JUNGLE_LOG);
        }
    }

    private void buildTreeHouse(World w, int x, int y, int z, Random rand) {
        int size = 6;

        // Plataforma
        for (int dx = -2; dx <= size; dx++)
            for (int dz = -2; dz <= size; dz++)
                setBlock(w, x + dx, y, z + dz, Material.JUNGLE_PLANKS);

        // Barandas
        for (int dx = -2; dx <= size; dx++) {
            setBlock(w, x + dx, y + 1, z - 2, Material.OAK_FENCE);
            setBlock(w, x + dx, y + 1, z + size, Material.OAK_FENCE);
        }
        for (int dz = -2; dz <= size; dz++) {
            setBlock(w, x - 2, y + 1, z + dz, Material.OAK_FENCE);
            setBlock(w, x + size, y + 1, z + dz, Material.OAK_FENCE);
        }

        // Casa en la plataforma
        for (int dy = 1; dy <= 4; dy++) {
            for (int dx = 0; dx < 5; dx++) {
                setBlock(w, x + dx, y + dy, z, Material.JUNGLE_PLANKS);
                setBlock(w, x + dx, y + dy, z + 4, Material.JUNGLE_PLANKS);
            }
            for (int dz = 0; dz < 5; dz++) {
                setBlock(w, x, y + dy, z + dz, Material.JUNGLE_PLANKS);
                setBlock(w, x + 4, y + dy, z + dz, Material.JUNGLE_PLANKS);
            }
        }

        // Techo
        for (int dx = 0; dx < 5; dx++)
            for (int dz = 0; dz < 5; dz++)
                setBlock(w, x + dx, y + 5, z + dz, Material.JUNGLE_SLAB);

        // Puerta y ventanas
        setBlock(w, x + 2, y + 1, z, Material.AIR);
        setBlock(w, x + 2, y + 2, z, Material.AIR);
        setBlock(w, x + 1, y + 2, z, Material.GLASS_PANE);
        setBlock(w, x + 3, y + 2, z, Material.GLASS_PANE);

        // Interior
        setBlock(w, x + 1, y + 1, z + 3, Material.CRAFTING_TABLE);
        setBlock(w, x + 3, y + 1, z + 3, Material.CHEST);
        Block b = w.getBlockAt(x + 3, y + 1, z + 3);
        if (b.getState() instanceof Chest chest) fillTreeLoot(chest.getInventory(), rand);
        setBlock(w, x + 2, y + 3, z + 2, Material.LANTERN);

        // Escalera de cuerda al suelo
        for (int dy = 0; dy > -18; dy--)
            setBlock(w, x + 2, y + dy, z + 2, Material.LADDER);
    }

    private void buildRopeBridge(World w, int x1, int y1, int z1, int x2, int y2, int z2, Random rand) {
        int length = x2 - x1;
        for (int i = 0; i <= length; i++) {
            double progress = (double) i / length;
            int bridgeY = (int) (y1 + (y2 - y1) * progress - Math.sin(progress * Math.PI) * 2);
            int bx = x1 + i;
            int bz = (int) (z1 + (z2 - z1) * progress);

            setBlock(w, bx, bridgeY, bz, Material.OAK_PLANKS);
            setBlock(w, bx, bridgeY, bz + 1, Material.OAK_PLANKS);
            setBlock(w, bx, bridgeY + 1, bz, Material.OAK_FENCE);
            setBlock(w, bx, bridgeY + 1, bz + 1, Material.OAK_FENCE);
        }
    }

    private void fillTreeLoot(Inventory inv, Random rand) {
        Material[] loot = {Material.GOLDEN_APPLE, Material.IRON_SWORD,
            Material.BOW, Material.ARROW, Material.COOKED_BEEF,
            Material.EMERALD, Material.GOLD_INGOT, Material.ENDER_PEARL};
        for (int i = 0; i < 4 + rand.nextInt(5); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(loot[rand.nextInt(loot.length)], 1 + rand.nextInt(8)));
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }

    private void setBlockIfAir(World w, int x, int y, int z, Material m) {
        try {
            if (w.getBlockAt(x, y, z).getType() == Material.AIR)
                w.getBlockAt(x, y, z).setType(m, false);
        } catch (Exception ignored) {}
    }
}
