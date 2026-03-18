package com.advancedworldgen;

import com.advancedworldgen.commands.CommandManager;
import com.advancedworldgen.generator.*;
import com.advancedworldgen.structures.StructureManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedWorldGenerator extends JavaPlugin implements Listener {

    private static AdvancedWorldGenerator instance;
    private StructureManager structureManager;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        getLogger().info("==============================================");
        getLogger().info("  AdvancedWorldGenerator v1.0.0");
        getLogger().info("  Plugin creado por soyadrianyt001");
        getLogger().info("==============================================");

        structureManager = new StructureManager(this);
        getServer().getPluginManager().registerEvents(structureManager, this);
        getServer().getPluginManager().registerEvents(this, this);

        commandManager = new CommandManager(this);

        getLogger().info("Plugin habilitado. Usa /awgcreate <nombre> para crear un mundo epico.");
    }

    @Override
    public void onDisable() {
        getLogger().info("AdvancedWorldGenerator deshabilitado.");
    }

    // Mensaje de creditos al entrar al servidor
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getServer().getScheduler().runTaskLater(this, () -> {
            sendCredits(player);
        }, 40L);
    }

    public static void sendCredits(Player player) {
        player.sendMessage("");
        player.sendMessage("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("§e§l        AdvancedWorldGenerator");
        player.sendMessage("§8          ─────────────────────");
        player.sendMessage("§7          Plugin creado por");
        player.sendMessage("§b§l          ✦ soyadrianyt001 ✦");
        player.sendMessage("§8          ─────────────────────");
        player.sendMessage("§7          Version §e1.0.0 §7| §aPaper §71.21.1");
        player.sendMessage("§a§l     ✦ Mundos Epicos Disponibles ✦");
        player.sendMessage("§e  DuneWorld §7| §cInfernoWorld §7| §bFrozenRealm");
        player.sendMessage("§3  SkyRealm §7| §2ForestLegend");
        player.sendMessage("§7     Usa §e/awg §7para ver los comandos");
        player.sendMessage("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        player.sendMessage("");
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        if (id == null) id = "";
        return switch (id.toLowerCase()) {
            case "dune" -> new DuneWorldGenerator();
            case "inferno" -> new InfernoWorldGenerator();
            case "frozen" -> new FrozenRealmGenerator();
            case "sky" -> new SkyRealmGenerator();
            case "forest" -> new ForestLegendGenerator();
            default -> new AdvancedChunkGenerator();
        };
    }

    public static AdvancedWorldGenerator getInstance() {
        return instance;
    }
}
