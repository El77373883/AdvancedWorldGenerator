package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class MayaTempleBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + 2;
        int z = chunk.getZ() * 16 + 2;
        int y = world.getHighestBlockYAt(x + 6, z + 6);
        if (y < 63 || y > 180) return;

        buildBase(world, x, y, z, rand);
        buildPyramid(world, x, y, z, rand);
        buildInterior(world, x, y, z, rand);
        buildDecorations(world, x, y, z, rand);
    }

    private void buildBase(World w, int x, int y, int z, Random rand) {
        // Base de piedra grande 20x20
        for (int dx = 0; dx < 20; dx++)
            for (int dz = 0; dz < 20; dz++)
                setBlock(w, x + dx, y, z + dz, Material.MOSSY_STONE_BRICKS);

        // Escalinatas en los 4 lados
        for (int step = 0; step < 5; step++) {
            for (int i = step; i < 20 - step; i++) {
                setBlock(w, x + i, y + step + 1, z + step, Material.STONE_BRICK_STAIRS);
                setBlock(w, x + i, y + step + 1, z + 19 - step, Material.STONE_BRICK_STAIRS);
                setBlock(w, x + step, y + step + 1, z + i, Material.STONE_BRICK_STAIRS);
                setBlock(w, x + 19 - step, y + step + 1, z + i, Material.STONE_BRICK_STAIRS);
            }
        }
    }

    private void buildPyramid(World w, int x, int y, int z, Random rand) {
        int levels = 7;
        for (int level = 0; level < levels; level++) {
            int offset = level * 1;
            int size = 20 - offset * 2;
            int ly = y + level + 1;
            if (size <= 0) break;

            for (int dx = offset; dx < offset + size; dx++) {
                for (int dz = offset; dz < offset + size; dz++) {
                    boolean isWall = dx == offset || dx == offset + size - 1 ||
                                     dz == offset || dz == offset + size - 1;
                    Material m = level % 2 == 0 ? Material.STONE_BRICKS :
                                 level % 3 == 0 ? Material.MOSSY_STONE_BRICKS :
                                 Material.CRACKED_STONE_BRICKS;
                    if (isWall || level == levels - 1)
                        setBlock(w, x + dx, ly, z + dz, m);
                    else if (rand.nextInt(4) == 0)
                        setBlock(w, x + dx, ly, z + dz, Material.MOSSY_COBBLESTONE);
                }
            }
        }

        // Templo en la cima
        int topX = x + 7, topZ = z + 7, topY = y + levels + 1;
        for (int dy = 0; dy < 5; dy++) {
            for (int dx = 0; dx < 6; dx++) {
                for (int dz = 0; dz < 6; dz++) {
                    boolean isWall = dx == 0 || dx == 5 || dz == 0 || dz == 5;
                    if (isWall || dy == 0 || dy == 4)
                        setBlock(w, topX + dx, topY + dy, topZ + dz, Material.CHISELED_STONE_BRICKS);
                }
            }
        }
        // Techo del templo
        for (int dx = -1; dx <= 6; dx++)
            for (int dz = -1; dz <= 6; dz++)
                setBlock(w, topX + dx, topY + 5, topZ + dz, Material.STONE_BRICK_SLAB);
    }

    private void buildInterior(World w, int x, int y, int z, Random rand) {
        // Camara secreta dentro de la piramide
        int cx = x + 8, cz = z + 8, cy = y + 2;

        for (int dx = 0; dx < 4; dx++)
            for (int dz = 0; dz < 4; dz++)
                for (int dy = 0; dy < 3; dy++)
                    setBlock(w, cx + dx, cy + dy, cz + dz, Material.AIR);

        // Altar
        setBlock(w, cx + 1, cy, cz + 1, Material.CHISELED_DEEPSLATE);
        setBlock(w, cx + 1, cy + 1, cz + 1, Material.ENCHANTING_TABLE);

        // Cofres del tesoro
        setBlock(w, cx, cy, cz, Material.CHEST);
        Block b1 = w.getBlockAt(cx, cy, cz);
        if (b1.getState() instanceof Chest c) fillMayaLoot(c.getInventory(), rand);

        setBlock(w, cx + 3, cy, cz + 3, Material.CHEST);
        Block b2 = w.getBlockAt(cx + 3, cy, cz + 3);
        if (b2.getState() instanceof Chest c) fillMayaLoot(c.getInventory(), rand);

        // Trampas
        setBlock(w, cx + 1, cy, cz + 2, Material.TNT);
        setBlock(w, cx + 2, cy, cz + 1, Material.TNT);

        // Antorchas
        setBlock(w, cx, cy + 1, cz + 3, Material.TORCH);
        setBlock(w, cx + 3, cy + 1, cz, Material.TORCH);
    }

    private void buildDecorations(World w, int x, int y, int z, Random rand) {
        // Pilares decorativos en las esquinas
        for (int dy = 0; dy < 4; dy++) {
            setBlock(w, x, y + dy + 1, z, Material.CHISELED_STONE_BRICKS);
            setBlock(w, x + 19, y + dy + 1, z, Material.CHISELED_STONE_BRICKS);
            setBlock(w, x, y + dy + 1, z + 19, Material.CHISELED_STONE_BRICKS);
            setBlock(w, x + 19, y + dy + 1, z + 19, Material.CHISELED_STONE_BRICKS);
        }

        // Antorchas en pilares
        setBlock(w, x + 1, y + 4, z, Material.TORCH);
        setBlock(w, x + 18, y + 4, z, Material.TORCH);
        setBlock(w, x + 1, y + 4, z + 19, Material.TORCH);
        setBlock(w, x + 18, y + 4, z + 19, Material.TORCH);

        // Enredaderas en paredes
        for (int dx = 0; dx < 20; dx += 3)
            for (int dy = 1; dy < 5; dy++)
                if (rand.nextInt(2) == 0)
                    setBlock(w, x + dx, y + dy, z, Material.VINE);

        // Arbustos alrededor
        for (int dx = -2; dx < 22; dx += 2) {
            if (rand.nextInt(2) == 0) setBlock(w, x + dx, y + 1, z - 1, Material.OAK_LEAVES);
            if (rand.nextInt(2) == 0) setBlock(w, x + dx, y + 1, z + 20, Material.JUNGLE_LEAVES);
        }
    }

    private void fillMayaLoot(Inventory inv, Random rand) {
        Material[] loot = {
            Material.DIAMOND, Material.EMERALD, Material.GOLD_INGOT,
            Material.ENCHANTED_GOLDEN_APPLE, Material.TOTEM_OF_UNDYING,
            Material.DIAMOND_SWORD, Material.DIAMOND_CHESTPLATE,
            Material.NETHERITE_SCRAP, Material.EXPERIENCE_BOTTLE,
            Material.ENDER_PEARL, Material.BLAZE_ROD, Material.NAME_TAG
        };
        for (int i = 0; i < 8 + rand.nextInt(6); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(loot[rand.nextInt(loot.length)], 1 + rand.nextInt(4)));
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }
}
