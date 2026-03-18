package com.advancedworldgen.generator;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.Arrays;
import java.util.List;

public class InfernoBiomeProvider extends BiomeProvider {

    private final NoiseUtil noiseT;
    private final NoiseUtil noiseH;

    public InfernoBiomeProvider(long seed) {
        noiseT = new NoiseUtil(seed + 300);
        noiseH = new NoiseUtil(seed + 400);
    }

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        double t = noiseT.octaveNoise(x / 700.0, z / 700.0, 4, 0.5, 2.0);
        double h = noiseH.octaveNoise(x / 700.0, z / 700.0, 4, 0.5, 2.0);

        if (t > 0.2) {
            if (h > 0.2) return Biome.JUNGLE;
            return Biome.SAVANNA;
        } else if (t > -0.1) {
            if (h > 0.1) return Biome.BADLANDS;
            return Biome.DESERT;
        } else {
            return Biome.SAVANNA_PLATEAU;
        }
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return Arrays.asList(Biome.JUNGLE, Biome.SAVANNA, Biome.BADLANDS, Biome.DESERT, Biome.SAVANNA_PLATEAU);
    }
}
