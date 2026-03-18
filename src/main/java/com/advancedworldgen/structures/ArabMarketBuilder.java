package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ArabMarketBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + 1;
        int z = chunk.getZ() * 16 + 1;
        int y = world.getHighestBlockYAt(x + 7, z + 7);
        if (y < 63 || y > 130) return;

        buildMarketSquare(world, x, y, z, rand);
        buildStalls(world, x, y, z, rand);
        buildMosque(world, x + 10, y, z + 10, rand);
        buildFountain(world, x + 7, y, z + 7, rand);
        buildCamelPen(world, x, y, z + 14, rand);
    }

    private void buildMarketSquare(World w, int x, int y, int z, Random rand) {
        // Plaza central de arena
        for (int dx = 0; dx < 20; dx++)
            for (int dz = 0; dz < 20; dz++)
                setBlock(w, x + dx, y, z + dz,
                    rand.nextInt(4) == 0 ? Material.SANDSTONE : Material.SMOOTH_SANDSTONE);

        // Caminos
        for (int i = 0; i < 20; i++) {
            setBlock(w, x + i, y, z + 10, Material.CHISELED_SANDSTONE);
            setBlock(w, x + 10, y, z + i, Material.CHISELED_SANDSTONE);
        }
    }

    private void buildStalls(World w, int x, int y, int z, Random rand) {
        Material[] colors = {
            Material.ORANGE_WOOL, Material.RED_WOOL, Material.YELLOW_WOOL,
            Material.BLUE_WOOL, Material.PURPLE_WOOL, Material.GREEN_WOOL
        };

        int[][] stallPos = {{x, z}, {x + 7, z}, {x + 14, z}, {x, z + 7}, {x + 14, z + 7}};

        for (int s = 0; s < stallPos.length; s++) {
            int sx = stallPos[s][0], sz = stallPos[s][1];
            Material color = colors[rand.nextInt(colors.length)];

            // Poste
            setBlock(w, sx, y + 1, sz, Material.OAK_FENCE);
            setBlock(w, sx + 3, y + 1, sz, Material.OAK_FENCE);
            setBlock(w, sx, y + 1, sz + 2, Material.OAK_FENCE);
            setBlock(w, sx + 3, y + 1, sz + 2, Material.OAK_FENCE);
            setBlock(w, sx, y + 2, sz, Material.OAK_FENCE);
            setBlock(w, sx + 3, y + 2, sz, Material.OAK_FENCE);
            setBlock(w, sx, y + 2, sz + 2, Material.OAK_FENCE);
            setBlock(w, sx + 3, y + 2, sz + 2, Material.OAK_FENCE);

            // Toldo colorido
            for (int dx = -1; dx <= 4; dx++)
                for (int dz = -1; dz <= 3; dz++)
                    setBlock(w, sx + dx, y + 3, sz + dz, color);

            // Mostrador
            setBlock(w, sx + 1, y + 1, sz + 1, Material.CHEST);
            Block b = w.getBlockAt(sx + 1, y + 1, sz + 1);
            if (b.getState() instanceof Chest chest) fillMarketLoot(chest.getInventory(), rand, s);

            setBlock(w, sx + 2, y + 1, sz + 1, Material.BARREL);
        }
    }

    private void buildMosque(World w, int x, int y, int z, Random rand) {
        int size = 8;

        // Base
        for (int dx = 0; dx < size; dx++)
            for (int dz = 0; dz < size; dz++)
                setBlock(w, x + dx, y, z + dz, Material.SMOOTH_SANDSTONE);

        // Paredes
        for (int dy = 1; dy <= 5; dy++) {
            for (int dx = 0; dx < size; dx++) {
                setBlock(w, x + dx, y + dy, z, Material.SANDSTONE);
                setBlock(w, x + dx, y + dy, z + size - 1, Material.SANDSTONE);
            }
            for (int dz = 0; dz < size; dz++) {
                setBlock(w, x, y + dy, z + dz, Material.SANDSTONE);
                setBlock(w, x + size - 1, y + dy, z + dz, Material.SANDSTONE);
            }
        }

        // Cupula
        for (int dy = 0; dy <= 3; dy++) {
            int radius = 3 - dy;
            for (int dx = -radius; dx <= radius; dx++)
                for (int dz = -radius; dz <= radius; dz++)
                    if (dx * dx + dz * dz <= radius * radius + 1)
                        setBlock(w, x + 3 + dx, y + 6 + dy, z + 3 + dz, Material.SMOOTH_SANDSTONE);
        }
        setBlock(w, x + 3, y + 10, z + 3, Material.GOLD_BLOCK);

        // Minarete
        for (int dy = 1; dy <= 12; dy++)
            setBlock(w, x - 1, y + dy, z - 1, Material.SANDSTONE);
        setBlock(w, x - 1, y + 13, z - 1, Material.GOLD_BLOCK);
        setBlock(w, x - 1, y + 14, z - 1, Material.LANTERN);

        // Interior
        setBlock(w, x + 3, y + 1, z + 3, Material.CHEST);
        Block b = w.getBlockAt(x + 3, y + 1, z + 3);
        if (b.getState() instanceof Chest chest) fillMosqueLoot(chest.getInventory(), rand);
        setBlock(w, x + 4, y + 1, z + 4, Material.ENCHANTING_TABLE);
        setBlock(w, x + 3, y + 4, z + 3, Material.LANTERN);
    }

    private void buildFountain(World w, int x, int y, int z, Random rand) {
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++)
                if (dx * dx + dz * dz <= 4)
                    setBlock(w, x + dx, y, z + dz, Material.SMOOTH_SANDSTONE);

        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++)
                setBlock(w, x + dx, y + 1, z + dz, Material.WATER);

        setBlock(w, x, y + 1, z, Material.WATER);
        setBlock(w, x, y + 2, z, Material.WATER);

        // Bordes
        for (int dx = -2; dx <= 2; dx++) {
            setBlock(w, x + dx, y + 1, z - 2, Material.SANDSTONE_WALL);
            setBlock(w, x + dx, y + 1, z + 2, Material.SANDSTONE_WALL);
        }
        for (int dz = -2; dz <= 2; dz++) {
            setBlock(w, x - 2, y + 1, z + dz, Material.SANDSTONE_WALL);
            setBlock(w, x + 2, y + 1, z + dz, Material.SANDSTONE_WALL);
        }
    }

    private void buildCamelPen(World w, int x, int y, int z, Random rand) {
        // Corral
        for (int dx = 0; dx <= 6; dx++) {
            setBlock(w, x + dx, y + 1, z, Material.OAK_FENCE);
            setBlock(w, x + dx, y + 1, z + 4, Material.OAK_FENCE);
        }
        for (int dz = 0; dz <= 4; dz++) {
            setBlock(w, x, y + 1, z + dz, Material.OAK_FENCE);
            setBlock(w, x + 6, y + 1, z + dz, Material.OAK_FENCE);
        }
        setBlock(w, x + 3, y + 1, z, Material.OAK_FENCE_GATE);
        // Paja
        for (int dx = 1; dx <= 5; dx++)
            for (int dz = 1; dz <= 3; dz++)
                setBlock(w, x + dx, y, z + dz, Material.HAY_BLOCK);
    }

    private void fillMarketLoot(Inventory inv, Random rand, int type) {
        Material[][] tables = {
            {Material.GOLD_INGOT, Material.EMERALD, Material.DIAMOND, Material.IRON_INGOT},
            {Material.BREAD, Material.COOKED_BEEF, Material.GOLDEN_APPLE, Material.APPLE},
            {Material.IRON_SWORD, Material.BOW, Material.ARROW, Material.SHIELD},
            {Material.SADDLE, Material.LEAD, Material.NAME_TAG, Material.EXPERIENCE_BOTTLE},
            {Material.SAND, Material.SANDSTONE, Material.CACTUS, Material.DEAD_BUSH}
        };
        Material[] table = tables[type % tables.length];
        for (int i = 0; i < 5 + rand.nextInt(4); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(table[rand.nextInt(table.length)], 1 + rand.nextInt(8)));
    }

    private void fillMosqueLoot(Inventory inv, Random rand) {
        Material[] loot = {Material.DIAMOND, Material.GOLD_INGOT, Material.EMERALD,
            Material.ENCHANTED_GOLDEN_APPLE, Material.TOTEM_OF_UNDYING, Material.NETHERITE_SCRAP};
        for (int i = 0; i < 5 + rand.nextInt(5); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(loot[rand.nextInt(loot.length)], 1 + rand.nextInt(3)));
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }
}
