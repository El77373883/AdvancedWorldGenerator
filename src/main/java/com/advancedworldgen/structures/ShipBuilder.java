package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ShipBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + 2;
        int z = chunk.getZ() * 16 + 2;
        int y = world.getHighestBlockYAt(x + 6, z + 6);
        buildShip(world, x, y, z, rand);
    }

    private void buildShip(World w, int x, int y, int z, Random rand) {
        int[][] hull = {{2,12},{1,13},{0,14},{0,14},{0,14},{0,14},{0,14},{1,13},{2,12}};

        for (int row = 0; row < hull.length; row++) {
            int startZ = hull[row][0], endZ = hull[row][1];
            for (int dz = startZ; dz <= endZ; dz++) {
                setBlock(w, x + row, y, z + dz, Material.OAK_PLANKS);
                if (dz == startZ || dz == endZ) {
                    setBlock(w, x + row, y + 1, z + dz, Material.OAK_PLANKS);
                    setBlock(w, x + row, y + 2, z + dz, Material.OAK_PLANKS);
                }
            }
            for (int dz = hull[row][0] + 1; dz < hull[row][1]; dz++)
                setBlock(w, x + row, y + 1, z + dz, Material.OAK_PLANKS);
        }

        for (int dy = 2; dy <= 14; dy++) setBlock(w, x + 4, y + dy, z + 7, Material.OAK_LOG);
        for (int dy = 5; dy <= 13; dy++)
            for (int dz = 5; dz <= 9; dz++)
                setBlock(w, x + 4, y + dy, z + dz, Material.WHITE_WOOL);

        for (int dy = 2; dy <= 9; dy++) setBlock(w, x + 7, y + dy, z + 7, Material.OAK_LOG);
        for (int dy = 4; dy <= 8; dy++)
            for (int dz = 6; dz <= 8; dz++)
                setBlock(w, x + 7, y + dy, z + dz, Material.WHITE_WOOL);

        for (int dy = 2; dy <= 5; dy++)
            for (int dz = 5; dz <= 9; dz++)
                if (dy == 2 || dy == 5 || dz == 5 || dz == 9)
                    setBlock(w, x + 7, y + dy, z + dz, Material.OAK_PLANKS);

        for (int dz = 5; dz <= 9; dz++) setBlock(w, x + 7, y + 6, z + dz, Material.SPRUCE_SLAB);

        setBlock(w, x + 7, y + 3, z + 7, Material.CHEST);
        Block chest = w.getBlockAt(x + 7, y + 3, z + 7);
        if (chest.getState() instanceof Chest c) fillShipLoot(c.getInventory(), rand);

        setBlock(w, x + 2, y + 2, z + 7, Material.BARREL);
        setBlock(w, x + 2, y + 2, z + 8, Material.BARREL);
    }

    private void fillShipLoot(Inventory inv, Random rand) {
        Material[] loot = {Material.DIAMOND, Material.GOLD_INGOT, Material.IRON_INGOT,
            Material.EMERALD, Material.COOKED_COD, Material.COMPASS,
            Material.NAUTILUS_SHELL, Material.HEART_OF_THE_SEA};
        for (int i = 0; i < 5 + rand.nextInt(5); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(loot[rand.nextInt(loot.length)], 1 + rand.nextInt(5)));
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }
}
