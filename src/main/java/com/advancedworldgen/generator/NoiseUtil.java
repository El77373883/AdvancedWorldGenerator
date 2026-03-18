package com.advancedworldgen.generator;

import java.util.Random;

public class NoiseUtil {

    private final int[] perm = new int[512];

    public NoiseUtil(long seed) {
        Random rand = new Random(seed);
        int[] p = new int[256];
        for (int i = 0; i < 256; i++) p[i] = i;
        for (int i = 255; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int tmp = p[i]; p[i] = p[j]; p[j] = tmp;
        }
        for (int i = 0; i < 512; i++) perm[i] = p[i & 255];
    }

    private double fade(double t) { return t * t * t * (t * (t * 6 - 15) + 10); }
    private double lerp(double a, double b, double t) { return a + t * (b - a); }
    private double grad(int hash, double x, double y) {
        int h = hash & 3;
        double u = h < 2 ? x : y;
        double v = h < 2 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    public double noise(double x, double y) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;
        x -= Math.floor(x);
        y -= Math.floor(y);
        double u = fade(x), v = fade(y);
        int a = perm[X] + Y, aa = perm[a], ab = perm[a + 1];
        int b = perm[X + 1] + Y, ba = perm[b], bb = perm[b + 1];
        return lerp(lerp(grad(perm[aa], x, y), grad(perm[ba], x - 1, y), u),
                    lerp(grad(perm[ab], x, y - 1), grad(perm[bb], x - 1, y - 1), u), v);
    }

    public double octaveNoise(double x, double y, int octaves, double persistence, double lacunarity) {
        double value = 0, amplitude = 1, frequency = 1, max = 0;
        for (int i = 0; i < octaves; i++) {
            value += noise(x * frequency, y * frequency) * amplitude;
            max += amplitude;
            amplitude *= persistence;
            frequency *= lacunarity;
        }
        return value / max;
    }
}
