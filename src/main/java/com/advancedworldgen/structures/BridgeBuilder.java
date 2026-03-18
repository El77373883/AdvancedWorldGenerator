package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.Random;

public class BridgeBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + 2;
        int z = chunk.getZ() * 16 + 8;
        int y = world.getHighestBlockYAt(x, z);
        if (y < 90 || y > 280) return;

        // Solo en montañas
        int endX = x + 30;
        int endY = world.getHighestBlockYAt(endX, z);
        if (Math.abs(endY - y) > 40) return;

        buildStoneBridge(world, x, y, z, endX, endY, rand);
    }

    private void buildStoneBridge(World w, int startX, int startY, int startZ, int endX, int endY, Random rand) {
        int length = endX - startX;
        int width = 4;

        for (int i = 0; i <= length; i++) {
            double progress = (double) i / length;
            // Arco parabolico
            double arcDrop = Math.sin(progress * Math.PI) * 8;
            int bridgeY = (int) (startY + (endY - startY) * progress - arcDrop);

            for (int dz = 0; dz < width; dz++) {
                // Piso del puente
                setBlock(w, startX + i, bridgeY, startZ + dz, Material.STONE_BRICKS);

                // Barandas
                setBlock(w, startX + i, bridgeY + 1, startZ, Material.STONE_BRICK_WALL);
                setBlock(w, startX + i, bridgeY + 1, startZ + width - 1, Material.STONE_BRICK_WALL);
            }

            // Pilares de soporte cada 5 bloques
            if (i % 5 == 0) {
                for (int dy = 1; dy <= 6; dy++) {
                    setBlock(w, startX + i, bridgeY - dy, startZ + 1, Material.STONE_BRICKS);
                    setBlock(w, startX + i, bridgeY - dy, startZ + width - 2, Material.STONE_BRICKS);
                }
            }

            // Antorchas cada 6 bloques
            if (i % 6 == 0) {
                setBlock(w, startX + i, bridgeY + 2, startZ, Material.LANTERN);
                setBlock(w, startX + i, bridgeY + 2, startZ + width - 1, Material.LANTERN);
            }
        }

        // Torres en los extremos
        buildBridgeTower(w, startX - 1, startY, startZ - 1, rand);
        buildBridgeTower(w, endX + 1, endY, startZ - 1, rand);
    }

    private void buildBridgeTower(World w, int x, int y, int z, Random rand) {
        int towerH = 8;
        for (int dy = 0; dy <= towerH; dy++) {
            for (int dx = 0; dx < 5; dx++) {
                for (int dz = 0; dz < 5; dz++) {
                    boolean isWall = dx == 0 || dx == 4 || dz == 0 || dz == 4;
                    if (isWall || dy == 0 || dy == towerH)
                        setBlock(w, x + dx, y + dy, z + dz, Material.STONE_BRICKS);
                }
            }
        }
        // Almenas
        for (int dx = 0; dx < 5; dx += 2) {
            setBlock(w, x + dx, y + towerH + 1, z, Material.STONE_BRICK_WALL);
            setBlock(w, x + dx, y + towerH + 1, z + 4, Material.STONE_BRICK_WALL);
        }
        setBlock(w, x + 2, y + 1, z + 2, Material.LANTERN);
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }
}
