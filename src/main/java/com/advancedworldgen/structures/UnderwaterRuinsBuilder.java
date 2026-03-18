package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class UnderwaterRuinsBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + 2;
        int z = chunk.getZ() * 16 + 2;

        // Solo bajo el agua
        int surfY = world.getHighestBlockYAt(x + 7, z + 7);
        if (surfY > 58) return;

        int y = surfY - 3;
        if (y < 20) return;

        buildRuinedTemple(world, x, y, z, rand);
        buildCoralDecoration(world, x, y, z, rand);
        buildSunkenShip(world, x + 10, y, z + 5, rand);
    }

    private void buildRuinedTemple(World w, int x, int y, int z, Random rand) {
        Material[] materials = {Material.PRISMARINE, Material.DARK_PRISMARINE, Material.PRISMARINE_BRICKS};

        // Base
        for (int dx = 0; dx < 10; dx++)
            for (int dz = 0; dz < 10; dz++)
                setBlock(w, x + dx, y, z + dz, materials[rand.nextInt(materials.length)]);

        // Columnas rotas
        int[][] cols = {{0,0},{0,9},{9,0},{9,9},{4,4}};
        for (int[] col : cols) {
            int colH = 3 + rand.nextInt(5);
            boolean broken = rand.nextInt(2) == 0;
            for (int dy = 1; dy <= (broken ? colH - 1 : colH); dy++)
                setBlock(w, x + col[0], y + dy, z + col[1], Material.PRISMARINE_PILLAR);
            if (!broken)
                setBlock(w, x + col[0], y + colH + 1, z + col[1], Material.SEA_LANTERN);
        }

        // Altar con tesoro
        setBlock(w, x + 4, y + 1, z + 4, Material.DARK_PRISMARINE);
        setBlock(w, x + 4, y + 2, z + 4, Material.CHEST);
        Block b = w.getBlockAt(x + 4, y + 2, z + 4);
        if (b.getState() instanceof Chest chest) fillUnderwaterLoot(chest.getInventory(), rand);

        // Linternas del mar
        for (int i = 0; i < 4; i++) {
            setBlock(w, x + 1 + i * 2, y + 1, z + 1, Material.SEA_LANTERN);
            setBlock(w, x + 1 + i * 2, y + 1, z + 8, Material.SEA_LANTERN);
        }
    }

    private void buildCoralDecoration(World w, int x, int y, int z, Random rand) {
        Material[] corals = {
            Material.BRAIN_CORAL, Material.BUBBLE_CORAL,
            Material.FIRE_CORAL, Material.HORN_CORAL, Material.TUBE_CORAL
        };
        Material[] coralBlocks = {
            Material.BRAIN_CORAL_BLOCK, Material.BUBBLE_CORAL_BLOCK,
            Material.FIRE_CORAL_BLOCK, Material.HORN_CORAL_BLOCK, Material.TUBE_CORAL_BLOCK
        };

        for (int i = 0; i < 20; i++) {
            int cx = x - 5 + rand.nextInt(20);
            int cz2 = z - 5 + rand.nextInt(20);
            int cy = w.getHighestBlockYAt(cx, cz2);
            if (cy < 58) {
                setBlock(w, cx, cy, cz2, coralBlocks[rand.nextInt(coralBlocks.length)]);
                setBlock(w, cx, cy + 1, cz2, corals[rand.nextInt(corals.length)]);
            }
        }

        // Algas
        for (int i = 0; i < 10; i++) {
            int ax = x + rand.nextInt(12);
            int az = z + rand.nextInt(12);
            int ay = w.getHighestBlockYAt(ax, az);
            if (ay < 58) {
                int algH = 2 + rand.nextInt(4);
                for (int dy = 0; dy <= algH; dy++)
                    setBlock(w, ax, ay + dy, az, Material.KELP);
            }
        }
    }

    private void buildSunkenShip(World w, int x, int y, int z, Random rand) {
        // Barco hundido inclinado
        for (int dx = 0; dx < 8; dx++) {
            int shipY = y + (dx < 4 ? 0 : -1);
            setBlock(w, x + dx, shipY, z, Material.OAK_PLANKS);
            setBlock(w, x + dx, shipY, z + 3, Material.OAK_PLANKS);
            setBlock(w, x + dx, shipY + 1, z, Material.OAK_PLANKS);
            setBlock(w, x + dx, shipY + 1, z + 3, Material.OAK_PLANKS);
        }
        for (int dz = 0; dz <= 3; dz++) {
            setBlock(w, x, y, z + dz, Material.OAK_PLANKS);
            setBlock(w, x + 7, y - 1, z + dz, Material.OAK_PLANKS);
        }
        // Mastil roto
        for (int dy = 2; dy <= 5; dy++)
            setBlock(w, x + 3, y + dy, z + 1, Material.OAK_LOG);

        // Cofre hundido
        setBlock(w, x + 4, y + 1, z + 1, Material.CHEST);
        Block b = w.getBlockAt(x + 4, y + 1, z + 1);
        if (b.getState() instanceof Chest chest) fillUnderwaterLoot(chest.getInventory(), rand);
    }

    private void fillUnderwaterLoot(Inventory inv, Random rand) {
        Material[] loot = {
            Material.HEART_OF_THE_SEA, Material.NAUTILUS_SHELL,
            Material.DIAMOND, Material.GOLD_INGOT, Material.EMERALD,
            Material.TRIDENT, Material.ENCHANTED_GOLDEN_APPLE,
            Material.SPONGE, Material.PRISMARINE_CRYSTALS
        };
        for (int i = 0; i < 5 + rand.nextInt(5); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(loot[rand.nextInt(loot.length)], 1 + rand.nextInt(3)));
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }
}
