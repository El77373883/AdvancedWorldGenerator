package com.advancedworldgen.generator;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.Arrays;
import java.util.List;

public class SkyBiomeProvider extends BiomeProvider {

    private final NoiseUtil noiseT;

    public SkyBiomeProvider(long seed) {
        noiseT = new NoiseUtil(seed + 700);
    }

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        double t = noiseT.octaveNoise(x / 600.0, z / 600.0, 3, 0.5, 2.0);
        if (t > 0.3) return Biome.JUNGLE;
        if (t > 0.1) return Biome.FOREST;
        if (t > -0.1) return Biome.MEADOW;
        if (t > -0.3) return Biome.PLAINS;
        return Biome.BIRCH_FOREST;
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return Arrays.asList(Biome.JUNGLE, Biome.FOREST, Biome.MEADOW, Biome.PLAINS, Biome.BIRCH_FOREST);
    }
}
