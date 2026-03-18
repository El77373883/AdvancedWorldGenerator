package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class JapaneseTempleBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + 2;
        int z = chunk.getZ() * 16 + 2;
        int y = world.getHighestBlockYAt(x + 7, z + 7);
        if (y < 63 || y > 200) return;

        buildGate(world, x, y, z, rand);
        buildMainTemple(world, x + 4, y, z + 6, rand);
        buildGarden(world, x, y, z, rand);
        buildPagoda(world, x + 10, y, z + 10, rand);
    }

    private void buildGate(World w, int x, int y, int z, Random rand) {
        // Torii gate — CRIMSON_LOG reemplazado por DARK_OAK_LOG (color rojo oscuro)
        for (int dy = 1; dy <= 4; dy++) {
            setBlock(w, x + 3, y + dy, z, Material.DARK_OAK_LOG);
            setBlock(w, x + 7, y + dy, z, Material.DARK_OAK_LOG);
        }
        // Barra horizontal
        for (int dx = 2; dx <= 8; dx++) {
            setBlock(w, x + dx, y + 4, z, Material.DARK_OAK_LOG);
            setBlock(w, x + dx, y + 5, z, Material.DARK_OAK_SLAB);
        }
        // Escaleras al templo
        for (int i = 0; i < 5; i++)
            setBlock(w, x + 5, y + i, z + i, Material.STONE_BRICK_STAIRS);
    }

    private void buildMainTemple(World w, int x, int y, int z, Random rand) {
        int width = 10, depth = 10, height = 6;

        // Base
        for (int dx = 0; dx < width; dx++)
            for (int dz = 0; dz < depth; dz++)
                setBlock(w, x + dx, y, z + dz, Material.STONE_BRICKS);

        // Paredes
        for (int dy = 1; dy <= height; dy++) {
            for (int dx = 0; dx < width; dx++) {
                setBlock(w, x + dx, y + dy, z, Material.DARK_OAK_PLANKS);
                setBlock(w, x + dx, y + dy, z + depth - 1, Material.DARK_OAK_PLANKS);
            }
            for (int dz = 0; dz < depth; dz++) {
                setBlock(w, x, y + dy, z + dz, Material.DARK_OAK_PLANKS);
                setBlock(w, x + width - 1, y + dy, z + dz, Material.DARK_OAK_PLANKS);
            }
        }

        // Techo curvo estilo japones — CRIMSON_SLAB reemplazado por RED_NETHER_BRICK_SLAB
        for (int dy = 0; dy <= 3; dy++) {
            int offset = dy;
            for (int dx = -offset; dx < width + offset; dx++) {
                setBlock(w, x + dx, y + height + dy + 1, z - offset, Material.RED_NETHER_BRICK_SLAB);
                setBlock(w, x + dx, y + height + dy + 1, z + depth - 1 + offset, Material.RED_NETHER_BRICK_SLAB);
            }
            for (int dz = -offset; dz < depth + offset; dz++) {
                setBlock(w, x - offset, y + height + dy + 1, z + dz, Material.RED_NETHER_BRICK_SLAB);
                setBlock(w, x + width - 1 + offset, y + height + dy + 1, z + dz, Material.RED_NETHER_BRICK_SLAB);
            }
        }

        // Interior
        setBlock(w, x + 4, y + 1, z + 4, Material.CHEST);
        Block b = w.getBlockAt(x + 4, y + 1, z + 4);
        if (b.getState() instanceof Chest chest) fillJapaneseLoot(chest.getInventory(), rand);
        setBlock(w, x + 5, y + 1, z + 5, Material.ENCHANTING_TABLE);
        setBlock(w, x + 4, y + 3, z + 4, Material.LANTERN);

        // Puerta
        setBlock(w, x + 4, y + 1, z, Material.AIR);
        setBlock(w, x + 4, y + 2, z, Material.AIR);
        setBlock(w, x + 5, y + 1, z, Material.AIR);
        setBlock(w, x + 5, y + 2, z, Material.AIR);
    }

    private void buildGarden(World w, int x, int y, int z, Random rand) {
        // Camino de piedra
        for (int i = 0; i < 10; i++)
            setBlock(w, x + 5, y + 1, z + i, Material.STONE_BRICKS);

        // Estanque
        for (int dx = 0; dx <= 2; dx++)
            for (int dz = 0; dz <= 2; dz++) {
                setBlock(w, x + dx, y, z + 10 + dz, Material.WATER);
                setBlock(w, x + dx, y + 1, z + 10 + dz, Material.WATER);
            }

        // Troncos de cerezo en el jardin
        for (int i = 0; i < 3; i++) {
            int tx = x + rand.nextInt(8);
            int tz = z + 12 + rand.nextInt(4);
            int ty = w.getHighestBlockYAt(tx, tz);
            if (Math.abs(ty - y) < 3)
                for (int dy = 0; dy < 10; dy++)
                    setBlock(w, tx, ty + dy, tz, Material.CHERRY_LOG);
        }

        // Linterna de piedra
        setBlock(w, x + 2, y + 1, z + 5, Material.STONE_BRICKS);
        setBlock(w, x + 2, y + 2, z + 5, Material.STONE_BRICK_WALL);
        setBlock(w, x + 2, y + 3, z + 5, Material.LANTERN);
    }

    private void buildPagoda(World w, int x, int y, int z, Random rand) {
        int floors = 4;
        for (int floor = 0; floor < floors; floor++) {
            int size = 6 - floor;
            int floorY = y + floor * 4;

            for (int dy = 0; dy <= 3; dy++) {
                for (int dx = 0; dx < size; dx++) {
                    boolean isWall = dx == 0 || dx == size - 1;
                    if (isWall || dy == 0 || dy == 3) {
                        setBlock(w, x + dx, floorY + dy, z, Material.DARK_OAK_PLANKS);
                        setBlock(w, x + dx, floorY + dy, z + size - 1, Material.DARK_OAK_PLANKS);
                    }
                }
                for (int dz = 0; dz < size; dz++) {
                    boolean isWall = dz == 0 || dz == size - 1;
                    if (isWall || dy == 0 || dy == 3) {
                        setBlock(w, x, floorY + dy, z + dz, Material.DARK_OAK_PLANKS);
                        setBlock(w, x + size - 1, floorY + dy, z + dz, Material.DARK_OAK_PLANKS);
                    }
                }
            }

            // Techo con RED_NETHER_BRICK_SLAB
            int offset = 1;
            for (int dx = -offset; dx < size + offset; dx++) {
                setBlock(w, x + dx, floorY + 4, z - offset, Material.RED_NETHER_BRICK_SLAB);
                setBlock(w, x + dx, floorY + 4, z + size - 1 + offset, Material.RED_NETHER_BRICK_SLAB);
            }
            for (int dz = -offset; dz < size + offset; dz++) {
                setBlock(w, x - offset, floorY + 4, z + dz, Material.RED_NETHER_BRICK_SLAB);
                setBlock(w, x + size - 1 + offset, floorY + 4, z + dz, Material.RED_NETHER_BRICK_SLAB);
            }

            setBlock(w, x + size / 2, floorY + 2, z + size / 2, Material.LANTERN);
        }
    }

    private void fillJapaneseLoot(Inventory inv, Random rand) {
        Material[] loot = {
            Material.DIAMOND, Material.EMERALD, Material.GOLDEN_APPLE,
            Material.ENCHANTED_GOLDEN_APPLE, Material.TOTEM_OF_UNDYING,
            Material.EXPERIENCE_BOTTLE, Material.NAME_TAG,
            Material.NETHERITE_SCRAP, Material.DIAMOND_SWORD
        };
        for (int i = 0; i < 5 + rand.nextInt(5); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(loot[rand.nextInt(loot.length)], 1 + rand.nextInt(3)));
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }
}
