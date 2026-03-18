package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class HarborBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + 1;
        int z = chunk.getZ() * 16 + 1;
        int y = world.getHighestBlockYAt(x + 7, z + 7);

        // Solo en costas cerca del agua
        boolean nearWater = false;
        for (int dx = -3; dx <= 3 && !nearWater; dx++)
            for (int dz = -3; dz <= 3 && !nearWater; dz++)
                if (world.getBlockAt(x + dx, 62, z + dz).getType() == Material.WATER)
                    nearWater = true;

        if (!nearWater || y < 60 || y > 80) return;

        buildDock(world, x, y, z, rand);
        buildWarehouse(world, x + 14, y, z, rand);
        buildLighthouse(world, x, y, z + 14, rand);
        buildBoats(world, x, y, z, rand);
        buildMarket(world, x + 6, y, z + 6, rand);
    }

    private void buildDock(World w, int x, int y, int z, Random rand) {
        // Muelle principal de madera
        for (int dx = 0; dx < 14; dx++) {
            for (int dz = 0; dz < 4; dz++) {
                setBlock(w, x + dx, y, z + dz, Material.OAK_PLANKS);
                if (dx % 3 == 0)
                    setBlock(w, x + dx, y - 1, z + dz, Material.OAK_LOG);
            }
            // Barandas
            setBlock(w, x + dx, y + 1, z, Material.OAK_FENCE);
            setBlock(w, x + dx, y + 1, z + 3, Material.OAK_FENCE);
        }

        // Extension al agua
        for (int dz = 4; dz < 10; dz++) {
            for (int dx = 3; dx < 7; dx++) {
                setBlock(w, x + dx, y, z + dz, Material.OAK_PLANKS);
                setBlock(w, x + dx, y - 1, z + dz, Material.OAK_LOG);
            }
        }

        // Faroles en el muelle
        for (int dx = 0; dx < 14; dx += 4) {
            setBlock(w, x + dx, y + 1, z, Material.OAK_FENCE);
            setBlock(w, x + dx, y + 2, z, Material.LANTERN);
        }

        // Cajas y barriles
        setBlock(w, x + 2, y + 1, z + 1, Material.BARREL);
        setBlock(w, x + 3, y + 1, z + 1, Material.BARREL);
        setBlock(w, x + 2, y + 1, z + 2, Material.CHEST);
        Block b = w.getBlockAt(x + 2, y + 1, z + 2);
        if (b.getState() instanceof Chest chest) fillHarborLoot(chest.getInventory(), rand);
    }

    private void buildWarehouse(World w, int x, int y, int z, Random rand) {
        int width = 8, depth = 10, height = 6;

        // Base
        for (int dx = 0; dx < width; dx++)
            for (int dz = 0; dz < depth; dz++)
                setBlock(w, x + dx, y, z + dz, Material.COBBLESTONE);

        // Paredes
        for (int dy = 1; dy <= height; dy++) {
            for (int dx = 0; dx < width; dx++) {
                setBlock(w, x + dx, y + dy, z, Material.OAK_PLANKS);
                setBlock(w, x + dx, y + dy, z + depth - 1, Material.OAK_PLANKS);
            }
            for (int dz = 0; dz < depth; dz++) {
                setBlock(w, x, y + dy, z + dz, Material.OAK_PLANKS);
                setBlock(w, x + width - 1, y + dy, z + dz, Material.OAK_PLANKS);
            }
        }

        // Techo
        for (int dx = 0; dx < width; dx++)
            for (int dz = 0; dz < depth; dz++)
                setBlock(w, x + dx, y + height + 1, z + dz, Material.SPRUCE_SLAB);

        // Puerta
        setBlock(w, x + 3, y + 1, z, Material.AIR);
        setBlock(w, x + 3, y + 2, z, Material.AIR);
        setBlock(w, x + 4, y + 1, z, Material.AIR);
        setBlock(w, x + 4, y + 2, z, Material.AIR);

        // Interior almacen
        for (int i = 0; i < 4; i++) {
            int bx = x + 1 + (i % 2) * 4;
            int bz2 = z + 2 + (i / 2) * 4;
            setBlock(w, bx, y + 1, bz2, Material.BARREL);
            setBlock(w, bx + 1, y + 1, bz2, Material.CHEST);
            Block b = w.getBlockAt(bx + 1, y + 1, bz2);
            if (b.getState() instanceof Chest chest) fillHarborLoot(chest.getInventory(), rand);
        }
        setBlock(w, x + 3, y + 3, z + 4, Material.LANTERN);
    }

    private void buildLighthouse(World w, int x, int y, int z, Random rand) {
        int height = 18 + rand.nextInt(6);

        // Torre del faro
        for (int dy = 0; dy <= height; dy++) {
            for (int dx = 0; dx < 4; dx++) {
                for (int dz = 0; dz < 4; dz++) {
                    boolean isWall = dx == 0 || dx == 3 || dz == 0 || dz == 3;
                    if (isWall) setBlock(w, x + dx, y + dy, z + dz,
                        dy % 3 == 0 ? Material.STONE_BRICKS : Material.WHITE_CONCRETE);
                }
            }
        }

        // Luz del faro
        setBlock(w, x + 1, y + height + 1, z + 1, Material.SEA_LANTERN);
        setBlock(w, x + 2, y + height + 1, z + 1, Material.SEA_LANTERN);
        setBlock(w, x + 1, y + height + 1, z + 2, Material.SEA_LANTERN);
        setBlock(w, x + 2, y + height + 1, z + 2, Material.SEA_LANTERN);

        // Balcon
        for (int dx = -1; dx <= 4; dx++)
            for (int dz = -1; dz <= 4; dz++)
                setBlock(w, x + dx, y + height, z + dz, Material.STONE_BRICK_SLAB);

        for (int dx = -1; dx <= 4; dx++) {
            setBlock(w, x + dx, y + height + 1, z - 1, Material.STONE_BRICK_WALL);
            setBlock(w, x + dx, y + height + 1, z + 4, Material.STONE_BRICK_WALL);
        }

        // Escalera de caracol
        for (int dy = 1; dy < height; dy++) {
            int sx = x + 1 + (int)(Math.cos(dy * 0.8) * 1);
            int sz = z + 1 + (int)(Math.sin(dy * 0.8) * 1);
            setBlock(w, sx, y + dy, sz, Material.OAK_SLAB);
        }
    }

    private void buildBoats(World w, int x, int y, int z, Random rand) {
        // Barco pequeno atracado
        int bx = x + 4, bz = z + 8;
        for (int dx = 0; dx < 6; dx++) {
            setBlock(w, bx + dx, y, bz, Material.OAK_PLANKS);
            setBlock(w, bx + dx, y, bz + 3, Material.OAK_PLANKS);
            setBlock(w, bx + dx, y + 1, bz, Material.OAK_PLANKS);
            setBlock(w, bx + dx, y + 1, bz + 3, Material.OAK_PLANKS);
        }
        for (int dz = 0; dz <= 3; dz++) {
            setBlock(w, bx, y, bz + dz, Material.OAK_PLANKS);
            setBlock(w, bx + 5, y, bz + dz, Material.OAK_PLANKS);
        }
        // Mastil
        for (int dy = 2; dy <= 8; dy++)
            setBlock(w, bx + 2, y + dy, bz + 1, Material.OAK_LOG);
        for (int dy = 3; dy <= 7; dy++)
            for (int dz = 0; dz <= 2; dz++)
                setBlockIfAir(w, bx + 2, y + dy, bz + dz, Material.WHITE_WOOL);
    }

    private void buildMarket(World w, int x, int y, int z, Random rand) {
        // Puestos de mercado
        String[] stalls = {"fish", "food", "tools"};
        for (int i = 0; i < 3; i++) {
            int sx = x + i * 5;
            // Techo del puesto
            for (int dx = 0; dx < 4; dx++)
                for (int dz = 0; dz < 3; dz++)
                    setBlock(w, sx + dx, y + 3, z + dz,
                        i == 0 ? Material.BLUE_WOOL : i == 1 ? Material.RED_WOOL : Material.YELLOW_WOOL);
            // Poste
            setBlock(w, sx, y + 1, z, Material.OAK_FENCE);
            setBlock(w, sx, y + 2, z, Material.OAK_FENCE);
            setBlock(w, sx + 3, y + 1, z, Material.OAK_FENCE);
            setBlock(w, sx + 3, y + 2, z, Material.OAK_FENCE);
            // Mostrador
            setBlock(w, sx + 1, y + 1, z + 1, Material.CHEST);
            Block b = w.getBlockAt(sx + 1, y + 1, z + 1);
            if (b.getState() instanceof Chest chest) fillMarketLoot(chest.getInventory(), rand, i);
        }
    }

    private void fillHarborLoot(Inventory inv, Random rand) {
        Material[] loot = {Material.COD, Material.SALMON, Material.NAUTILUS_SHELL,
            Material.HEART_OF_THE_SEA, Material.COMPASS, Material.MAP,
            Material.GOLD_INGOT, Material.IRON_INGOT, Material.EMERALD};
        for (int i = 0; i < 4 + rand.nextInt(5); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(loot[rand.nextInt(loot.length)], 1 + rand.nextInt(8)));
    }

    private void fillMarketLoot(Inventory inv, Random rand, int type) {
        Material[][] tables = {
            {Material.COD, Material.SALMON, Material.TROPICAL_FISH, Material.INK_SAC},
            {Material.BREAD, Material.APPLE, Material.COOKED_BEEF, Material.GOLDEN_APPLE},
            {Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SWORD, Material.SHEARS}
        };
        Material[] table = tables[type % 3];
        for (int i = 0; i < 5 + rand.nextInt(4); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(table[rand.nextInt(table.length)], 1 + rand.nextInt(8)));
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
