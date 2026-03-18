package com.advancedworldgen.structures;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class StructureManager implements Listener {

    private final JavaPlugin plugin;

    // Estructuras generales
    private final VillageBuilder villageBuilder;
    private final CityBuilder cityBuilder;
    private final ShipBuilder shipBuilder;
    private final TreasureBuilder treasureBuilder;
    private final DungeonBuilder dungeonBuilder;
    private final VolcanoBuilder volcanoBuilder;
    private final RuinsBuilder ruinsBuilder;
    private final FarmBuilder farmBuilder;
    private final MayaTempleBuilder mayaTempleBuilder;
    private final CastleBuilder castleBuilder;
    private final BridgeBuilder bridgeBuilder;
    private final TreeVillageBuilder treeVillageBuilder;
    private final HarborBuilder harborBuilder;

    // Estructuras DuneWorld
    private final PirateIslandBuilder pirateIslandBuilder;
    private final GreekRuinsBuilder greekRuinsBuilder;
    private final JapaneseTempleBuilder japaneseTempleBuilder;
    private final ArabMarketBuilder arabMarketBuilder;
    private final UnderwaterRuinsBuilder underwaterRuinsBuilder;

    public StructureManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.villageBuilder = new VillageBuilder();
        this.cityBuilder = new CityBuilder();
        this.shipBuilder = new ShipBuilder();
        this.treasureBuilder = new TreasureBuilder();
        this.dungeonBuilder = new DungeonBuilder();
        this.volcanoBuilder = new VolcanoBuilder();
        this.ruinsBuilder = new RuinsBuilder();
        this.farmBuilder = new FarmBuilder();
        this.mayaTempleBuilder = new MayaTempleBuilder();
        this.castleBuilder = new CastleBuilder();
        this.bridgeBuilder = new BridgeBuilder();
        this.treeVillageBuilder = new TreeVillageBuilder();
        this.harborBuilder = new HarborBuilder();
        this.pirateIslandBuilder = new PirateIslandBuilder();
        this.greekRuinsBuilder = new GreekRuinsBuilder();
        this.japaneseTempleBuilder = new JapaneseTempleBuilder();
        this.arabMarketBuilder = new ArabMarketBuilder();
        this.underwaterRuinsBuilder = new UnderwaterRuinsBuilder();
    }

    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        Chunk chunk = event.getChunk();
        World world = chunk.getWorld();
        if (world.getGenerator() == null) return;

        long seed = world.getSeed();
        int cx = chunk.getX();
        int cz = chunk.getZ();
        Random rand = new Random(seed ^ ((long) cx * 341873128712L + (long) cz * 132897987541L));

        // Detectar si es DuneWorld
        boolean isDuneWorld = world.getName().equals("DuneWorld") ||
            (world.getGenerator() != null &&
             world.getGenerator().getClass().getSimpleName().equals("DuneWorldGenerator"));

        if (isDuneWorld) {
            spawnDuneStructures(chunk, rand);
        } else {
            spawnNormalStructures(chunk, rand);
        }
    }

    // ─────────────────────────────────────────
    // Estructuras DuneWorld
    // ─────────────────────────────────────────
    private void spawnDuneStructures(Chunk chunk, Random rand) {
        int roll = rand.nextInt(100);

        if (roll < 2) {
            // Ciudad épica — muy rara
            cityBuilder.build(chunk, rand);
        } else if (roll < 4) {
            // Volcán épico
            volcanoBuilder.build(chunk, rand);
        } else if (roll < 7) {
            // Isla pirata
            pirateIslandBuilder.build(chunk, rand);
        } else if (roll < 10) {
            // Castillo en montaña
            castleBuilder.build(chunk, rand);
        } else if (roll < 13) {
            // Ruinas griegas
            greekRuinsBuilder.build(chunk, rand);
        } else if (roll < 16) {
            // Templo japonés con cerezos
            japaneseTempleBuilder.build(chunk, rand);
        } else if (roll < 19) {
            // Mercado árabe
            arabMarketBuilder.build(chunk, rand);
        } else if (roll < 22) {
            // Ruinas submarinas
            underwaterRuinsBuilder.build(chunk, rand);
        } else if (roll < 25) {
            // Puerto épico en costa
            harborBuilder.build(chunk, rand);
        } else if (roll < 28) {
            // Templo maya
            mayaTempleBuilder.build(chunk, rand);
        } else if (roll < 31) {
            // Aldea moderna — SOLO en terreno plano
            villageBuilder.build(chunk, rand);
        } else if (roll < 33) {
            // Mazmorra
            dungeonBuilder.build(chunk, rand);
        } else if (roll < 35) {
            // Puente entre montañas
            bridgeBuilder.build(chunk, rand);
        } else if (roll < 40) {
            // Tesoro enterrado
            treasureBuilder.build(chunk, rand);
        }
        // 60% del tiempo no spawna nada — mundos más limpios
    }

    // ─────────────────────────────────────────
    // Estructuras normales para otros mundos
    // ─────────────────────────────────────────
    private void spawnNormalStructures(Chunk chunk, Random rand) {
        int roll = rand.nextInt(100);

        if (roll < 2) {
            cityBuilder.build(chunk, rand);
        } else if (roll < 4) {
            volcanoBuilder.build(chunk, rand);
        } else if (roll < 7) {
            mayaTempleBuilder.build(chunk, rand);
        } else if (roll < 10) {
            castleBuilder.build(chunk, rand);
        } else if (roll < 14) {
            dungeonBuilder.build(chunk, rand);
        } else if (roll < 19) {
            villageBuilder.build(chunk, rand);
        } else if (roll < 23) {
            treeVillageBuilder.build(chunk, rand);
        } else if (roll < 27) {
            ruinsBuilder.build(chunk, rand);
        } else if (roll < 31) {
            farmBuilder.build(chunk, rand);
        } else if (roll < 34) {
            harborBuilder.build(chunk, rand);
        } else if (roll < 37) {
            bridgeBuilder.build(chunk, rand);
        } else if (roll < 40) {
            shipBuilder.build(chunk, rand);
        } else if (roll < 50) {
            treasureBuilder.build(chunk, rand);
        }
    }
}
