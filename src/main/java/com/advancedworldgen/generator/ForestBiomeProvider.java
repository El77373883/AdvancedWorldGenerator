package com.advancedworldgen.generator;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.Arrays;
import java.util.List;

public class ForestBiomeProvider extends BiomeProvider {

    private final NoiseUtil noiseT;
    private final NoiseUtil noiseH;

    public ForestBiomeProvider(long seed) {
        noiseT = new NoiseUtil(seed + 800);
        noiseH = new NoiseUtil(seed + 900);
    }

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        double t = noiseT.octaveNoise(x / 700.0, z / 700.0, 4, 0.5, 2.0);
        double h = noiseH.octaveNoise(x / 700.0, z / 700.0, 4, 0.5, 2.0);

        if (t > 0.3) {
            if (h > 0.2) return Biome.JUNGLE;
            return Biome.BAMBOO_JUNGLE;
        } else if (t > 0.0) {
            if (h > 0.2) return Biome.DARK_FOREST;
            return Biome.FOREST;
        } else if (t > -0.2) {
            if (h > 0.1) return Biome.OLD_GROWTH_BIRCH_FOREST;
            return Biome.BIRCH_FOREST;
        } else {
            if (h > 0.2) return Biome.OLD_GROWTH_PINE_TAIGA;
            return Biome.TAIGA;
        }
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return Arrays.asList(
            Biome.JUNGLE, Biome.BAMBOO_JUNGLE, Biome.DARK_FOREST,
            Biome.FOREST, Biome.OLD_GROWTH_BIRCH_FOREST,
            Biome.BIRCH_FOREST, Biome.OLD_GROWTH_PINE_TAIGA, Biome.TAIGA
        );
    }
}
