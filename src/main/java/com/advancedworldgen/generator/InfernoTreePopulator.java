package com.advancedworldgen.generator;

import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public class InfernoTreePopulator extends BlockPopulator {

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion region) {
        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;

        int count = 1 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            int x = baseX + random.nextInt(16);
            int z = baseZ + random.nextInt(16);
            int y = getSurface(region, x, z);
            if (y < 63 || y > 220) continue;

            Material ground = region.getType(x, y, z);
            if (ground != Material.BLACKSTONE && ground != Material.BASALT &&
                ground != Material.GRASS_BLOCK && ground != Material.DIRT) continue;

            int type = random.nextInt(3);
            int height = 14 + random.nextInt(12);

            switch (type) {
                // Arbol quemado
                case 0 -> buildBurnedTree(region, x, y + 1, z, height, random);
                // Arbol de jungla oscuro
                case 1 -> buildDarkJungleTree(region, x, y + 1, z, height + 8, random);
                // Arbol de obsidiana
                case 2 -> buildObsidianTree(region, x, y + 1, z, height - 4, random);
            }
        }
    }

    private void buildBurnedTree(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        // Tronco quemado sin hojas
        for (int dy = 0; dy < height; dy++) {
            setBlock(r, x, y + dy, z, Material.DEAD_BUSH);
            if (dy < height - 3) setBlock(r, x, y + dy, z, Material.STRIPPED_OAK_LOG);
        }
        // Ramas rotas
        for (int branch = 0; branch < 4; branch++) {
            int bh = height - 2 - rand.nextInt(4);
            int dir = rand.nextInt(4);
            int bx = dir == 0 ? 1 : dir == 1 ? -1 : 0;
            int bz2 = dir == 2 ? 1 : dir == 3 ? -1 : 0;
            for (int len = 1; len <= 3; len++)
                setBlock(r, x + bx * len, y + bh, z + bz2 * len, Material.STRIPPED_OAK_LOG);
        }
        // Unas pocas hojas muertas
        for (int dy = height - 3; dy <= height + 1; dy++) {
            for (int dx = -2; dx <= 2; dx++)
                for (int dz = -2; dz <= 2; dz++)
                    if (rand.nextInt(3) == 0)
                        setBlockIfAir(r, x + dx, y + dy, z + dz, Material.DEAD_BUSH);
        }
        // Fuego en la base
        if (rand.nextInt(3) == 0)
            setBlock(r, x, y, z, Material.FIRE);
    }

    private void buildDarkJungleTree(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        for (int dy = 0; dy < height; dy++) {
            for (int dx = 0; dx <= 1; dx++)
                for (int dz = 0; dz <= 1; dz++)
                    setBlock(r, x + dx, y + dy, z + dz, Material.JUNGLE_LOG);
        }
        for (int dy = height - 7; dy <= height + 2; dy++) {
            int radius = dy < height - 3 ? 6 : dy < height ? 5 : 3;
            for (int dx = -radius; dx <= radius; dx++)
                for (int dz = -radius; dz <= radius; dz++)
                    if (dx * dx + dz * dz <= radius * radius + rand.nextInt(3))
                        setBlockIfAir(r, x + dx, y + dy, z + dz, Material.JUNGLE_LEAVES);
        }
    }

    private void buildObsidianTree(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        for (int dy = 0; dy < height; dy++)
            setBlock(r, x, y + dy, z, Material.OBSIDIAN);
        for (int dy = height - 3; dy <= height + 2; dy++) {
            int radius = 3;
            for (int dx = -radius; dx <= radius; dx++)
                for (int dz = -radius; dz <= radius; dz++)
                    if (dx * dx + dz * dz <= radius * radius)
                        setBlockIfAir(r, x + dx, y + dy, z + dz, Material.SHROOMLIGHT);
        }
    }

    private void setBlock(LimitedRegion r, int x, int y, int z, Material m) {
        try { if (r.isInRegion(x, y, z)) r.setType(x, y, z, m); }
        catch (Exception ignored) {}
    }

    private void setBlockIfAir(LimitedRegion r, int x, int y, int z, Material m) {
        try {
            if (r.isInRegion(x, y, z) && r.getType(x, y, z) == Material.AIR)
                r.setType(x, y, z, m);
        } catch (Exception ignored) {}
    }

    private int getSurface(LimitedRegion r, int x, int z) {
        for (int y = 250; y > 60; y--) {
            try {
                Material m = r.getType(x, y, z);
                if (m != Material.AIR && m != Material.CAVE_AIR && m != Material.WATER) return y;
            } catch (Exception ignored) {}
        }
        return 64;
    }
}
