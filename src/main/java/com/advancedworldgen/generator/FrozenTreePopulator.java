package com.advancedworldgen.generator;

import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public class FrozenTreePopulator extends BlockPopulator {

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion region) {
        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;

        int count = 2 + random.nextInt(5);
        for (int i = 0; i < count; i++) {
            int x = baseX + random.nextInt(16);
            int z = baseZ + random.nextInt(16);
            int y = getSurface(region, x, z);
            if (y < 63 || y > 230) continue;

            Material ground = region.getType(x, y, z);
            if (ground != Material.SNOW_BLOCK && ground != Material.GRASS_BLOCK &&
                ground != Material.DIRT) continue;

            int type = random.nextInt(3);
            int height = 16 + random.nextInt(14);

            switch (type) {
                case 0 -> buildFrozenPine(region, x, y + 1, z, height + 6, random);
                case 1 -> buildFrozenSpruce(region, x, y + 1, z, height, random);
                case 2 -> buildIceTree(region, x, y + 1, z, height - 4, random);
            }
        }
    }

    private void buildFrozenPine(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        // Pino nevado gigante
        for (int dy = 0; dy < height; dy++)
            setBlock(r, x, y + dy, z, Material.SPRUCE_LOG);
        // Nieve en el tronco
        for (int dy = height / 2; dy < height; dy += 3)
            setBlock(r, x + 1, y + dy, z, Material.SNOW_BLOCK);

        // Copa conica enorme con nieve
        int layers = height / 2;
        for (int layer = 0; layer < layers; layer++) {
            int radius = (layers - layer) / 2 + 1;
            int yLevel = y + height - 2 - (layer * 2);
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) + Math.abs(dz) <= radius + 1) {
                        setBlockIfAir(r, x + dx, yLevel, z + dz, Material.SPRUCE_LEAVES);
                        // Nieve encima de las hojas
                        if (rand.nextInt(3) == 0)
                            setBlockIfAir(r, x + dx, yLevel + 1, z + dz, Material.SNOW);
                    }
                }
            }
        }
        setBlock(r, x, y + height, z, Material.SPRUCE_LEAVES);
        setBlock(r, x, y + height + 1, z, Material.SNOW);
    }

    private void buildFrozenSpruce(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        for (int dy = 0; dy < height; dy++)
            setBlock(r, x, y + dy, z, Material.SPRUCE_LOG);

        for (int dy = height - 4; dy <= height + 1; dy++) {
            int radius = dy < height - 1 ? 4 : 2;
            for (int dx = -radius; dx <= radius; dx++)
                for (int dz = -radius; dz <= radius; dz++)
                    if (dx * dx + dz * dz <= radius * radius + rand.nextInt(2)) {
                        setBlockIfAir(r, x + dx, y + dy, z + dz, Material.SPRUCE_LEAVES);
                        if (rand.nextInt(4) == 0)
                            setBlockIfAir(r, x + dx, y + dy + 1, z + dz, Material.SNOW);
                    }
        }
    }

    private void buildIceTree(LimitedRegion r, int x, int y, int z, int height, Random rand) {
        // Arbol de hielo magico
        for (int dy = 0; dy < height; dy++)
            setBlock(r, x, y + dy, z, Material.PACKED_ICE);
        for (int dy = height - 4; dy <= height + 2; dy++) {
            int radius = dy < height ? 4 : 2;
            for (int dx = -radius; dx <= radius; dx++)
                for (int dz = -radius; dz <= radius; dz++)
                    if (dx * dx + dz * dz <= radius * radius)
                        setBlockIfAir(r, x + dx, y + dy, z + dz, Material.BLUE_ICE);
        }
        setBlock(r, x, y + height + 2, z, Material.BLUE_ICE);
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
