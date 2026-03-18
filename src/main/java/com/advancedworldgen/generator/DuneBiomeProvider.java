package com.advancedworldgen.generator;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.Arrays;
import java.util.List;

public class DuneBiomeProvider extends BiomeProvider {

    private final NoiseUtil noiseT;
    private final NoiseUtil noiseH;

    public DuneBiomeProvider(long seed) {
        noiseT = new NoiseUtil(seed + 100);
        noiseH = new NoiseUtil(seed + 200);
    }

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        double t = noiseT.octaveNoise(x / 700.0, z / 700.0, 4, 0.5, 2.0);
        double h = noiseH.octaveNoise(x / 700.0, z / 700.0, 4, 0.5, 2.0);

        // 60% desierto dominante
        if (t > 0.1) {
            if (h > 0.3) return Biome.JUNGLE;
            if (h > 0.1) return Biome.SAVANNA_PLATEAU;
            return Biome.DESERT;
        } else if (t > -0.1) {
            if (h > 0.2) return Biome.FOREST;
            return Biome.PLAINS;
        } else {
            if (h > 0.2) return Biome.SAVANNA;
            return Biome.BADLANDS;
        }
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return Arrays.asList(
            Biome.DESERT, Biome.JUNGLE, Biome.SAVANNA_PLATEAU,
            Biome.SAVANNA, Biome.BADLANDS, Biome.FOREST, Biome.PLAINS
        );
    }
}
