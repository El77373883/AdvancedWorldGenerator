package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class VillageBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int baseX = chunk.getX() * 16 + 4;
        int baseZ = chunk.getZ() * 16 + 4;
        int y = getSurface(world, baseX + 8, baseZ + 8);
        if (y < 63 || y > 180) return;

        for (int i = -8; i <= 8; i++) {
            setBlock(world, baseX + 8, y, baseZ + 8 + i, Material.COBBLESTONE);
            setBlock(world, baseX + 8 + i, y, baseZ + 8, Material.COBBLESTONE);
        }

        int houseCount = 3 + rand.nextInt(5);
        for (int h = 0; h < houseCount; h++) {
            int hx = baseX + rand.nextInt(12);
            int hz = baseZ + rand.nextInt(12);
            int hy = getSurface(world, hx, hz);
            if (hy < 63) continue;
            buildEpicHouse(world, hx, hy, hz, rand);
        }

        buildTower(world, baseX + 8, y, baseZ + 8, rand);
        buildVillageWell(world, baseX + 4, y, baseZ + 4, rand);
    }

    private void buildEpicHouse(World w, int x, int y, int z, Random rand) {
        Material wall = rand.nextBoolean() ? Material.STONE_BRICKS : Material.OAK_PLANKS;
        Material roof = rand.nextBoolean() ? Material.DARK_OAK_SLAB : Material.SPRUCE_SLAB;

        int width = 5 + rand.nextInt(4);
        int depth = 5 + rand.nextInt(4);
        int height = 4 + rand.nextInt(3);

        for (int dx = 0; dx < width; dx++)
            for (int dz = 0; dz < depth; dz++)
                setBlock(w, x + dx, y, z + dz, Material.COBBLESTONE);

        for (int dy = 1; dy <= height; dy++) {
            for (int dx = 0; dx < width; dx++) {
                setBlock(w, x + dx, y + dy, z, wall);
                setBlock(w, x + dx, y + dy, z + depth - 1, wall);
            }
            for (int dz = 0; dz < depth; dz++) {
                setBlock(w, x, y + dy, z + dz, wall);
                setBlock(w, x + width - 1, y + dy, z + dz, wall);
            }
        }

        for (int dx = 0; dx < width; dx++)
            for (int dz = 0; dz < depth; dz++)
                setBlock(w, x + dx, y + height + 1, z + dz, roof);

        setBlock(w, x + width / 2, y + 1, z, Material.AIR);
        setBlock(w, x + width / 2, y + 2, z, Material.AIR);
        setBlock(w, x + 1, y + 2, z, Material.GLASS_PANE);
        setBlock(w, x + width - 2, y + 2, z, Material.GLASS_PANE);
        setBlock(w, x + 1, y + 2, z + depth - 1, Material.GLASS_PANE);
        setBlock(w, x + width - 2, y + 2, z + depth - 1, Material.GLASS_PANE);

        // Interior — BED reemplazado por RED_BED
        setBlock(w, x + 1, y + 1, z + 1, Material.CRAFTING_TABLE);
        setBlock(w, x + width - 2, y + 1, z + 1, Material.FURNACE);
        setBlock(w, x + 1, y + 1, z + depth - 2, Material.RED_BED);
        setBlock(w, x + width / 2, y + 2, z + depth / 2, Material.LANTERN);

        if (rand.nextInt(3) == 0) {
            int cx = x + width / 2;
            int cz2 = z + depth / 2;
            setBlock(w, cx, y + 1, cz2, Material.CHEST);
            Block b = w.getBlockAt(cx, y + 1, cz2);
            if (b.getState() instanceof Chest chest) fillLoot(chest.getInventory(), rand);
        }
    }

    private void buildTower(World w, int x, int y, int z, Random rand) {
        int towerHeight = 12 + rand.nextInt(8);
        for (int dy = 0; dy <= towerHeight; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (Math.abs(dx) == 1 || Math.abs(dz) == 1)
                        setBlock(w, x + dx, y + dy, z + dz, Material.STONE_BRICKS);
                }
            }
        }
        for (int dx = -2; dx <= 2; dx++)
            for (int dz = -2; dz <= 2; dz++)
                setBlock(w, x + dx, y + towerHeight + 1, z + dz, Material.STONE_BRICK_SLAB);
        setBlock(w, x, y + towerHeight + 2, z, Material.LANTERN);
        for (int dx = -2; dx <= 2; dx += 2) {
            setBlock(w, x + dx, y + towerHeight + 2, z, Material.STONE_BRICK_WALL);
            setBlock(w, x, y + towerHeight + 2, z + dx, Material.STONE_BRICK_WALL);
        }
    }

    private void buildVillageWell(World w, int x, int y, int z, Random rand) {
        for (int dx = 0; dx <= 2; dx++)
            for (int dz = 0; dz <= 2; dz++)
                setBlock(w, x + dx, y, z + dz, Material.COBBLESTONE);
        setBlock(w, x, y + 1, z, Material.COBBLESTONE_WALL);
        setBlock(w, x + 2, y + 1, z, Material.COBBLESTONE_WALL);
        setBlock(w, x, y + 1, z + 2, Material.COBBLESTONE_WALL);
        setBlock(w, x + 2, y + 1, z + 2, Material.COBBLESTONE_WALL);
        setBlock(w, x + 1, y, z + 1, Material.WATER);
        setBlock(w, x + 1, y + 1, z + 1, Material.WATER);
        setBlock(w, x, y + 3, z + 1, Material.OAK_FENCE);
        setBlock(w, x + 2, y + 3, z + 1, Material.OAK_FENCE);
        setBlock(w, x + 1, y + 4, z + 1, Material.OAK_SLAB);
        setBlock(w, x + 1, y + 3, z + 1, Material.LANTERN);
    }

    private void fillLoot(Inventory inv, Random rand) {
        Material[] lootTable = {Material.DIAMOND, Material.EMERALD, Material.GOLD_INGOT,
            Material.IRON_INGOT, Material.IRON_SWORD, Material.BOW,
            Material.COOKED_BEEF, Material.BREAD, Material.ENCHANTED_GOLDEN_APPLE};
        for (int i = 0; i < 3 + rand.nextInt(5); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(lootTable[rand.nextInt(lootTable.length)], 1 + rand.nextInt(8)));
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }

    private int getSurface(World w, int x, int z) {
        return w.getHighestBlockYAt(x, z);
    }
}
