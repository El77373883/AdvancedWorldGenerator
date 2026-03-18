package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class CastleBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + 1;
        int z = chunk.getZ() * 16 + 1;
        int y = world.getHighestBlockYAt(x + 7, z + 7);
        if (y < 100 || y > 280) return;

        buildWalls(world, x, y, z, rand);
        buildTowers(world, x, y, z, rand);
        buildKeep(world, x, y, z, rand);
        buildCourtyard(world, x, y, z, rand);
        buildGate(world, x, y, z, rand);
    }

    private void buildWalls(World w, int x, int y, int z, Random rand) {
        int size = 22;
        int wallH = 8;
        Material wall = Material.STONE_BRICKS;
        Material battlement = Material.STONE_BRICK_WALL;

        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < wallH; dy++) {
                setBlock(w, x + dx, y + dy, z, wall);
                setBlock(w, x + dx, y + dy, z + size - 1, wall);
            }
            // Almenas
            if (dx % 2 == 0) {
                setBlock(w, x + dx, y + wallH, z, battlement);
                setBlock(w, x + dx, y + wallH, z + size - 1, battlement);
            }
        }
        for (int dz = 0; dz < size; dz++) {
            for (int dy = 0; dy < wallH; dy++) {
                setBlock(w, x, y + dy, z + dz, wall);
                setBlock(w, x + size - 1, y + dy, z + dz, wall);
            }
            if (dz % 2 == 0) {
                setBlock(w, x, y + wallH, z + dz, battlement);
                setBlock(w, x + size - 1, y + wallH, z + dz, battlement);
            }
        }

        // Piso del patio
        for (int dx = 1; dx < size - 1; dx++)
            for (int dz = 1; dz < size - 1; dz++)
                setBlock(w, x + dx, y, z + dz, Material.COBBLESTONE);
    }

    private void buildTowers(World w, int x, int y, int z, Random rand) {
        int size = 22;
        int towerH = 14;
        int towerR = 4;

        int[][] corners = {{x, z}, {x + size - towerR, z}, {x, z + size - towerR}, {x + size - towerR, z + size - towerR}};

        for (int[] corner : corners) {
            int tx = corner[0], tz = corner[1];
            // Torre circular
            for (int dy = 0; dy <= towerH; dy++) {
                for (int dx = 0; dx < towerR; dx++) {
                    for (int dz = 0; dz < towerR; dz++) {
                        boolean isWall = dx == 0 || dx == towerR - 1 || dz == 0 || dz == towerR - 1;
                        if (isWall || dy == 0)
                            setBlock(w, tx + dx, y + dy, tz + dz, Material.STONE_BRICKS);
                    }
                }
            }
            // Techo de la torre
            for (int dx = -1; dx <= towerR; dx++)
                for (int dz = -1; dz <= towerR; dz++)
                    setBlock(w, tx + dx, y + towerH + 1, tz + dz, Material.STONE_BRICK_SLAB);

            // Bandera
            setBlock(w, tx + towerR / 2, y + towerH + 2, tz + towerR / 2, Material.OAK_FENCE);
            setBlock(w, tx + towerR / 2, y + towerH + 3, tz + towerR / 2, Material.RED_WOOL);

            // Antorcha
            setBlock(w, tx + 1, y + towerH, tz + 1, Material.LANTERN);

            // Cofre en la torre
            if (rand.nextInt(2) == 0) {
                setBlock(w, tx + 1, y + 1, tz + 1, Material.CHEST);
                Block b = w.getBlockAt(tx + 1, y + 1, tz + 1);
                if (b.getState() instanceof Chest chest) fillCastleLoot(chest.getInventory(), rand);
            }
        }
    }

    private void buildKeep(World w, int x, int y, int z, Random rand) {
        // Torre central grande
        int kx = x + 8, kz = z + 8;
        int keepW = 8, keepH = 18;

        for (int dy = 0; dy <= keepH; dy++) {
            for (int dx = 0; dx < keepW; dx++) {
                for (int dz = 0; dz < keepW; dz++) {
                    boolean isWall = dx == 0 || dx == keepW - 1 || dz == 0 || dz == keepW - 1;
                    boolean isFloor = dy % 5 == 0;
                    if (isWall || isFloor)
                        setBlock(w, kx + dx, y + dy, kz + dz,
                            dy % 2 == 0 ? Material.STONE_BRICKS : Material.MOSSY_STONE_BRICKS);

                    // Ventanas
                    if (isWall && dy % 5 == 2 && (dx == keepW / 2 || dz == keepW / 2))
                        setBlock(w, kx + dx, y + dy, kz + dz, Material.GLASS_PANE);
                }
            }
        }

        // Techo del keep con almenas
        for (int dx = -1; dx <= keepW; dx++)
            for (int dz = -1; dz <= keepW; dz++)
                setBlock(w, kx + dx, y + keepH + 1, kz + dz, Material.STONE_BRICK_SLAB);

        for (int dx = 0; dx < keepW; dx += 2) {
            setBlock(w, kx + dx, y + keepH + 2, kz, Material.STONE_BRICK_WALL);
            setBlock(w, kx + dx, y + keepH + 2, kz + keepW - 1, Material.STONE_BRICK_WALL);
        }

        // Salon del trono interior
        setBlock(w, kx + keepW / 2, y + 1, kz + keepW - 2, Material.CHISELED_STONE_BRICKS);
        setBlock(w, kx + keepW / 2, y + 2, kz + keepW - 2, Material.LANTERN);

        // Trono
        setBlock(w, kx + 3, y + 1, kz + 6, Material.CHISELED_DEEPSLATE);
        setBlock(w, kx + 3, y + 2, kz + 6, Material.CHISELED_STONE_BRICKS);

        // Cofre del rey
        setBlock(w, kx + 2, y + 1, kz + 5, Material.CHEST);
        Block b = w.getBlockAt(kx + 2, y + 1, kz + 5);
        if (b.getState() instanceof Chest chest) fillKingLoot(chest.getInventory(), rand);
    }

    private void buildCourtyard(World w, int x, int y, int z, Random rand) {
        // Pozo en el patio
        int wx = x + 5, wz = z + 5;
        for (int dx = 0; dx <= 2; dx++)
            for (int dz = 0; dz <= 2; dz++)
                setBlock(w, wx + dx, y, wz + dz, Material.COBBLESTONE);
        setBlock(w, wx, y + 1, wz, Material.COBBLESTONE_WALL);
        setBlock(w, wx + 2, y + 1, wz, Material.COBBLESTONE_WALL);
        setBlock(w, wx, y + 1, wz + 2, Material.COBBLESTONE_WALL);
        setBlock(w, wx + 2, y + 1, wz + 2, Material.COBBLESTONE_WALL);
        setBlock(w, wx + 1, y, wz + 1, Material.WATER);
        setBlock(w, wx + 1, y + 2, wz + 1, Material.OAK_SLAB);

        // Jardin
        for (int i = 0; i < 5; i++) {
            int fx = x + 14 + rand.nextInt(5);
            int fz = z + 5 + rand.nextInt(10);
            setBlock(w, fx, y + 1, fz, rand.nextBoolean() ? Material.RED_TULIP : Material.DANDELION);
        }

        // Armeria
        setBlock(w, x + 3, y + 1, z + 15, Material.CHEST);
        Block b = w.getBlockAt(x + 3, y + 1, z + 15);
        if (b.getState() instanceof Chest chest) fillArmorLoot(chest.getInventory(), rand);
        setBlock(w, x + 4, y + 1, z + 15, Material.ANVIL);
        setBlock(w, x + 5, y + 1, z + 15, Material.GRINDSTONE);
    }

    private void buildGate(World w, int x, int y, int z, Random rand) {
        // Puerta principal en el centro del muro sur
        int gx = x + 10;
        for (int dy = 0; dy < 4; dy++) {
            setBlock(w, gx, y + dy, z, Material.AIR);
            setBlock(w, gx + 1, y + dy, z, Material.AIR);
        }
        // Arco de la puerta
        setBlock(w, gx - 1, y + 4, z, Material.STONE_BRICK_STAIRS);
        setBlock(w, gx, y + 4, z, Material.STONE_BRICKS);
        setBlock(w, gx + 1, y + 4, z, Material.STONE_BRICKS);
        setBlock(w, gx + 2, y + 4, z, Material.STONE_BRICK_STAIRS);

        // Puente levadizo
        for (int dx = 0; dx <= 1; dx++)
            for (int dz = -3; dz < 0; dz++)
                setBlock(w, gx + dx, y, z + dz, Material.OAK_PLANKS);

        // Cadenas del puente
        setBlock(w, gx, y + 3, z - 1, Material.CHAIN);
        setBlock(w, gx + 1, y + 3, z - 1, Material.CHAIN);
    }

    private void fillCastleLoot(Inventory inv, Random rand) {
        Material[] loot = {Material.IRON_SWORD, Material.IRON_CHESTPLATE,
            Material.ARROW, Material.BREAD, Material.GOLD_INGOT, Material.IRON_INGOT};
        for (int i = 0; i < 4 + rand.nextInt(4); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(loot[rand.nextInt(loot.length)], 1 + rand.nextInt(8)));
    }

    private void fillKingLoot(Inventory inv, Random rand) {
        Material[] loot = {Material.DIAMOND, Material.DIAMOND_SWORD,
            Material.DIAMOND_CHESTPLATE, Material.ENCHANTED_GOLDEN_APPLE,
            Material.TOTEM_OF_UNDYING, Material.NETHERITE_SCRAP};
        for (int i = 0; i < 6 + rand.nextInt(5); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(loot[rand.nextInt(loot.length)], 1 + rand.nextInt(3)));
    }

    private void fillArmorLoot(Inventory inv, Random rand) {
        Material[] loot = {Material.IRON_HELMET, Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.SHIELD,
            Material.IRON_SWORD, Material.BOW, Material.ARROW};
        for (int i = 0; i < 5 + rand.nextInt(4); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(loot[rand.nextInt(loot.length)], 1 + rand.nextInt(16)));
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }
}
