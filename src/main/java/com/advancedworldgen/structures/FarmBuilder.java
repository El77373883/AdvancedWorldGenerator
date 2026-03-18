package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.Ageable;

import java.util.Random;

public class FarmBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + 2;
        int z = chunk.getZ() * 16 + 2;
        int y = world.getHighestBlockYAt(x + 6, z + 6);
        if (y < 63 || y > 130) return;

        buildFarmField(world, x, y, z, rand);
        buildFarmerHouse(world, x + 10, y, z, rand);
        buildBarn(world, x, y, z + 10, rand);
        buildWell(world, x + 10, y, z + 10, rand);
    }

    private void buildFarmField(World w, int x, int y, int z, Random rand) {
        Material[] crops = {Material.WHEAT, Material.CARROTS, Material.POTATOES,
            Material.BEETROOTS, Material.MELON_STEM, Material.PUMPKIN_STEM};

        for (int dx = 0; dx < 8; dx++) {
            for (int dz = 0; dz < 8; dz++) {
                setBlock(w, x + dx, y, z + dz, Material.FARMLAND);
                setBlock(w, x + dx, y - 1, z + dz, Material.DIRT);
                Material crop = crops[rand.nextInt(crops.length)];
                Block cropBlock = w.getBlockAt(x + dx, y + 1, z + dz);
                cropBlock.setType(crop, false);
                if (cropBlock.getBlockData() instanceof Ageable ageable) {
                    ageable.setAge(ageable.getMaximumAge());
                    cropBlock.setBlockData(ageable, false);
                }
            }
        }

        setBlock(w, x - 1, y, z + 4, Material.WATER);

        for (int dx = -1; dx <= 8; dx++) {
            setBlock(w, x + dx, y + 1, z - 1, Material.OAK_FENCE);
            setBlock(w, x + dx, y + 1, z + 8, Material.OAK_FENCE);
        }
        for (int dz2 = -1; dz2 <= 8; dz2++) {
            setBlock(w, x - 1, y + 1, z + dz2, Material.OAK_FENCE);
            setBlock(w, x + 8, y + 1, z + dz2, Material.OAK_FENCE);
        }
        setBlock(w, x + 3, y + 1, z - 1, Material.OAK_FENCE_GATE);
    }

    private void buildFarmerHouse(World w, int x, int y, int z, Random rand) {
        for (int dx = 0; dx < 6; dx++)
            for (int dz = 0; dz < 6; dz++)
                setBlock(w, x + dx, y, z + dz, Material.COBBLESTONE);

        for (int dy = 1; dy <= 4; dy++) {
            for (int dx = 0; dx < 6; dx++) {
                setBlock(w, x + dx, y + dy, z, Material.OAK_PLANKS);
                setBlock(w, x + dx, y + dy, z + 5, Material.OAK_PLANKS);
            }
            for (int dz = 0; dz < 6; dz++) {
                setBlock(w, x, y + dy, z + dz, Material.OAK_PLANKS);
                setBlock(w, x + 5, y + dy, z + dz, Material.OAK_PLANKS);
            }
        }

        for (int dx = 0; dx < 6; dx++)
            for (int dz = 0; dz < 6; dz++)
                setBlock(w, x + dx, y + 5, z + dz, Material.OAK_SLAB);

        setBlock(w, x + 2, y + 1, z, Material.AIR);
        setBlock(w, x + 2, y + 2, z, Material.AIR);
        setBlock(w, x + 1, y + 2, z, Material.GLASS_PANE);
        setBlock(w, x + 4, y + 2, z, Material.GLASS_PANE);

        setBlock(w, x + 1, y + 1, z + 1, Material.CRAFTING_TABLE);
        setBlock(w, x + 4, y + 1, z + 1, Material.FURNACE);
        setBlock(w, x + 2, y + 1, z + 4, Material.RED_BED);
        setBlock(w, x + 4, y + 1, z + 4, Material.CHEST);
        setBlock(w, x + 2, y + 3, z + 2, Material.LANTERN);
    }

    private void buildBarn(World w, int x, int y, int z, Random rand) {
        for (int dx = 0; dx < 7; dx++) {
            for (int dy = 1; dy <= 5; dy++) {
                setBlock(w, x + dx, y + dy, z, dy == 5 ? Material.OAK_SLAB : Material.OAK_LOG);
                setBlock(w, x + dx, y + dy, z + 6, dy == 5 ? Material.OAK_SLAB : Material.OAK_LOG);
            }
        }
        for (int dz = 0; dz <= 6; dz++)
            for (int dy = 1; dy <= 4; dy++) {
                setBlock(w, x, y + dy, z + dz, Material.OAK_LOG);
                setBlock(w, x + 6, y + dy, z + dz, Material.OAK_LOG);
            }

        for (int dx = 1; dx < 6; dx++)
            for (int dz2 = 1; dz2 < 6; dz2++)
                setBlock(w, x + dx, y, z + dz2, Material.HAY_BLOCK);

        for (int dz2 = 1; dz2 <= 5; dz2++)
            setBlock(w, x + 3, y + 1, z + dz2, Material.OAK_FENCE);
        setBlock(w, x + 3, y + 1, z + 3, Material.OAK_FENCE_GATE);
    }

    private void buildWell(World w, int x, int y, int z, Random rand) {
        for (int dx = 0; dx <= 2; dx++)
            for (int dz = 0; dz <= 2; dz++)
                setBlock(w, x + dx, y, z + dz, Material.COBBLESTONE);
        setBlock(w, x, y + 1, z, Material.COBBLESTONE_WALL);
        setBlock(w, x + 2, y + 1, z, Material.COBBLESTONE_WALL);
        setBlock(w, x, y + 1, z + 2, Material.COBBLESTONE_WALL);
        setBlock(w, x + 2, y + 1, z + 2, Material.COBBLESTONE_WALL);
        setBlock(w, x + 1, y, z + 1, Material.WATER);
        setBlock(w, x + 1, y + 1, z + 1, Material.WATER);
        setBlock(w, x, y + 3, z + 1, Material.OAK_FENCE);
        setBlock(w, x + 2, y + 3, z + 1, Material.OAK_FENCE);
        setBlock(w, x + 1, y + 4, z + 1, Material.OAK_SLAB);
        setBlock(w, x + 1, y + 3, z + 1, Material.LANTERN);
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }
}
