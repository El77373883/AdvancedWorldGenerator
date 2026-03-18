package com.advancedworldgen.generator;

import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public class EpicCaveDecoratorPopulator extends BlockPopulator {

    private static final EntityType[] CAVE_MOBS = {
        EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER,
        EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.WITCH
    };

    @Override
    public void populate(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, LimitedRegion region) {
        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;

        for (int x = baseX; x < baseX + 16; x++) {
            for (int z = baseZ; z < baseZ + 16; z++) {
                for (int y = -50; y < 40; y++) {
                    try {
                        if (!region.isInRegion(x, y, z)) continue;
                        Material m = region.getType(x, y, z);
                        if (m != Material.CAVE_AIR && m != Material.AIR) continue;

                        Material below = y > -64 ? region.getType(x, y - 1, z) : Material.STONE;
                        Material above = y < 319 ? region.getType(x, y + 1, z) : Material.STONE;

                        if (random.nextInt(60) == 0 && isSolid(below)) {
                            int h = 2 + random.nextInt(5);
                            for (int dy = 0; dy < h; dy++)
                                if (region.isInRegion(x, y + dy, z) && isAir(region.getType(x, y + dy, z)))
                                    region.setType(x, y + dy, z, Material.DRIPSTONE_BLOCK);
                        }

                        if (random.nextInt(40) == 0 && isSolid(below))
                            if (region.isInRegion(x, y - 1, z))
                                region.setType(x, y - 1, z, Material.MOSSY_COBBLESTONE);

                        if (random.nextInt(80) == 0 && isSolid(above))
                            region.setType(x, y, z, Material.GLOW_LICHEN);

                        if (random.nextInt(200) == 0 && isSolid(below)) {
                            region.setType(x, y - 1, z, Material.AMETHYST_BLOCK);
                            region.setType(x, y, z, Material.AMETHYST_CLUSTER);
                        }

                        if (random.nextInt(400) == 0 && isSolid(below))
                            region.setType(x, y, z, Material.SPAWNER);

                        if (y < -20 && random.nextInt(150) == 0 && isSolid(below))
                            region.setType(x, y, z, Material.LAVA);

                    } catch (Exception ignored) {}
                }
            }
        }
    }

    private boolean isSolid(Material m) {
        return m == Material.STONE || m == Material.DEEPSLATE ||
               m == Material.GRANITE || m == Material.DIORITE ||
               m == Material.ANDESITE || m == Material.TUFF ||
               m == Material.BLACKSTONE || m == Material.BASALT;
    }

    private boolean isAir(Material m) {
        return m == Material.AIR || m == Material.CAVE_AIR;
    }
}
