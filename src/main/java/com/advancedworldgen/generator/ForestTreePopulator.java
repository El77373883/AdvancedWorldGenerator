package com.advancedworldgen.generator;

import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public class ForestTreePopulator extends BlockPopulator {

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion region) {
        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;

        // Bosque muy denso
        int count = 4 + random.nextInt(6);
        for (int i = 0; i < count; i++) {
            int x = baseX + random.nextInt(16);
            int z = baseZ + random.nextInt(16);
            int y = getSurface(region, x, z);
            if (y < 63 || y > 210) continue;

            Material ground = region.getType(x, y, z);
            if (ground != Material.GRASS_BLOCK && ground != Material.DIRT) continue;

            int type = random.nextInt(5);
            int height = 20 + random.nextInt(20);

            switch (type) {
                // Arbol Amazonas enorme estilo selva
                case 0 -> EpicTreePopulator.buildAmazonTree(region, x, y + 1, z, height + 15, random);
                // Dark oak gigante
                case 1 -> EpicTreePopulator.buildDarkOakTree(region, x, y + 1, z, height, random);
                // Arbol de jungla
                case 2 -> EpicTreePopulator.buildJungleTree(region, x, y + 1, z, height, random);
                // Hongo gigante
                case 3 -> buildGiantMushroom(region, x, y + 1, z, height - 5, random);
                // Pino enorme
                case 4 -> buildEpicPine(region, x, y + 1, z, height + 5, random);
            }
        }
    }

    private void buildGiantMushroom(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        for (int dy = 0; dy < height; dy++)
            setBlock(r, x, y + dy, z, rand.nextBoolean() ? Material.MUSHROOM_STEM : Material.BROWN_MUSHROOM_BLOCK);

        int capRadius = 5 + rand.nextInt(4);
        for (int dy = height - 2; dy <= height + 2; dy++) {
            int radius = dy == height ? capRadius : capRadius - 2;
            for (int dx = -radius; dx <= radius; dx++)
                for (int dz = -radius; dz <= radius; dz++)
                    if (dx * dx + dz * dz <= radius * radius)
                        setBlockIfAir(r, x + dx, y + dy, z + dz,
                            rand.nextBoolean() ? Material.RED_MUSHROOM_BLOCK : Material.BROWN_MUSHROOM_BLOCK);
        }
    }

    private void buildEpicPine(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        for (int dy = 0; dy < height; dy++)
            setBlock(r, x, y + dy, z, Material.SPRUCE_LOG);

        int layers = height / 2;
        for (int layer = 0; layer < layers; layer++) {
            int radius = (layers - layer) / 2 + 2;
            int yLevel = y + height - 2 - (layer * 2);
            for (int dx = -radius; dx <= radius; dx++)
                for (int dz = -radius; dz <= radius; dz++)
                    if (Math.abs(dx) + Math.abs(dz) <= radius + 1)
                        setBlockIfAir(r, x + dx, yLevel, z + dz, Material.SPRUCE_LEAVES);
        }
        setBlock(r, x, y + height, z, Material.SPRUCE_LEAVES);
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
