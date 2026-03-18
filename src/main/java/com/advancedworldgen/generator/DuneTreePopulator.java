package com.advancedworldgen.generator;

import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public class DuneTreePopulator extends BlockPopulator {

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion region) {
        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;

        int count = 1 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            int x = baseX + random.nextInt(16);
            int z = baseZ + random.nextInt(16);
            int y = getSurface(region, x, z);
            if (y < 63 || y > 250) continue;

            Material ground;
            try { ground = region.getType(x, y, z); }
            catch (Exception e) { continue; }

            int height = 20 + random.nextInt(20);

            // Cerezo en bioma cerezo (grass block en zona media)
            if (ground == Material.GRASS_BLOCK) {
                double biomeCheck = new NoiseUtil(worldInfo.getSeed() + 5000)
                    .octaveNoise(x / 600.0, z / 600.0, 4, 0.5, 2.0);

                if (biomeCheck >= -0.05 && biomeCheck < 0.25) {
                    // ZONA CEREZO — árboles de cerezo gigantes 40%
                    buildCherryTree(region, x, y + 1, z, height + 10, random);
                } else if (biomeCheck >= 0.25) {
                    // ZONA MONTANA — pinos gigantes
                    if (y > 80 && y < 200) buildEpicPine(region, x, y + 1, z, height + 8, random);
                } else {
                    // ZONA PRADERA — robles
                    EpicTreePopulator.buildOakTree(region, x, y + 1, z, height, random);
                }
            }

            // Palmeras en arena — playas e islas
            if (ground == Material.SAND && y > 62 && y < 75) {
                if (random.nextInt(3) == 0)
                    buildEpicPalm(region, x, y + 1, z, 15 + random.nextInt(12), random);
            }
        }
    }

    // Arbol de cerezo GIGANTE épico
    public static void buildCherryTree(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        // Tronco grueso 2x2
        for (int dy = 0; dy < height; dy++) {
            setBlock(r, x, y + dy, z, Material.CHERRY_LOG);
            setBlock(r, x + 1, y + dy, z, Material.CHERRY_LOG);
            setBlock(r, x, y + dy, z + 1, Material.CHERRY_LOG);
            setBlock(r, x + 1, y + dy, z + 1, Material.CHERRY_LOG);
        }

        // Ramas laterales grandes
        int[] branchHeights = {height / 3, height / 2, height * 2 / 3, height - 4};
        int[][] dirs = {{3,0},{-3,0},{0,3},{0,-3},{2,2},{-2,2},{2,-2},{-2,-2}};
        for (int bh : branchHeights) {
            for (int[] dir : dirs) {
                if (rand.nextInt(2) == 0) {
                    int bLen = 2 + rand.nextInt(3);
                    for (int bl = 1; bl <= bLen; bl++) {
                        setBlock(r, x + dir[0] * bl / bLen, y + bh + bl / 2, z + dir[1] * bl / bLen, Material.CHERRY_LOG);
                    }
                }
            }
        }

        // Copa ENORME rosada
        for (int dy = height - 8; dy <= height + 5; dy++) {
            int radius = dy < height - 3 ? 8 : dy < height + 1 ? 7 : dy < height + 3 ? 5 : 3;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz <= radius * radius + rand.nextInt(5)) {
                        setBlockIfAir(r, x + dx, y + dy, z + dz, Material.CHERRY_LEAVES);
                    }
                }
            }
        }

        // Raices
        int[][] roots = {{-1,0},{2,0},{0,-1},{0,2},{-1,-1},{2,2}};
        for (int[] root : roots) {
            for (int dy = 0; dy < 3; dy++)
                setBlock(r, x + root[0], y - dy, z + root[1], Material.CHERRY_LOG);
        }
    }

    // Palmera GIGANTE épica
    public static void buildEpicPalm(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        int ox = 0, oz = 0;
        for (int dy = 0; dy < height; dy++) {
            if (dy > height / 2) {
                if (rand.nextInt(4) == 0) ox += rand.nextBoolean() ? 1 : -1;
                if (rand.nextInt(4) == 0) oz += rand.nextBoolean() ? 1 : -1;
                ox = Math.max(-2, Math.min(2, ox));
                oz = Math.max(-2, Math.min(2, oz));
            }
            setBlock(r, x + ox, y + dy, z + oz, Material.JUNGLE_LOG);
        }

        int tx = x + ox, tz = z + oz, ty = y + height;

        // Hojas largas en 8 direcciones
        int[] dxA = {1,-1,0,0,1,-1,1,-1,2,-2,0,0};
        int[] dzA = {0,0,1,-1,1,1,-1,-1,0,0,2,-2};
        for (int dir = 0; dir < dxA.length; dir++) {
            int maxLen = 6 + rand.nextInt(4);
            for (int len = 1; len <= maxLen; len++) {
                int lx = tx + dxA[dir] * len;
                int lz = tz + dzA[dir] * len;
                int ly = ty - (len > 3 ? (len - 3) : 0);
                setBlockIfAir(r, lx, ly, lz, Material.JUNGLE_LEAVES);
                if (len > 4 && rand.nextInt(2) == 0)
                    setBlockIfAir(r, lx, ly - 1, lz, Material.JUNGLE_LEAVES);
            }
        }
        setBlock(r, tx, ty + 1, tz, Material.JUNGLE_LEAVES);
        setBlock(r, tx, ty + 2, tz, Material.JUNGLE_LEAVES);
    }

    // Pino épico gigante para montañas
    private void buildEpicPine(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        for (int dy = 0; dy < height; dy++)
            setBlock(r, x, y + dy, z, Material.SPRUCE_LOG);

        int layers = height / 2;
        for (int layer = 0; layer < layers; layer++) {
            int radius = (layers - layer) / 2 + 2;
            int yLevel = y + height - 2 - (layer * 2);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) <= radius + 1) {
                        setBlockIfAir(r, x + dx, yLevel, z + dz, Material.SPRUCE_LEAVES);
                        if (rand.nextInt(5) == 0)
                            setBlockIfAir(r, x + dx, yLevel + 1, z + dz, Material.SNOW);
                    }
                }
            }
        }
        setBlock(r, x, y + height, z, Material.SPRUCE_LEAVES);
        setBlock(r, x, y + height + 1, z, Material.SNOW);
    }

    private int getSurface(LimitedRegion r, int x, int z) {
        for (int y = 260; y > 60; y--) {
            try {
                Material m = r.getType(x, y, z);
                if (m != Material.AIR && m != Material.CAVE_AIR && m != Material.WATER) return y;
            } catch (Exception ignored) {}
        }
        return 64;
    }

    private static void setBlock(LimitedRegion r, int x, int y, int z, Material m) {
        try { if (r.isInRegion(x, y, z)) r.setType(x, y, z, m); }
        catch (Exception ignored) {}
    }

    private static void setBlockIfAir(LimitedRegion r, int x, int y, int z, Material m) {
        try {
            if (r.isInRegion(x, y, z) && r.getType(x, y, z) == Material.AIR)
                r.setType(x, y, z, m);
        } catch (Exception ignored) {}
    }
}
