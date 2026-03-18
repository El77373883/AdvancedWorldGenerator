package com.advancedworldgen.structures;

import org.bukkit.*;

import java.util.Random;

public class VolcanoBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + 7;
        int z = chunk.getZ() * 16 + 7;
        int baseY = world.getHighestBlockYAt(x, z);
        if (baseY < 70 || baseY > 180) return;

        int volcanoHeight = 30 + rand.nextInt(30);
        int baseRadius = 18 + rand.nextInt(10);

        for (int dy = 0; dy <= volcanoHeight; dy++) {
            double progress = (double) dy / volcanoHeight;
            int radius = (int) (baseRadius * (1.0 - progress)) + 1;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    if (dist <= radius) {
                        int bx = x + dx, by = baseY + dy, bz = z + dz;
                        boolean isShell = dist >= radius - 2;
                        boolean isInner = dist < radius - 4;
                        boolean isTop = dy > volcanoHeight - 5;

                        if (isTop) {
                            if (isShell) setBlock(world, bx, by, bz, Material.BLACKSTONE);
                            else if (!isInner) setBlock(world, bx, by, bz, Material.BASALT);
                        } else {
                            if (isShell) setBlock(world, bx, by, bz,
                                dy < 5 ? Material.STONE : dy < 15 ? Material.BLACKSTONE : Material.BASALT);
                            else if (!isInner) setBlock(world, bx, by, bz, Material.TUFF);
                            else {
                                if (rand.nextInt(3) == 0) setBlock(world, bx, by, bz, Material.MAGMA_BLOCK);
                                else setBlock(world, bx, by, bz, Material.CAVE_AIR);
                            }
                        }
                    }
                }
            }
        }

        int craterY = baseY + volcanoHeight - 3;
        int craterRadius = 5 + rand.nextInt(4);
        for (int dx = -craterRadius; dx <= craterRadius; dx++)
            for (int dz = -craterRadius; dz <= craterRadius; dz++)
                if (dx * dx + dz * dz <= craterRadius * craterRadius) {
                    setBlock(world, x + dx, craterY, z + dz, Material.LAVA);
                    setBlock(world, x + dx, craterY - 1, z + dz, Material.LAVA);
                }

        int riverX = x + baseRadius / 2;
        for (int dy = 0; dy < volcanoHeight - 5; dy++) {
            int ry = craterY - dy;
            if (ry < baseY) break;
            setBlock(world, riverX, ry, z, Material.LAVA);
            if (dy % 3 == 0) riverX += rand.nextBoolean() ? 1 : -1;
        }

        for (int dx = -baseRadius - 3; dx <= baseRadius + 3; dx++)
            for (int dz = -baseRadius - 3; dz <= baseRadius + 3; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist >= baseRadius && dist <= baseRadius + 3) {
                    int surfY = world.getHighestBlockYAt(x + dx, z + dz);
                    setBlock(world, x + dx, surfY, z + dz, Material.BLACKSTONE);
                }
            }
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }
}
