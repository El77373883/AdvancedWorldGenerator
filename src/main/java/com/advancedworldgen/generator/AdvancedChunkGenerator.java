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

public class AdvancedChunkGenerator extends ChunkGenerator {

    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        long seed = worldInfo.getSeed();
        NoiseUtil base = new NoiseUtil(seed);
        NoiseUtil mountain = new NoiseUtil(seed + 1000);
        NoiseUtil detail = new NoiseUtil(seed + 2000);
        NoiseUtil cave = new NoiseUtil(seed + 3000);
        NoiseUtil cave2 = new NoiseUtil(seed + 4000);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int wx = chunkX * 16 + x;
                int wz = chunkZ * 16 + z;

                double baseH = base.octaveNoise(wx / 200.0, wz / 200.0, 6, 0.55, 2.1);
                double mountH = mountain.octaveNoise(wx / 150.0, wz / 150.0, 5, 0.6, 2.2);
                double det = detail.octaveNoise(wx / 50.0, wz / 50.0, 3, 0.4, 2.0);

                double mf = Math.max(0, mountH);
                mf = Math.pow(mf, 1.5);

                double heightD = 64 + baseH * 30 + mf * 120 + det * 10;
                int height = (int) Math.max(4, Math.min(319, heightD));

                chunkData.setBlock(x, -64, z, Material.BEDROCK);
                for (int y = -63; y <= -60; y++)
                    if (random.nextInt(4 - (y + 63)) == 0)
                        chunkData.setBlock(x, y, z, Material.BEDROCK);

                for (int y = -59; y < height; y++) {
                    double c1 = cave.octaveNoise(wx / 40.0, wz / 40.0 + y / 40.0, 3, 0.5, 2.0);
                    double c2 = cave2.octaveNoise(wx / 40.0 + y / 40.0, wz / 40.0, 3, 0.5, 2.0);
                    boolean isCave = y > -30 && y < height - 5 && (c1 * c1 + c2 * c2) < 0.08;

                    if (!isCave) {
                        if (y < -48) chunkData.setBlock(x, y, z, random.nextInt(20) == 0 ? Material.DEEPSLATE_DIAMOND_ORE : Material.DEEPSLATE);
                        else if (y < -30) chunkData.setBlock(x, y, z, random.nextInt(30) == 0 ? Material.DEEPSLATE_GOLD_ORE : Material.DEEPSLATE);
                        else if (y < 0) chunkData.setBlock(x, y, z, random.nextInt(25) == 0 ? Material.IRON_ORE : Material.STONE);
                        else chunkData.setBlock(x, y, z, Material.STONE);
                    }
                }

                for (int y = height - 3; y < height; y++)
                    if (y >= -64) chunkData.setBlock(x, y, z, Material.DIRT);

                if (height >= 0) {
                    Material top = height > 200 ? Material.SNOW_BLOCK : height > 150 ? Material.STONE : Material.GRASS_BLOCK;
                    chunkData.setBlock(x, height, z, top);
                }

                if (height < 62) {
                    for (int y = height + 1; y <= 62; y++) chunkData.setBlock(x, y, z, Material.WATER);
                    chunkData.setBlock(x, height, z, Material.SAND);
                }
            }
        }
    }

    @Override
    public void generateBedrock(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {}

    @Override
    public BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo) {
        return new AdvancedBiomeProvider(worldInfo.getSeed());
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        List<BlockPopulator> p = new ArrayList<>();
        p.add(new EpicTreePopulator());
        p.add(new EpicCaveDecoratorPopulator());
        return p;
    }
}
