package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class RuinsBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + rand.nextInt(10);
        int z = chunk.getZ() * 16 + rand.nextInt(10);
        int y = world.getHighestBlockYAt(x + 4, z + 4);
        if (y < 120 || y > 250) return;

        int type = rand.nextInt(3);
        switch (type) {
            case 0 -> buildAncientTemple(world, x, y, z, rand);
            case 1 -> buildBrokenTower(world, x, y, z, rand);
            case 2 -> buildStoneCircle(world, x, y, z, rand);
        }
    }

    private void buildAncientTemple(World w, int x, int y, int z, Random rand) {
        Material[] materials = {Material.STONE_BRICKS, Material.MOSSY_STONE_BRICKS, Material.CRACKED_STONE_BRICKS};
        int width = 7, depth = 9, height = 6;

        for (int dx = 0; dx < width; dx++)
            for (int dz = 0; dz < depth; dz++)
                setBlock(w, x + dx, y, z + dz, materials[rand.nextInt(materials.length)]);

        for (int dy = 1; dy <= height; dy++) {
            for (int dx = 0; dx < width; dx++) {
                if (rand.nextInt(5) != 0) setBlock(w, x + dx, y + dy, z, materials[rand.nextInt(materials.length)]);
                if (rand.nextInt(5) != 0) setBlock(w, x + dx, y + dy, z + depth - 1, materials[rand.nextInt(materials.length)]);
            }
            for (int dz = 0; dz < depth; dz++) {
                if (rand.nextInt(5) != 0) setBlock(w, x, y + dy, z + dz, materials[rand.nextInt(materials.length)]);
                if (rand.nextInt(5) != 0) setBlock(w, x + width - 1, y + dy, z + dz, materials[rand.nextInt(materials.length)]);
            }
        }

        setBlock(w, x + width / 2, y + 1, z + depth / 2, Material.CHISELED_STONE_BRICKS);
        setBlock(w, x + width / 2, y + 2, z + depth / 2, Material.ENCHANTING_TABLE);

        setBlock(w, x + 1, y + 1, z + 1, Material.CHEST);
        Block b = w.getBlockAt(x + 1, y + 1, z + 1);
        if (b.getState() instanceof Chest chest) fillRuinsLoot(chest.getInventory(), rand);

        setBlock(w, x + 1, y + 2, z + 1, Material.TORCH);
        setBlock(w, x + width - 2, y + 2, z + depth - 2, Material.TORCH);
    }

    private void buildBrokenTower(World w, int x, int y, int z, Random rand) {
        Material[] materials = {Material.STONE_BRICKS, Material.MOSSY_STONE_BRICKS, Material.COBBLESTONE};
        int towerHeight = 8 + rand.nextInt(10);

        for (int dy = 0; dy < towerHeight; dy++) {
            for (int dx = 0; dx <= 4; dx++) {
                for (int dz = 0; dz <= 4; dz++) {
                    boolean wall = dx == 0 || dx == 4 || dz == 0 || dz == 4;
                    if (wall) {
                        if (dy > towerHeight - 4 && rand.nextInt(3) == 0) continue;
                        setBlock(w, x + dx, y + dy, z + dz, materials[rand.nextInt(materials.length)]);
                    }
                }
            }
        }

        for (int dy = 0; dy < towerHeight; dy++) {
            int sx = x + 2 + (int)(Math.cos(dy * 0.8) * 1.5);
            int sz = z + 2 + (int)(Math.sin(dy * 0.8) * 1.5);
            setBlock(w, sx, y + dy, sz, Material.OAK_SLAB);
        }

        setBlock(w, x + 2, y + towerHeight, z + 2, Material.CHEST);
        Block b = w.getBlockAt(x + 2, y + towerHeight, z + 2);
        if (b.getState() instanceof Chest chest) fillRuinsLoot(chest.getInventory(), rand);
    }

    private void buildStoneCircle(World w, int x, int y, int z, Random rand) {
        int radius = 7;
        int cx = x + radius, cz = z + radius;

        for (int angle = 0; angle < 360; angle += 30) {
            double rad = Math.toRadians(angle);
            int px = cx + (int)(Math.cos(rad) * radius);
            int pz = cz + (int)(Math.sin(rad) * radius);
            int stonePillarH = 2 + rand.nextInt(4);
            for (int dy = 0; dy <= stonePillarH; dy++)
                setBlock(w, px, y + dy, pz, Material.STONE_BRICKS);
            if (rand.nextBoolean())
                setBlock(w, px + 1, y + stonePillarH, pz, Material.STONE_BRICK_SLAB);
        }

        setBlock(w, cx, y + 1, cz, Material.CHISELED_DEEPSLATE);
        setBlock(w, cx, y + 2, cz, Material.SOUL_LANTERN);
    }

    private void fillRuinsLoot(Inventory inv, Random rand) {
        Material[] loot = {Material.MAP, Material.COMPASS, Material.GOLDEN_APPLE,
            Material.IRON_INGOT, Material.GOLD_INGOT, Material.DIAMOND,
            Material.ENDER_PEARL, Material.BLAZE_POWDER, Material.EXPERIENCE_BOTTLE};
        for (int i = 0; i < 4 + rand.nextInt(5); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(loot[rand.nextInt(loot.length)], 1 + rand.nextInt(4)));
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }
}
