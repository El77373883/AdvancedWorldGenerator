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

public class DuneWorldGenerator extends ChunkGenerator {

    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        long seed = worldInfo.getSeed();
        NoiseUtil base      = new NoiseUtil(seed);
        NoiseUtil mountain  = new NoiseUtil(seed + 1000);
        NoiseUtil detail    = new NoiseUtil(seed + 2000);
        NoiseUtil cave      = new NoiseUtil(seed + 3000);
        NoiseUtil cave2     = new NoiseUtil(seed + 4000);
        NoiseUtil biomeMap  = new NoiseUtil(seed + 5000);
        NoiseUtil islandMap = new NoiseUtil(seed + 6000);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int wx = chunkX * 16 + x;
                int wz = chunkZ * 16 + z;

                // Determinar tipo de zona
                double biomeV  = biomeMap.octaveNoise(wx / 600.0, wz / 600.0, 4, 0.5, 2.0);
                double islandV = islandMap.octaveNoise(wx / 300.0, wz / 300.0, 4, 0.5, 2.0);

                double baseH   = base.octaveNoise(wx / 200.0, wz / 200.0, 6, 0.55, 2.1);
                double mountH  = mountain.octaveNoise(wx / 130.0, wz / 130.0, 5, 0.68, 2.3);
                double det     = detail.octaveNoise(wx / 40.0, wz / 40.0, 3, 0.4, 2.0);

                double mf = Math.max(0, mountH);
                mf = Math.pow(mf, 1.3);

                int height;
                Material surface;
                Material subSurface;

                // ZONA OCEANO
                if (biomeV < -0.25) {
                    height = (int)(45 + baseH * 10 + det * 3);
                    height = Math.max(4, Math.min(60, height));
                    surface = Material.SAND;
                    subSurface = Material.SAND;
                }
                // ZONA DESIERTO
                else if (biomeV < -0.05) {
                    height = (int)(64 + baseH * 20 + mf * 60 + det * 8);
                    height = Math.max(63, Math.min(140, height));
                    surface = Material.SAND;
                    subSurface = Material.SANDSTONE;
                }
                // ZONA CEREZO — 40% dominante
                else if (biomeV < 0.25) {
                    height = (int)(65 + baseH * 25 + mf * 140 + det * 10);
                    height = Math.max(63, Math.min(280, height));
                    surface = height > 220 ? Material.SNOW_BLOCK : Material.GRASS_BLOCK;
                    subSurface = Material.DIRT;
                }
                // ZONA MONTANA ROCOSA
                else if (biomeV < 0.45) {
                    height = (int)(70 + baseH * 30 + mf * 180 + det * 12);
                    height = Math.max(63, Math.min(319, height));
                    surface = height > 200 ? Material.SNOW_BLOCK : height > 140 ? Material.STONE : Material.GRASS_BLOCK;
                    subSurface = height > 140 ? Material.STONE : Material.DIRT;
                }
                // ZONA PRADERA
                else {
                    height = (int)(64 + baseH * 12 + det * 5);
                    height = Math.max(63, Math.min(90, height));
                    surface = Material.GRASS_BLOCK;
                    subSurface = Material.DIRT;
                }

                // ISLAS en el oceano
                if (biomeV < -0.15 && islandV > 0.35) {
                    double islandHeight = islandV * 80;
                    height = (int)(62 + islandHeight + det * 5);
                    height = Math.min(130, height);
                    surface = height < 66 ? Material.SAND : Material.GRASS_BLOCK;
                    subSurface = height < 66 ? Material.SAND : Material.DIRT;
                }

                // Bedrock
                chunkData.setBlock(x, -64, z, Material.BEDROCK);
                for (int y = -63; y <= -60; y++)
                    if (random.nextInt(4 - (y + 63)) == 0)
                        chunkData.setBlock(x, y, z, Material.BEDROCK);

                // Capas de roca
                for (int y = -59; y < height; y++) {
                    double c1 = cave.octaveNoise(wx / 40.0, wz / 40.0 + y / 40.0, 3, 0.5, 2.0);
                    double c2 = cave2.octaveNoise(wx / 40.0 + y / 40.0, wz / 40.0, 3, 0.5, 2.0);
                    boolean isCave = y > -30 && y < height - 6 && (c1 * c1 + c2 * c2) < 0.08;

                    if (!isCave) {
                        if (y < -48) {
                            chunkData.setBlock(x, y, z, random.nextInt(15) == 0 ? Material.DEEPSLATE_DIAMOND_ORE : Material.DEEPSLATE);
                        } else if (y < -20) {
                            chunkData.setBlock(x, y, z, random.nextInt(20) == 0 ? Material.DEEPSLATE_GOLD_ORE : Material.DEEPSLATE);
                        } else if (y < 0) {
                            chunkData.setBlock(x, y, z, random.nextInt(18) == 0 ? Material.IRON_ORE : Material.STONE);
                        } else {
                            boolean isSandZone = surface == Material.SAND;
                            chunkData.setBlock(x, y, z, isSandZone ? Material.SANDSTONE : Material.STONE);
                        }
                    }
                }

                // Capas de superficie
                int layers = surface == Material.SAND ? 4 : 3;
                for (int y = height - layers; y < height; y++)
                    if (y >= -64) chunkData.setBlock(x, y, z, subSurface);

                // Bloque superficie
                if (height >= 0) chunkData.setBlock(x, height, z, surface);

                // Agua en valles y océano
                if (height < 62) {
                    for (int y = height + 1; y <= 62; y++)
                        chunkData.setBlock(x, y, z, Material.WATER);
                    if (height < 60) chunkData.setBlock(x, height, z, Material.SAND);
                }
            }
        }
    }

    @Override
    public void generateBedrock(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {}

    @Override
    public BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo) {
        return new DuneBiomeProvider(worldInfo.getSeed());
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        List<BlockPopulator> p = new ArrayList<>();
        p.add(new DuneTreePopulator());
        p.add(new EpicCaveDecoratorPopulator());
        return p;
    }
}
