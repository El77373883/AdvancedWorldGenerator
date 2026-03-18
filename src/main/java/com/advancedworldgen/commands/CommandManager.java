package com.advancedworldgen.commands;

import com.advancedworldgen.AdvancedWorldGenerator;
import com.advancedworldgen.util.TitleUtil;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final AdvancedWorldGenerator plugin;

    private static final Map<String, String> WORLD_GENERATORS = new LinkedHashMap<>();
    static {
        WORLD_GENERATORS.put("DuneWorld",    "dune");
        WORLD_GENERATORS.put("InfernoWorld", "inferno");
        WORLD_GENERATORS.put("FrozenRealm",  "frozen");
        WORLD_GENERATORS.put("SkyRealm",     "sky");
        WORLD_GENERATORS.put("ForestLegend", "forest");
    }

    private static final Map<String, String> WORLD_DESCRIPTIONS = new LinkedHashMap<>();
    static {
        WORLD_DESCRIPTIONS.put("DuneWorld",    "§e🏜  Desierto epico con montanas y oasis");
        WORLD_DESCRIPTIONS.put("InfernoWorld", "§c🌋 Volcanico con lava y obsidiana");
        WORLD_DESCRIPTIONS.put("FrozenRealm",  "§b❄  Tundra helada con glaciares");
        WORLD_DESCRIPTIONS.put("SkyRealm",     "§3🌊 Islas flotantes con cascadas");
        WORLD_DESCRIPTIONS.put("ForestLegend", "§2🌲 Bosque Amazonas gigante");
    }

    public CommandManager(AdvancedWorldGenerator plugin) {
        this.plugin = plugin;
        register("awg");
        register("awgcreate");
        register("awgdelete");
        register("awgtp");
        register("awgconfig");
        register("awginfo");
        register("awglist");
        register("awgworlds");
    }

    private void register(String cmd) {
        PluginCommand pc = plugin.getCommand(cmd);
        if (pc != null) {
            pc.setExecutor(this);
            pc.setTabCompleter(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp() && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(prefix() + "§cNo tienes permiso.");
            return true;
        }
        switch (cmd.getName().toLowerCase()) {
            case "awg"       -> handleHelp(sender);
            case "awgcreate" -> handleCreate(sender, args);
            case "awgdelete" -> handleDelete(sender, args);
            case "awgtp"     -> handleTeleport(sender, args);
            case "awgconfig" -> handleConfig(sender, args);
            case "awginfo"   -> handleInfo(sender);
            case "awglist"   -> handleList(sender);
            case "awgworlds" -> handleWorlds(sender);
        }
        return true;
    }

    // ─────────────────────────────────────────
    // /awg — Ayuda + creditos
    // ─────────────────────────────────────────
    private void handleHelp(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sender.sendMessage("§e§l      AdvancedWorldGenerator");
        sender.sendMessage("§8        ──────────────────────");
        sender.sendMessage("§7        Plugin creado por");
        sender.sendMessage("§b§l        ✦ soyadrianyt001 ✦");
        sender.sendMessage("§8        ──────────────────────");
        sender.sendMessage("§7        Version §e1.0.0 §7| §aPaper 1.21.1");
        sender.sendMessage("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sender.sendMessage("");
        sender.sendMessage("§6§lComandos:");
        sender.sendMessage("§e /awgcreate §7<nombre> §f- Crear mundo epico");
        sender.sendMessage("§e /awgdelete §7<nombre> §f- Borrar un mundo");
        sender.sendMessage("§e /awgtp §7<estructura|mundo> §f- Teletransporte");
        sender.sendMessage("§e /awgconfig §7<opcion> <valor> §f- Configurar");
        sender.sendMessage("§e /awginfo §f- Info del mundo actual");
        sender.sendMessage("§e /awglist §f- Listar todos los mundos");
        sender.sendMessage("§e /awgworlds §f- Ver los 5 mundos epicos");
        sender.sendMessage("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sender.sendMessage("");
    }

    // ─────────────────────────────────────────
    // /awgcreate <nombre>
    // ─────────────────────────────────────────
    private void handleCreate(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(prefix() + "§cUso: /awgcreate §e<nombre>");
            sender.sendMessage(prefix() + "§7Mundos epicos: §eDuneWorld InfernoWorld FrozenRealm SkyRealm ForestLegend");
            return;
        }

        String name = args[0];

        if (Bukkit.getWorld(name) != null) {
            sender.sendMessage(prefix() + "§cYa existe un mundo llamado §e" + name);
            return;
        }

        String genId = WORLD_GENERATORS.getOrDefault(name, "default");
        String desc  = WORLD_DESCRIPTIONS.getOrDefault(name, "§7Mundo custom");

        sender.sendMessage("");
        sender.sendMessage("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sender.sendMessage("§a§l  Creando Mundo Epico...");
        sender.sendMessage("§7  Nombre: §e" + name);
        sender.sendMessage("§7  Tipo:   " + desc);
        sender.sendMessage("§7  Generador: §e" + genId);
        sender.sendMessage("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sender.sendMessage("");

        if (sender instanceof Player player) {
            TitleUtil.send(player,
                "§6§lCreando Mundo",
                "§e" + name + " §7se esta generando...",
                10, 80, 20);
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            WorldCreator creator = new WorldCreator(name);
            creator.generator(plugin.getDefaultWorldGenerator(name, genId));
            creator.environment(World.Environment.NORMAL);
            creator.generateStructures(false);

            World world = Bukkit.getScheduler().callSyncMethod(plugin, creator::createWorld).join();

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (world != null) {
                    world.setDifficulty(Difficulty.NORMAL);
                    world.setSpawnFlags(true, true);
                    world.setTime(6000);

                    sender.sendMessage("");
                    sender.sendMessage("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                    sender.sendMessage("§a§l  ✔ Mundo Creado Exitosamente!");
                    sender.sendMessage("§7  Nombre: §e" + name);
                    sender.sendMessage("§7  Tipo:   " + desc);
                    sender.sendMessage("§7  Creado por: §bsoyadrianyt001");
                    sender.sendMessage("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                    sender.sendMessage("");

                    if (sender instanceof Player player) {
                        player.teleport(world.getSpawnLocation());
                        TitleUtil.send(player,
                            "§a§l¡Bienvenido!",
                            "§e" + name + " §7| §b✦ soyadrianyt001",
                            10, 100, 20);
                        player.playSound(player.getLocation(),
                            Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
                        AdvancedWorldGenerator.sendCredits(player);
                    }
                } else {
                    sender.sendMessage(prefix() + "§cError al crear el mundo.");
                }
            });
        });
    }

    // ─────────────────────────────────────────
    // /awgdelete <nombre>
    // ─────────────────────────────────────────
    private void handleDelete(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(prefix() + "§cUso: /awgdelete §e<nombre>");
            return;
        }
        String name = args[0];

        if (!Bukkit.getWorlds().isEmpty() && Bukkit.getWorlds().get(0).getName().equals(name)) {
            sender.sendMessage(prefix() + "§cNo puedes borrar el mundo principal.");
            return;
        }

        World world = Bukkit.getWorld(name);
        if (world != null) {
            World fallback = Bukkit.getWorlds().get(0);
            for (Player p : world.getPlayers()) {
                p.teleport(fallback.getSpawnLocation());
                p.sendMessage(prefix() + "§cFuiste expulsado porque §e" + name + " §cfue eliminado.");
                TitleUtil.send(p, "§c§lMundo Eliminado", "§eFuiste enviado al lobby", 10, 60, 20);
            }
            Bukkit.unloadWorld(world, false);
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            File worldFolder = new File(Bukkit.getWorldContainer(), name);
            if (worldFolder.exists()) {
                deleteFolder(worldFolder);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    sender.sendMessage(prefix() + "§c✔ Mundo §e" + name + " §celiminado permanentemente.");
                    if (sender instanceof Player p)
                        TitleUtil.send(p, "§c§lEliminado", "§e" + name, 10, 60, 20);
                });
            } else {
                Bukkit.getScheduler().runTask(plugin, () ->
                    sender.sendMessage(prefix() + "§cNo se encontro la carpeta del mundo §e" + name));
            }
        });
    }

    // ─────────────────────────────────────────
    // /awgtp <estructura|mundo>
    // ─────────────────────────────────────────
    private void handleTeleport(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + "§cSolo jugadores pueden usar este comando.");
            return;
        }
        if (args.length < 1) {
            player.sendMessage(prefix() + "§cUso: /awgtp §e<estructura|mundo>");
            player.sendMessage("§7Estructuras: §ealdea ciudad dungeon volcan ruinas barco tesoro granja templo castillo puente aldeaarbol puerto");
            player.sendMessage("§7Mundos: §eDuneWorld InfernoWorld FrozenRealm SkyRealm ForestLegend");
            return;
        }

        String target = args[0];

        if (WORLD_GENERATORS.containsKey(target)) {
            World w = Bukkit.getWorld(target);
            if (w == null) {
                player.sendMessage(prefix() + "§cEse mundo no esta creado. Usa §e/awgcreate " + target);
                return;
            }
            player.teleport(w.getSpawnLocation());
            player.sendMessage(prefix() + "§a✔ Teletransportado a §e" + target);
            TitleUtil.send(player, "§6§l" + target,
                WORLD_DESCRIPTIONS.getOrDefault(target, ""), 10, 80, 20);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            return;
        }

        player.sendMessage(prefix() + "§7Buscando §e" + target + "§7 cerca de ti...");
        TitleUtil.send(player, "§6Buscando...", "§e" + target, 10, 40, 10);

        World world = player.getWorld();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Location found = findStructure(world, player.getLocation(), target.toLowerCase());
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (found != null) {
                    player.teleport(found);
                    player.sendMessage(prefix() + "§a✔ Teletransportado a §e" + target +
                        " §a(" + found.getBlockX() + ", " + found.getBlockY() + ", " + found.getBlockZ() + ")");
                    TitleUtil.send(player,
                        "§a§l¡Encontrado!",
                        "§e" + capitalize(target) + " §7en §6" + found.getBlockX() + ", " + found.getBlockZ(),
                        10, 80, 20);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                } else {
                    player.sendMessage(prefix() + "§cNo se encontro §e" + target + " §ccerca. Explora mas.");
                    TitleUtil.send(player, "§c§lNo Encontrado", "§7Intenta explorar mas lejos", 10, 60, 20);
                }
            });
        });
    }

    private Location findStructure(World world, Location origin, String type) {
        int searchRadius = 2000;
        int step = 50;
        int ox = origin.getBlockX();
        int oz = origin.getBlockZ();

        Material marker;
        if (type.equals("aldea") || type.equals("granja")) marker = Material.HAY_BLOCK;
        else if (type.equals("ciudad"))     marker = Material.CHISELED_STONE_BRICKS;
        else if (type.equals("dungeon"))    marker = Material.MOSSY_STONE_BRICKS;
        else if (type.equals("volcan"))     marker = Material.MAGMA_BLOCK;
        else if (type.equals("ruinas"))     marker = Material.CHISELED_DEEPSLATE;
        else if (type.equals("barco"))      marker = Material.BARREL;
        else if (type.equals("tesoro"))     marker = Material.CHEST;
        else if (type.equals("templo"))     marker = Material.MOSSY_STONE_BRICKS;
        else if (type.equals("castillo"))   marker = Material.STONE_BRICK_WALL;
        else if (type.equals("puente"))     marker = Material.STONE_BRICKS;
        else if (type.equals("aldeaarbol")) marker = Material.JUNGLE_LOG;
        else if (type.equals("puerto"))     marker = Material.SEA_LANTERN;
        else                                marker = Material.STONE_BRICKS;

        for (int r = step; r <= searchRadius; r += step) {
            for (int angle = 0; angle < 360; angle += 15) {
                int cx = ox + (int)(Math.cos(Math.toRadians(angle)) * r);
                int cz = oz + (int)(Math.sin(Math.toRadians(angle)) * r);
                int cy = world.getHighestBlockYAt(cx, cz);
                for (int dy = -20; dy <= 20; dy++) {
                    try {
                        if (world.getBlockAt(cx, cy + dy, cz).getType() == marker)
                            return new Location(world, cx, cy + dy + 2, cz);
                    } catch (Exception ignored) {}
                }
            }
        }
        return null;
    }

    // ─────────────────────────────────────────
    // /awgconfig <opcion> <valor>
    // ─────────────────────────────────────────
    private void handleConfig(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("");
            sender.sendMessage("§6§l▬▬▬▬ Configuracion AWG ▬▬▬▬");
            sender.sendMessage("§e mountain-height  §7(50-320)   §f- Altura montanas");
            sender.sendMessage("§e tree-frequency   §7(1-10)     §f- Frecuencia arboles");
            sender.sendMessage("§e cave-density     §7(0.01-0.2) §f- Densidad cuevas");
            sender.sendMessage("§e volcano-chance   §7(1-20)     §f- % Volcanes");
            sender.sendMessage("§e dungeon-chance   §7(1-30)     §f- % Mazmorras");
            sender.sendMessage("§e village-chance   §7(1-40)     §f- % Aldeas");
            sender.sendMessage("§e city-chance      §7(1-10)     §f- % Ciudades");
            sender.sendMessage("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            sender.sendMessage("");
            return;
        }

        String option = args[0].toLowerCase();
        List<String> valid = List.of("mountain-height", "tree-frequency", "cave-density",
            "volcano-chance", "dungeon-chance", "village-chance", "city-chance", "structure-frequency");

        if (!valid.contains(option)) {
            sender.sendMessage(prefix() + "§cOpcion desconocida: §e" + option);
            return;
        }

        try {
            double val = Double.parseDouble(args[1]);
            plugin.getConfig().set("generator." + option, val);
            plugin.saveConfig();
            sender.sendMessage(prefix() + "§a✔ §e" + option + " §a= §6" + args[1]);
            sender.sendMessage(prefix() + "§7Los nuevos mundos usaran esta configuracion.");
            if (sender instanceof Player p)
                TitleUtil.send(p, "§a§lConfigurado", "§e" + option + " = §6" + args[1], 10, 60, 20);
        } catch (NumberFormatException e) {
            sender.sendMessage(prefix() + "§cValor invalido: §e" + args[1]);
        }
    }

    // ─────────────────────────────────────────
    // /awginfo
    // ─────────────────────────────────────────
    private void handleInfo(CommandSender sender) {
        World world = sender instanceof Player p ? p.getWorld() : Bukkit.getWorlds().get(0);
        if (world == null) return;

        long time = world.getTime();
        String timeStr;
        if (time < 6000)       timeStr = "☀ Manana";
        else if (time < 12000) timeStr = "☀ Tarde";
        else if (time < 18000) timeStr = "🌙 Noche";
        else                   timeStr = "🌙 Madrugada";

        String weather = world.hasStorm() ? "§9⛈ Tormenta" : "§a☀ Despejado";
        boolean isEpic = world.getGenerator() != null;
        String genType = WORLD_GENERATORS.getOrDefault(world.getName(), "custom");

        sender.sendMessage("");
        sender.sendMessage("§6§l▬▬▬▬▬ Info: §e" + world.getName() + " §6§l▬▬▬▬▬");
        sender.sendMessage("§7Generador epico: " + (isEpic ? "§a✔ Activo (§e" + genType + "§a)" : "§c✘ No activo"));
        sender.sendMessage("§7Seed: §e" + world.getSeed());
        sender.sendMessage("§7Jugadores: §e" + world.getPlayers().size());
        sender.sendMessage("§7Chunks cargados: §e" + world.getLoadedChunks().length);
        sender.sendMessage("§7Hora: §e" + timeStr + " §7(" + time + ")");
        sender.sendMessage("§7Dificultad: §e" + world.getDifficulty().name());
        sender.sendMessage("§7Clima: " + weather);
        sender.sendMessage("§7Spawn: §e" +
            world.getSpawnLocation().getBlockX() + ", " +
            world.getSpawnLocation().getBlockY() + ", " +
            world.getSpawnLocation().getBlockZ());
        sender.sendMessage("");
        sender.sendMessage("§6Configuracion actual:");
        sender.sendMessage("§7 Altura montanas: §e" + plugin.getConfig().get("generator.mountain-height", 120));
        sender.sendMessage("§7 Volcanes:  §e" + plugin.getConfig().get("generator.volcano-chance", 5) + "%");
        sender.sendMessage("§7 Mazmorras: §e" + plugin.getConfig().get("generator.dungeon-chance", 10) + "%");
        sender.sendMessage("§7 Aldeas:    §e" + plugin.getConfig().get("generator.village-chance", 16) + "%");
        sender.sendMessage("§7 Ciudades:  §e" + plugin.getConfig().get("generator.city-chance", 2) + "%");
        sender.sendMessage("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sender.sendMessage("");
    }

    // ─────────────────────────────────────────
    // /awglist
    // ─────────────────────────────────────────
    private void handleList(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("§6§l▬▬▬▬▬ Mundos en el Servidor ▬▬▬▬▬");
        for (World w : Bukkit.getWorlds()) {
            boolean isEpic = w.getGenerator() != null;
            String status = isEpic ? "§a[EPICO]" : "§7[Normal]";
            String desc = WORLD_DESCRIPTIONS.getOrDefault(w.getName(), "");
            sender.sendMessage("§f" + w.getName() + " " + status + " §e" + w.getPlayers().size() + " jugadores");
            if (!desc.isEmpty()) sender.sendMessage("  " + desc);
        }
        sender.sendMessage("");
        sender.sendMessage("§7Usa §e/awgcreate <nombre> §7para crear un mundo epico");
        sender.sendMessage("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sender.sendMessage("");
    }

    // ─────────────────────────────────────────
    // /awgworlds
    // ─────────────────────────────────────────
    private void handleWorlds(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sender.sendMessage("§e§l      Los 5 Mundos Epicos");
        sender.sendMessage("§8        ──────────────────────");
        sender.sendMessage("§7        Plugin por §b✦ soyadrianyt001 ✦");
        sender.sendMessage("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sender.sendMessage("");

        for (Map.Entry<String, String> entry : WORLD_DESCRIPTIONS.entrySet()) {
            String name = entry.getKey();
            String desc = entry.getValue();
            World w = Bukkit.getWorld(name);
            String status = w != null
                ? "§a[Activo §e" + w.getPlayers().size() + " jugadores§a]"
                : "§c[No creado]";
            String tip = w == null ? " §7- /awgcreate " + name : "";
            sender.sendMessage(desc);
            sender.sendMessage("  §7Nombre: §e" + name + " " + status + tip);
            sender.sendMessage("");
        }

        sender.sendMessage("§6§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        sender.sendMessage("");
    }

    // ─────────────────────────────────────────
    // Tab Completer — CORREGIDO
    // ─────────────────────────────────────────
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) return List.of();

        String cmdName = cmd.getName().toLowerCase();

        if (cmdName.equals("awgcreate") && args.length == 1) {
            return new ArrayList<>(WORLD_GENERATORS.keySet()).stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .toList();
        }

        if (cmdName.equals("awgdelete") && args.length == 1) {
            return Bukkit.getWorlds().stream()
                .map(World::getName)
                .filter(s -> s.startsWith(args[0]))
                .toList();
        }

        if (cmdName.equals("awgtp") && args.length == 1) {
            List<String> options = new ArrayList<>(List.of(
                "aldea", "ciudad", "dungeon", "volcan", "ruinas",
                "barco", "tesoro", "granja", "templo", "castillo",
                "puente", "aldeaarbol", "puerto"
            ));
            options.addAll(WORLD_GENERATORS.keySet());
            return options.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .toList();
        }

        if (cmdName.equals("awgconfig")) {
            if (args.length == 1) {
                return List.of(
                    "mountain-height", "tree-frequency", "cave-density",
                    "volcano-chance", "dungeon-chance", "village-chance",
                    "city-chance", "structure-frequency"
                ).stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
            }
            if (args.length == 2) {
                String opt = args[0].toLowerCase();
                if (opt.equals("mountain-height"))  return List.of("80", "120", "150", "200", "250");
                if (opt.equals("tree-frequency"))   return List.of("1", "2", "4", "6", "8", "10");
                if (opt.equals("cave-density"))     return List.of("0.04", "0.06", "0.08", "0.12");
                if (opt.equals("volcano-chance"))   return List.of("2", "5", "8", "12", "15");
                if (opt.equals("dungeon-chance"))   return List.of("5", "10", "15", "20");
                if (opt.equals("village-chance"))   return List.of("8", "12", "16", "20");
                if (opt.equals("city-chance"))      return List.of("1", "2", "4", "6");
            }
        }

        return List.of();
    }

    // ─────────────────────────────────────────
    // Utilidades
    // ─────────────────────────────────────────
    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) deleteFolder(f);
                else f.delete();
            }
        }
        folder.delete();
    }

    private String prefix() {
        return "§6[AWG] §r";
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
