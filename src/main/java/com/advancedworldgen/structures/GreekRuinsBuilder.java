package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class GreekRuinsBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + 2;
        int z = chunk.getZ() * 16 + 2;
        int y = world.getHighestBlockYAt(x + 7, z + 7);
        if (y < 63 || y > 120) return;

        buildTempleBase(world, x, y, z, rand);
        buildColumns(world, x, y, z, rand);
        buildAltar(world, x, y, z, rand);
        buildRuinedWalls(world, x, y, z, rand);
        buildStatues(world, x, y, z, rand);
    }

    private void buildTempleBase(World w, int x, int y, int z, Random rand) {
        // Base de marmol blanco
        for (int dx = 0; dx < 16; dx++)
            for (int dz = 0; dz < 16; dz++) {
                setBlock(w, x + dx, y, z + dz, Material.QUARTZ_BLOCK);
                setBlock(w, x + dx, y - 1, z + dz, Material.QUARTZ_PILLAR);
            }
        // Escalones
        for (int i = 0; i < 16; i++) {
            setBlock(w, x + i, y + 1, z - 1, Material.QUARTZ_STAIRS);
            setBlock(w, x + i, y + 1, z + 16, Material.QUARTZ_STAIRS);
        }
    }

    private void buildColumns(World w, int x, int y, int z, Random rand) {
        // Columnas en filas
        int[][] cols = {{0,0},{0,4},{0,8},{0,12},{0,15},{15,0},{15,4},{15,8},{15,12},{15,15}};
        for (int[] col : cols) {
            int cx = x + col[0], cz = z + col[1];
            int colH = 6 + rand.nextInt(4);
            // Algunas columnas rotas
            boolean broken = rand.nextInt(3) == 0;
            int actualH = broken ? colH - rand.nextInt(3) : colH;
            for (int dy = 1; dy <= actualH; dy++)
                setBlock(w, cx, y + dy, cz, Material.QUARTZ_PILLAR);
            if (!broken)
                setBlock(w, cx, y + actualH + 1, cz, Material.CHISELED_QUARTZ_BLOCK);
            // Columnas caidas
            if (broken && rand.nextInt(2) == 0) {
                for (int dl = 1; dl <= 3; dl++)
                    setBlock(w, cx + dl, y + 1, cz, Material.QUARTZ_PILLAR);
            }
        }
    }

    private void buildAltar(World w, int x, int y, int z, Random rand) {
        int ax = x + 6, az = z + 6;
        // Altar central
        for (int dx = 0; dx < 4; dx++)
            for (int dz = 0; dz < 4; dz++)
                setBlock(w, ax + dx, y + 1, az + dz, Material.CHISELED_QUARTZ_BLOCK);

        setBlock(w, ax + 1, y + 2, az + 1, Material.ENCHANTING_TABLE);
        setBlock(w, ax + 2, y + 2, az + 1, Material.BEACON);
        setBlock(w, ax + 1, y + 2, az + 2, Material.CHEST);
        Block b = w.getBlockAt(ax + 1, y + 2, az + 2);
        if (b.getState() instanceof Chest chest) fillGreekLoot(chest.getInventory(), rand);

        // Fuego eterno
        setBlock(w, ax + 2, y + 2, az + 2, Material.NETHERRACK);
        setBlock(w, ax + 2, y + 3, az + 2, Material.FIRE);
    }

    private void buildRuinedWalls(World w, int x, int y, int z, Random rand) {
        // Paredes parcialmente destruidas
        for (int dx = 2; dx < 14; dx++) {
            if (rand.nextInt(3) != 0) {
                int wallH = 2 + rand.nextInt(4);
                for (int dy = 1; dy <= wallH; dy++) {
                    setBlock(w, x + dx, y + dy, z + 2, Material.QUARTZ_BLOCK);
                    setBlock(w, x + dx, y + dy, z + 13, Material.QUARTZ_BLOCK);
                }
            }
        }
        // Escombros
        for (int i = 0; i < 8; i++) {
            int rx = x + rand.nextInt(14);
            int rz = z + rand.nextInt(14);
            setBlock(w, rx, y + 1, rz, Material.CHISELED_QUARTZ_BLOCK);
            setBlock(w, rx + 1, y + 1, rz, Material.QUARTZ_SLAB);
        }
    }

    private void buildStatues(World w, int x, int y, int z, Random rand) {
        // Estatuas en las esquinas
        int[][] statuePos = {{x + 1, z + 1}, {x + 14, z + 1}, {x + 1, z + 14}, {x + 14, z + 14}};
        for (int[] pos : statuePos) {
            if (rand.nextInt(2) == 0) {
                setBlock(w, pos[0], y + 1, pos[1], Material.CHISELED_STONE_BRICKS);
                setBlock(w, pos[0], y + 2, pos[1], Material.PLAYER_HEAD);
                setBlock(w, pos[0], y + 3, pos[1], Material.STONE_BRICKS);
            }
        }
    }

    private void fillGreekLoot(Inventory inv, Random rand) {
        Material[] loot = {
            Material.DIAMOND, Material.EMERALD, Material.GOLD_INGOT,
            Material.ENCHANTED_GOLDEN_APPLE, Material.EXPERIENCE_BOTTLE,
            Material.TOTEM_OF_UNDYING, Material.NETHERITE_SCRAP,
            Material.DIAMOND_CHESTPLATE, Material.ELYTRA
        };
        for (int i = 0; i < 6 + rand.nextInt(5); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(loot[rand.nextInt(loot.length)], 1 + rand.nextInt(3)));
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }
}
