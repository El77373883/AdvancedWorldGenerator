package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class CityBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int baseX = chunk.getX() * 16 + 2;
        int baseZ = chunk.getZ() * 16 + 2;
        int y = world.getHighestBlockYAt(baseX + 7, baseZ + 7);
        if (y < 63 || y > 160) return;

        // Plaza central
        for (int dx = 0; dx < 14; dx++)
            for (int dz = 0; dz < 14; dz++)
                setBlock(world, baseX + dx, y, baseZ + dz, Material.STONE_BRICKS);

        // Calles
        for (int i = 0; i < 14; i++) {
            setBlock(world, baseX + i, y, baseZ + 7, Material.CHISELED_STONE_BRICKS);
            setBlock(world, baseX + 7, y, baseZ + i, Material.CHISELED_STONE_BRICKS);
        }

        // 4 edificios en esquinas
        buildSkyscraper(world, baseX, y, baseZ, rand);
        buildSkyscraper(world, baseX + 8, y, baseZ, rand);
        buildSkyscraper(world, baseX, y, baseZ + 8, rand);
        buildSkyscraper(world, baseX + 8, y, baseZ + 8, rand);

        buildFountain(world, baseX + 6, y, baseZ + 6, rand);
        buildLampPost(world, baseX + 3, y + 1, baseZ + 3);
        buildLampPost(world, baseX + 10, y + 1, baseZ + 3);
        buildLampPost(world, baseX + 3, y + 1, baseZ + 10);
        buildLampPost(world, baseX + 10, y + 1, baseZ + 10);
    }

    private void buildSkyscraper(World w, int x, int y, int z, Random rand) {
        int width = 5;
        int floors = 4 + rand.nextInt(5);
        int floorHeight = 4;
        int totalHeight = floors * floorHeight;

        for (int dy = 0; dy < totalHeight; dy++) {
            boolean isFloorCeiling = dy % floorHeight == 0 || dy % floorHeight == floorHeight - 1;
            for (int dx = 0; dx < width; dx++) {
                for (int dz = 0; dz < width; dz++) {
                    boolean isWall = dx == 0 || dx == width - 1 || dz == 0 || dz == width - 1;
                    if (isFloorCeiling) {
                        setBlock(w, x + dx, y + dy + 1, z + dz, Material.STONE_BRICKS);
                    } else if (isWall) {
                        boolean isWindow = (dx == 1 || dx == width - 2 || dz == 1 || dz == width - 2)
                                && dy % floorHeight == 2;
                        setBlock(w, x + dx, y + dy + 1, z + dz,
                            isWindow ? Material.GLASS : Material.STONE_BRICKS);
                    }
                }
            }
        }

        for (int dx = 0; dx < width; dx++)
            for (int dz = 0; dz < width; dz++)
                setBlock(w, x + dx, y + totalHeight + 1, z + dz, Material.CHISELED_STONE_BRICKS);
        setBlock(w, x + 2, y + totalHeight + 2, z + 2, Material.LANTERN);
    }

    private void buildFountain(World w, int x, int y, int z, Random rand) {
        for (int dx = -1; dx <= 3; dx++)
            for (int dz = -1; dz <= 3; dz++)
                setBlock(w, x + dx, y + 1, z + dz, Material.STONE_BRICKS);
        setBlock(w, x, y + 2, z, Material.STONE_BRICK_WALL);
        setBlock(w, x + 2, y + 2, z, Material.STONE_BRICK_WALL);
        setBlock(w, x, y + 2, z + 2, Material.STONE_BRICK_WALL);
        setBlock(w, x + 2, y + 2, z + 2, Material.STONE_BRICK_WALL);
        setBlock(w, x + 1, y + 2, z + 1, Material.WATER);
    }

    private void buildLampPost(World w, int x, int y, int z) {
        setBlock(w, x, y, z, Material.IRON_BARS);
        setBlock(w, x, y + 1, z, Material.IRON_BARS);
        setBlock(w, x, y + 2, z, Material.IRON_BARS);
        setBlock(w, x, y + 3, z, Material.LANTERN);
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }
}
