package com.advancedworldgen.structures;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class DungeonBuilder {

    public void build(Chunk chunk, Random rand) {
        World world = chunk.getWorld();
        int x = chunk.getX() * 16 + 4 + rand.nextInt(8);
        int z = chunk.getZ() * 16 + 4 + rand.nextInt(8);
        int surface = world.getHighestBlockYAt(x, z);
        int y = surface - 15 - rand.nextInt(20);
        if (y < -40 || y > 50) return;

        int width = 9 + rand.nextInt(8);
        int depth = 9 + rand.nextInt(8);
        int height = 5 + rand.nextInt(4);

        for (int dx = 0; dx < width; dx++)
            for (int dz = 0; dz < depth; dz++)
                for (int dy = 0; dy < height; dy++)
                    setBlock(world, x + dx, y + dy, z + dz, Material.CAVE_AIR);

        for (int dx = 0; dx <= width; dx++) {
            for (int dz = 0; dz <= depth; dz++) {
                for (int dy = 0; dy <= height; dy++) {
                    boolean wall = dx == 0 || dx == width || dz == 0 || dz == depth || dy == 0 || dy == height;
                    if (wall) setBlock(world, x + dx, y + dy, z + dz, Material.MOSSY_STONE_BRICKS);
                }
            }
        }

        for (int px = 2; px < width - 1; px += 3)
            for (int pz = 2; pz < depth - 1; pz += 3)
                for (int dy = 1; dy < height; dy++)
                    setBlock(world, x + px, y + dy, z + pz, Material.CHISELED_STONE_BRICKS);

        EntityType[] mobs = {EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, EntityType.CAVE_SPIDER};
        int spawnerCount = 2 + rand.nextInt(3);
        for (int s = 0; s < spawnerCount; s++) {
            int sx = x + 2 + rand.nextInt(width - 4);
            int sz = z + 2 + rand.nextInt(depth - 4);
            setBlock(world, sx, y + 1, sz, Material.SPAWNER);
            Block spawnerBlock = world.getBlockAt(sx, y + 1, sz);
            if (spawnerBlock.getState() instanceof CreatureSpawner cs) {
                cs.setSpawnedType(mobs[rand.nextInt(mobs.length)]);
                cs.setDelay(20);
                cs.update();
            }
        }

        int chestX = x + width / 2;
        int chestZ = z + depth - 2;
        setBlock(world, chestX, y + 1, chestZ, Material.CHEST);
        Block b = world.getBlockAt(chestX, y + 1, chestZ);
        if (b.getState() instanceof Chest chest) fillDungeonLoot(chest.getInventory(), rand);

        if (rand.nextInt(3) == 0)
            setBlock(world, x + width / 2, y + height - 1, z + depth / 2, Material.TNT);

        setBlock(world, x + 1, y + 2, z + depth / 2, Material.TORCH);
        setBlock(world, x + width - 1, y + 2, z + depth / 2, Material.TORCH);

        for (int dy = 0; dy < height; dy++)
            setBlock(world, x + width / 2, y + dy, z + depth / 2, Material.LADDER);
    }

    private void fillDungeonLoot(Inventory inv, Random rand) {
        Material[] loot = {Material.DIAMOND, Material.DIAMOND_SWORD,
            Material.DIAMOND_CHESTPLATE, Material.ENCHANTED_GOLDEN_APPLE,
            Material.TOTEM_OF_UNDYING, Material.NETHERITE_SCRAP,
            Material.ENDER_PEARL, Material.BLAZE_ROD, Material.NAME_TAG};
        for (int i = 0; i < 7 + rand.nextInt(6); i++)
            inv.setItem(rand.nextInt(27), new ItemStack(loot[rand.nextInt(loot.length)], 1 + rand.nextInt(3)));
    }

    private void setBlock(World w, int x, int y, int z, Material m) {
        try { w.getBlockAt(x, y, z).setType(m, false); }
        catch (Exception ignored) {}
    }
}
