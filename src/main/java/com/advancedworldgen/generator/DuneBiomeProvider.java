package com.advancedworldgen.generator;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.Arrays;
import java.util.List;

public class DuneBiomeProvider extends BiomeProvider {

    private final NoiseUtil noiseB;
    private final NoiseUtil noiseT;

    public DuneBiomeProvider(long seed) {
        noiseB = new NoiseUtil(seed + 5000);
        noiseT = new NoiseUtil(seed + 7000);
    }

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        double b = noiseB.octaveNoise(x / 600.0, z / 600.0, 4, 0.5, 2.0);
        double t = noiseT.octaveNoise(x / 400.0, z / 400.0, 3, 0.5, 2.0);

        // OCEANO
        if (b < -0.25) {
            if (t > 0.2) return Biome.WARM_OCEAN;
            if (t > -0.1) return Biome.OCEAN;
            return Biome.DEEP_OCEAN;
        }
        // DESIERTO
        if (b < -0.05) {
            if (t > 0.1) return Biome.DESERT;
            return Biome.BADLANDS;
        }
        // CEREZO — 40% dominante
        if (b < 0.25) {
            if (y > 200) return Biome.FROZEN_PEAKS;
            if (y > 150) return Biome.JAGGED_PEAKS;
            return Biome.CHERRY_GROVE;
        }
        // MONTANA ROCOSA
        if (b < 0.45) {
            if (y > 200) return Biome.FROZEN_PEAKS;
            if (y > 150) return Biome.STONY_PEAKS;
            if (y > 100) return Biome.MEADOW;
            return Biome.FOREST;
        }
        // PRADERA
        if (t > 0.1) return Biome.SUNFLOWER_PLAINS;
        return Biome.PLAINS;
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return Arrays.asList(
            Biome.WARM_OCEAN, Biome.OCEAN, Biome.DEEP_OCEAN,
            Biome.DESERT, Biome.BADLANDS,
            Biome.CHERRY_GROVE, Biome.FROZEN_PEAKS, Biome.JAGGED_PEAKS,
            Biome.STONY_PEAKS, Biome.MEADOW, Biome.FOREST,
            Biome.SUNFLOWER_PLAINS, Biome.PLAINS
        );
    }
}
