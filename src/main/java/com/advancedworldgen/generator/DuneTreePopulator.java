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
            if (y < 63 || y > 200) continue;

            Material ground = region.getType(x, y, z);
            if (ground != Material.SAND && ground != Material.GRASS_BLOCK) continue;

            int type = random.nextInt(4);
            int height = 15 + random.nextInt(12);

            switch (type) {
                // Palmera gigante
                case 0 -> EpicTreePopulator.buildPalmTree(region, x, y + 1, z, height, random);
                // Arbol Amazonas en oasis
                case 1 -> EpicTreePopulator.buildAmazonTree(region, x, y + 1, z, height + 10, random);
                // Cactus gigante
                case 2 -> buildGiantCactus(region, x, y + 1, z, random);
                // Palmera doble
                case 3 -> {
                    EpicTreePopulator.buildPalmTree(region, x, y + 1, z, height, random);
                    EpicTreePopulator.buildPalmTree(region, x + 2, y + 1, z + 1, height - 3, random);
                }
            }
        }
    }

    private void buildGiantCactus(LimitedRegion r, int x, int y, int z, Random rand) {
        int h = 5 + rand.nextInt(8);
        for (int dy = 0; dy < h; dy++)
            setBlock(r, x, y + dy, z, Material.CACTUS);
        // Brazos
        if (h > 6) {
            int armY = h / 2;
            for (int arm = 0; arm < 3; arm++) {
                setBlock(r, x + 1, y + armY + arm, z, Material.CACTUS);
                setBlock(r, x - 1, y + armY + arm + 1, z, Material.CACTUS);
            }
        }
    }

    private void setBlock(LimitedRegion r, int x, int y, int z, Material m) {
        try { if (r.isInRegion(x, y, z)) r.setType(x, y, z, m); }
        catch (Exception ignored) {}
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
