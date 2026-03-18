package com.advancedworldgen.generator;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.Arrays;
import java.util.List;

public class AdvancedBiomeProvider extends BiomeProvider {

    private final NoiseUtil noiseT;
    private final NoiseUtil noiseH;

    public AdvancedBiomeProvider(long seed) {
        noiseT = new NoiseUtil(seed);
        noiseH = new NoiseUtil(seed + 12345);
    }

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        double t = noiseT.octaveNoise(x / 800.0, z / 800.0, 4, 0.5, 2.0);
        double h = noiseH.octaveNoise(x / 800.0, z / 800.0, 4, 0.5, 2.0);

        if (t > 0.4) {
            if (h > 0.3) return Biome.JUNGLE;
            if (h > 0.0) return Biome.SAVANNA;
            return Biome.DESERT;
        } else if (t > 0.1) {
            if (h > 0.3) return Biome.FOREST;
            if (h > 0.0) return Biome.PLAINS;
            return Biome.SAVANNA;
        } else if (t > -0.1) {
            if (h > 0.3) return Biome.DARK_FOREST;
            if (h > 0.0) return Biome.BIRCH_FOREST;
            return Biome.MEADOW;
        } else if (t > -0.3) {
            if (h > 0.2) return Biome.OLD_GROWTH_PINE_TAIGA;
            return Biome.TAIGA;
        } else {
            if (h > 0.2) return Biome.GROVE;
            return Biome.SNOWY_PLAINS;
        }
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return Arrays.asList(
            Biome.JUNGLE, Biome.SAVANNA, Biome.DESERT,
            Biome.FOREST, Biome.PLAINS, Biome.DARK_FOREST,
            Biome.BIRCH_FOREST, Biome.MEADOW, Biome.OLD_GROWTH_PINE_TAIGA,
            Biome.TAIGA, Biome.GROVE, Biome.SNOWY_PLAINS
        );
    }
}
