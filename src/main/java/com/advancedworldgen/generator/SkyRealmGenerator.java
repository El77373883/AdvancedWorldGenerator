package com.advancedworldgen.generator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkyRealmGenerator extends ChunkGenerator {

    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        long seed = worldInfo.getSeed();
        NoiseUtil island = new NoiseUtil(seed);
        NoiseUtil island2 = new NoiseUtil(seed + 1000);
        NoiseUtil detail = new NoiseUtil(seed + 2000);
        NoiseUtil height2 = new NoiseUtil(seed + 3000);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int wx = chunkX * 16 + x;
                int wz = chunkZ * 16 + z;

                double islandV = island.octaveNoise(wx / 120.0, wz / 120.0, 5, 0.6, 2.2);
                double islandV2 = island2.octaveNoise(wx / 80.0, wz / 80.0, 4, 0.5, 2.0);
                double det = detail.octaveNoise(wx / 30.0, wz / 30.0, 3, 0.4, 2.0);
                double altV = height2.octaveNoise(wx / 200.0, wz / 200.0, 3, 0.5, 2.0);

                // Islas flotantes: solo existen donde el noise es alto
                double combinedV = islandV * 0.6 + islandV2 * 0.4;

                if (combinedV < 0.15) continue; // Aire — no hay isla aqui

                // Altura base de la isla flotante
                double baseAlt = 80 + altV * 60; // islas entre y=80 y y=200
                double islandThickness = 8 + combinedV * 25 + det * 5;

                int topY = (int) Math.min(319, baseAlt + islandThickness / 2);
                int botY = (int) Math.max(-64, baseAlt - islandThickness / 2);

                // Bedrock en el fondo de cada isla
                chunkData.setBlock(x, botY, z, Material.STONE);

                for (int y = botY; y <= topY; y++) {
                    if (y == topY) {
                        chunkData.setBlock(x, y, z, Material.GRASS_BLOCK);
                    } else if (y >= topY - 3) {
                        chunkData.setBlock(x, y, z, Material.DIRT);
                    } else if (y >= topY - 8) {
                        chunkData.setBlock(x, y, z, Material.STONE);
                    } else {
                        chunkData.setBlock(x, y, z, random.nextInt(10) == 0 ? Material.IRON_ORE : Material.STONE);
                    }
                }

                // Cascada colgante debajo de la isla
                if (random.nextInt(20) == 0) {
                    for (int dy = 1; dy <= 15; dy++) {
                        int waterY = botY - dy;
                        if (waterY < -60) break;
                        chunkData.setBlock(x, waterY, z, Material.WATER);
                    }
                }
            }
        }
    }

    @Override
    public void generateBedrock(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {}

    @Override
    public BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo) {
        return new SkyBiomeProvider(worldInfo.getSeed());
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        List<BlockPopulator> p = new ArrayList<>();
        p.add(new EpicTreePopulator());
        p.add(new EpicCaveDecoratorPopulator());
        return p;
    }
}
