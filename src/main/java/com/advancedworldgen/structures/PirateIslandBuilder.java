package com.advancedworldgen.structures;

import com.advancedworldgen.generator.DuneTreePopulator;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PirateIslandBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + 2;
        int z = chunk.getZ() * 16 + 2;
        int y = world.getHighestBlockYAt(x + 7, z + 7);
        if (y < 60 || y > 80) return;

        buildIslandBase(world, x, y, z, rand);
        buildPirateBase(world, x, y, z, rand);
        buildCannons(world, x, y, z, rand);
        buildPirateTower(world, x + 10, y, z + 10, rand);
        buildPirateCemetery(world, x + 14, y, z + 2, rand);
    }

    private void buildIslandBase(World w, int x, int y, int z, Random rand) {
        int radius = 12;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist <= radius) {
                    setBlock(w, x + dx + radius, y, z + dz + radius, Material.SAND);
                    setBlock(w, x + dx + radius, y - 1, z + dz + radius, Material.SAND);
                    setBlock(w, x + dx + radius, y - 2, z + dz + radius, Material.SANDSTONE);
                }
            }
        }
    }

    private void buildPirateBase(World w, int x, int y, int z, Random rand) {
        int bx = x + 4, bz = z + 4;

        for (int dx = 0; dx < 8; dx++)
            for (int dz = 0; dz < 8; dz++)
                setBlock(w, bx + dx, y, bz + dz, Material.OAK_PLANKS);

        for (int dy = 1; dy <= 4; dy++) {
            for (int dx = 0; dx < 8; dx++) {
                setBlock(w, bx + dx, y + dy, bz, Material.DARK_OAK_PLANKS);
                setBlock(w, bx + dx, y + dy, bz + 7, Material.DARK_OAK_PLANKS);
            }
            for (int dz = 0; dz < 8; dz++) {
                setBlock(w, bx, y + dy, bz + dz, Material.DARK_OAK_PLANKS);
                setBlock(w, bx + 7, y + dy, bz + dz, Material.DARK_OAK_PLANKS);
            }
        }

        for (int dx = 0; dx < 8; dx++)
            for (int dz = 0; dz < 8; dz++)
                setBlock(w, bx + dx, y + 5, bz + dz, Material.DARK_OAK_SLAB);

        // Bandera pirata
        setBlock(w, bx + 3, y + 5, bz + 3, Material.OAK_FENCE);
        setBlock(w, bx + 3, y + 6, bz + 3, Material.OAK_FENCE);
        setBlock(w, bx + 3, y + 7, bz + 3, Material.BLACK_WOOL);
        setBlock(w, bx + 4, y + 7, bz + 3, Material.WHITE_WOOL);

        // Puerta
        setBlock(w, bx + 3, y + 1, bz, Material.AIR);
        setBlock(w, bx + 3, y + 2, bz, Material.AIR);
        setBlock(w, bx + 4, y + 1, bz, Material.AIR);
        setBlock(w, bx + 4, y + 2, bz, Material.AIR);

        // Ventanas
        setBlock(w, bx + 1, y + 2, bz, Material.GLASS_PANE);
        setBlock(w, bx + 6, y + 2, bz, Material.GLASS_PANE);

        // Interior
        setBlock(w, bx + 1, y + 1, bz + 1, Material.BARREL);
        setBlock(w, bx + 6, y + 1, bz + 1, Material.BARREL);
        setBlock(w, bx + 3, y + 1, bz + 6, Material.CHEST);
        Block b = w.getBlockAt(bx + 3, y + 1, bz + 6);
        if (b.getState() instanceof Chest chest) fillPirateLoot(chest.getInventory(), rand);
        setBlock(w, bx + 4, y + 1, bz + 6, Material.CHEST);
        Block b2 = w.getBlockAt(bx + 4, y + 1, bz + 6);
        if (b2.getState() instanceof Chest chest) fillPirateLoot(chest.getInventory(), rand);
        setBlock(w, bx + 3, y + 3, bz + 3, Material.LANTERN);
    }

    private void buildCannons(World w, int x, int y, int z, Random rand) {
        int[][] cannonPos = {{x + 2, z + 2}, {x + 18, z + 2}, {x + 2, z + 18}, {x + 18, z + 18}};
        for (int[] pos : cannonPos) {
            setBlock(w, pos[0], y + 1, pos[1], Material.DISPENSER);
            setBlock(w, pos[0], y + 1, pos[1] + 1, Material.TNT);
            setBlock(w, pos[0] - 1, y + 1, pos[1], Material.IRON_BLOCK);
            setBlock(w, pos[0] + 1, y + 1, pos[1], Material.IRON_BLOCK);
        }
    }

    private void buildPirateTower(World w, int x, int y, int z, Random rand) {
        int height = 10 + rand.nextInt(5);
        for (int dy = 0; dy <= height; dy++) {
            for (int dx = 0; dx < 4; dx++) {
                for (int dz = 0; dz < 4; dz++) {
                    boolean isWall = dx == 0 || dx == 3 || dz == 0 || dz == 3;
                    if (isWall || dy == 0)
                        setBlock(w, x + dx, y + dy, z + dz, Material.DARK_OAK_PLANKS);
                }
            }
        }
        for (int dx = -1; dx <= 4; dx++)
            for (int dz = -1; dz <= 4; dz++)
                setBlock(w, x + dx, y + height + 1, z + dz, Material.DARK_OAK_SLAB);

        setBlock(w, x + 1, y + height + 2, z + 1, Material.OAK_FENCE);
        setBlock(w, x + 1, y + height + 3, z + 1, Material.OAK_FENCE);
        setBlock(w, x + 1, y + height + 4, z + 1, Material.BLACK_WOOL);
        setBlock(w, x + 2, y + height + 4, z + 1, Material.BLACK_WOOL);
        setBlock(w, x + 1, y + height + 5, z + 1, Material.BLACK_WOOL);

        setBlock(w, x + 2, y + height, z + 2, Material.CHEST);
        Block b = w.getBlockAt(x + 2, y + height, z + 2);
        if (b.getState() instanceof Chest chest) fillPirateLoot(chest.getInventory(), rand);
    }

    private void buildPirateCemetery(World w, int x, int y, int z, Random rand) {
        for (int i = 0; i < 5; i++) {
            int gx = x + (i % 3) * 3;
            int gz = z + (i / 3) * 3;
            setBlock(w, gx, y + 1, gz, Material.CHISELED_STONE_BRICKS);
            setBlock(w, gx, y + 2, gz, Material.STONE_BRICK_WALL);
            if (rand.nextInt(3) == 0)
                setBlock(w, gx + 1, y + 1, gz, Material.BONE_BLOCK);
        }
        setBlock(w, x + 4, y - 1, z + 4, Material.CHEST);
        Block b = w.getBlockAt(x + 4, y - 1, z + 4);
        if (b.getState() instanceof Chest chest) fillPirateLoot(chest.getInventory(), rand);
        setBlock(w, x + 4, y, z + 4, Material.SAND);
    }

    private void fillPirateLoot(Inventory inv, Random rand) {
        Material[] loot = {
            Material.DIAMOND, Material.GOLD_INGOT, Material.EMERALD,
            Material.ENCHANTED_GOLDEN_APPLE, Material.TOTEM_OF_UNDYING,
            Material.NAUTILUS_SHELL, Material.HEART_OF_THE_SEA,
            Material.DIAMOND_SWORD, Material.TRIDENT, Material.NAME_TAG,
            Material.EXPERIENCE_BOTTLE, Material.NETHERITE_SCRAP
        };
        for (int i = 0; i < 6 + rand.nextInt(6); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(loot[rand.nextInt(loot.length)], 1 + rand.nextInt(4)));
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }
}
