package com.advancedworldgen.generator;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public class EpicTreePopulator extends BlockPopulator {

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion region) {
        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;

        int treeCount = 2 + random.nextInt(4);
        for (int i = 0; i < treeCount; i++) {
            int x = baseX + random.nextInt(16);
            int z = baseZ + random.nextInt(16);
            int y = getSurfaceY(region, x, z);
            if (y < 63 || y > 200) continue;

            try {
                Block ground = region.getBlockAt(x, y, z);
                if (ground.getType() != Material.GRASS_BLOCK && ground.getType() != Material.DIRT) continue;
            } catch (Exception ignored) { continue; }

            int type = random.nextInt(5);
            int height = 18 + random.nextInt(14);

            switch (type) {
                case 0 -> buildAmazonTree(region, x, y + 1, z, height + 15, random);
                case 1 -> buildJungleTree(region, x, y + 1, z, height, random);
                case 2 -> buildOakTree(region, x, y + 1, z, height, random);
                case 3 -> buildPalmTree(region, x, y + 1, z, height - 5, random);
                case 4 -> buildDarkOakTree(region, x, y + 1, z, height, random);
            }
        }
    }

    public static void buildAmazonTree(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        for (int dy = 0; dy < height; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) <= 2)
                        setBlock(r, x + dx, y + dy, z + dz, Material.JUNGLE_LOG);
                }
            }
        }

        int[][] rootDirs = {{3,0},{-3,0},{0,3},{0,-3},{2,2},{-2,2},{2,-2},{-2,-2}};
        for (int[] dir : rootDirs) {
            int rx = dir[0], rz = dir[1];
            for (int step = 0; step <= 3; step++) {
                int bx = x + (int)(rx * step / 3.0);
                int bz = z + (int)(rz * step / 3.0);
                int by = y + 3 - step;
                setBlock(r, bx, by, bz, Material.JUNGLE_LOG);
                setBlock(r, bx, by - 1, bz, Material.JUNGLE_LOG);
            }
        }

        for (int dy = height - 10; dy <= height + 4; dy++) {
            int radius = dy < height - 4 ? 7 : dy < height ? 6 : dy < height + 2 ? 4 : 2;
            for (int dx = -radius; dx <= radius; dx++)
                for (int dz = -radius; dz <= radius; dz++)
                    if (dx * dx + dz * dz <= radius * radius + rand.nextInt(4))
                        setBlockIfAir(r, x + dx, y + dy, z + dz, Material.JUNGLE_LEAVES);
        }

        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                if (rand.nextInt(3) == 0) {
                    int lianaLen = 2 + rand.nextInt(6);
                    for (int dy = 1; dy <= lianaLen; dy++) {
                        int ly = y + height - 8 - dy;
                        try {
                            if (r.isInRegion(x + dx, ly, z + dz) &&
                               (r.getType(x + dx, ly, z + dz) == Material.AIR ||
                                r.getType(x + dx, ly, z + dz) == Material.CAVE_AIR)) {
                                setBlock(r, x + dx, ly, z + dz, Material.VINE);
                            } else break;
                        } catch (Exception ignored) { break; }
                    }
                }
            }
        }
    }

    public static void buildJungleTree(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        for (int dy = 0; dy < height; dy++)
            for (int dx = 0; dx <= 1; dx++)
                for (int dz = 0; dz <= 1; dz++)
                    setBlock(r, x + dx, y + dy, z + dz, Material.JUNGLE_LOG);

        for (int dy = height - 6; dy <= height + 2; dy++) {
            int radius = dy < height - 2 ? 5 : dy < height ? 4 : 3;
            for (int dx = -radius; dx <= radius; dx++)
                for (int dz = -radius; dz <= radius; dz++)
                    if (dx * dx + dz * dz <= radius * radius + rand.nextInt(3))
                        setBlockIfAir(r, x + dx, y + dy, z + dz, Material.JUNGLE_LEAVES);
        }
    }

    public static void buildOakTree(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        for (int dy = 0; dy < height; dy++)
            setBlock(r, x, y + dy, z, Material.OAK_LOG);

        for (int dy = height - 5; dy <= height + 1; dy++) {
            int radius = dy < height - 1 ? 4 : 3;
            for (int dx = -radius; dx <= radius; dx++)
                for (int dz = -radius; dz <= radius; dz++)
                    if (dx * dx + dz * dz <= radius * radius + rand.nextInt(2))
                        setBlockIfAir(r, x + dx, y + dy, z + dz, Material.OAK_LEAVES);
        }
    }

    public static void buildPalmTree(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        int ox = 0, oz = 0;
        for (int dy = 0; dy < height; dy++) {
            if (dy > height / 2 && rand.nextInt(3) == 0) {
                ox += rand.nextBoolean() ? 1 : 0;
                oz += rand.nextBoolean() ? 1 : 0;
            }
            setBlock(r, x + ox, y + dy, z + oz, Material.JUNGLE_LOG);
        }
        int tx = x + ox, tz = z + oz, ty = y + height;
        int[] dxA = {1,-1,0,0,1,-1,1,-1};
        int[] dzA = {0,0,1,-1,1,1,-1,-1};
        for (int dir = 0; dir < 8; dir++)
            for (int len = 1; len <= 5; len++) {
                int ly = ty - (len > 2 ? len - 2 : 0);
                setBlockIfAir(r, tx + dxA[dir] * len, ly, tz + dzA[dir] * len, Material.JUNGLE_LEAVES);
            }
        setBlock(r, tx, ty + 1, tz, Material.JUNGLE_LEAVES);
    }

    public static void buildDarkOakTree(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        for (int dy = 0; dy < height; dy++) {
            setBlock(r, x, y + dy, z, Material.DARK_OAK_LOG);
            setBlock(r, x + 1, y + dy, z, Material.DARK_OAK_LOG);
            setBlock(r, x, y + dy, z + 1, Material.DARK_OAK_LOG);
            setBlock(r, x + 1, y + dy, z + 1, Material.DARK_OAK_LOG);
        }
        for (int dy = height - 4; dy <= height + 2; dy++) {
            int radius = dy <= height ? 6 : 4;
            for (int dx = -radius; dx <= radius; dx++)
                for (int dz = -radius; dz <= radius; dz++)
                    if (dx * dx + dz * dz <= radius * radius + rand.nextInt(4))
                        setBlockIfAir(r, x + dx, y + dy, z + dz, Material.DARK_OAK_LEAVES);
        }
    }

    protected int getSurfaceY(LimitedRegion r, int x, int z) {
        for (int y = 250; y > 60; y--) {
            try {
                Material m = r.getType(x, y, z);
                if (m != Material.AIR && m != Material.CAVE_AIR && m != Material.WATER) return y;
            } catch (Exception ignored) {}
        }
        return 64;
    }

    protected static void setBlock(LimitedRegion r, int x, int y, int z, Material m) {
        try { if (r.isInRegion(x, y, z)) r.setType(x, y, z, m); }
        catch (Exception ignored) {}
    }

    protected static void setBlockIfAir(LimitedRegion r, int x, int y, int z, Material m) {
        try {
            if (r.isInRegion(x, y, z) && r.getType(x, y, z) == Material.AIR)
                r.setType(x, y, z, m);
        } catch (Exception ignored) {}
    }
}
