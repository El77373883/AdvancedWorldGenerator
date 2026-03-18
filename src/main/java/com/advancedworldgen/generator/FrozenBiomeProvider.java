package com.advancedworldgen.generator;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.Arrays;
import java.util.List;

public class FrozenBiomeProvider extends BiomeProvider {

    private final NoiseUtil noiseT;
    private final NoiseUtil noiseH;

    public FrozenBiomeProvider(long seed) {
        noiseT = new NoiseUtil(seed + 500);
        noiseH = new NoiseUtil(seed + 600);
    }

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        double t = noiseT.octaveNoise(x / 700.0, z / 700.0, 4, 0.5, 2.0);
        double h = noiseH.octaveNoise(x / 700.0, z / 700.0, 4, 0.5, 2.0);

        if (t > 0.2) {
            if (h > 0.2) return Biome.TAIGA;
            return Biome.SNOWY_TAIGA;
        } else if (t > -0.1) {
            if (h > 0.1) return Biome.OLD_GROWTH_PINE_TAIGA;
            return Biome.SNOWY_PLAINS;
        } else {
            if (h > 0.2) return Biome.GROVE;
            return Biome.FROZEN_PEAKS;
        }
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return Arrays.asList(Biome.TAIGA, Biome.SNOWY_TAIGA, Biome.OLD_GROWTH_PINE_TAIGA, Biome.SNOWY_PLAINS, Biome.GROVE, Biome.FROZEN_PEAKS);
    }
}
