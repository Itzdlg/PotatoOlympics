package me.schooltests.potatoolympics.bedwars.game;

import me.schooltests.potatoolympics.bedwars.AttackInfo;
import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.events.InvisEvent;
import me.schooltests.potatoolympics.bedwars.generators.BaseGen;
import me.schooltests.potatoolympics.bedwars.generators.DiamondGen;
import me.schooltests.potatoolympics.bedwars.generators.EmeraldGen;
import me.schooltests.potatoolympics.bedwars.generators.IGen;
import me.schooltests.potatoolympics.core.PotatoOlympics;
import me.schooltests.potatoolympics.core.data.GameTeam;
import me.schooltests.potatoolympics.core.data.IGame;
import me.schooltests.potatoolympics.core.data.TeamPlayer;
import me.schooltests.potatoolympics.core.util.TeamUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BedwarsGame implements IGame {
    public static final String SCOREBOARD_CHECKMARK = "✓";
    public static final String SCOREBOARD_XMARK = "✘";

    private BedwarsWorldConfig mapConfig;
    private Set<BedwarsTeam> bedwarsTeams;
    private Set<Location> playerPlacedBlocks;
    private Set<Entity> gameEntities;
    private Map<BedwarsTeam, BaseGen> baseGens;
    private Set<BukkitTask> gameTasks;
    private Map<UUID, AttackInfo> lastAttacks;
    private Set<DiamondGen> diamondGens;
    private Set<EmeraldGen> emeraldGens;

    private AtomicBoolean announcedDiamondFirst = new AtomicBoolean(false);
    private AtomicBoolean announcedDiamondSecond = new AtomicBoolean(false);
    private AtomicBoolean announcedEmeraldFirst = new AtomicBoolean(false);
    private AtomicBoolean announcedEmeraldSecond = new AtomicBoolean(false);

    @Override
    public String getName() {
        return "bedwars";
    }

    @Override
    public void start(String map) {
        POBedwars.getInstance().activeGame = true;
        PotatoOlympics.getInstance().setCurrentGame(this);
        mapConfig = new BedwarsWorldConfig(map);
        bedwarsTeams = new HashSet<>();
        playerPlacedBlocks = new HashSet<>();
        gameEntities = new HashSet<>();
        baseGens = new HashMap<>();
        gameTasks = new HashSet<>();
        lastAttacks = new HashMap<>();
        diamondGens = new HashSet<>();
        emeraldGens = new HashSet<>();

        spawnNPCS();
        teleportPlayers();
        startBaseOperations();
        startDiamondGenerators();
        startEmeraldGenerators();
        createScoreboard();
    }

    @Override
    public void end() {
        POBedwars.getInstance().activeGame = false;
        PotatoOlympics.getInstance().setCurrentGame(null);

        for (BukkitTask task : gameTasks) task.cancel();
        for (BedwarsTeam team : bedwarsTeams) {
            for (TeamPlayer p : team.getGameTeam().getTeamMembers()) {
                p.getPlayer().ifPresent(plr -> {
                    plr.getInventory().clear();
                    plr.getInventory().setArmorContents(new ItemStack[]{});
                    plr.getActivePotionEffects().forEach(pE -> plr.removePotionEffect(pE.getType()));
                    plr.setHealth(20);
                    plr.teleport(Bukkit.getWorld("world").getSpawnLocation());
                    plr.setGameMode(GameMode.SURVIVAL);
                });

                getBaseGenerator(team).getDropTask().cancel();
            }
        }

        for (Location l : playerPlacedBlocks)
            mapConfig.getWorld().getBlockAt(l).setType(Material.AIR);
        playerPlacedBlocks.clear();

        gameEntities.forEach(Entity::remove);
        Bukkit.getScoreboardManager().getMainScoreboard().getObjective(ChatColor.YELLOW + "" + ChatColor.BOLD + "BEDWARS").unregister();
        Bukkit.getScoreboardManager().getMainScoreboard().getTeams().forEach(t -> {
            if (t.getName().startsWith("game_")) t.unregister();
        });

        mapConfig.getWorld().getEntities().forEach(e -> {
            if (e instanceof ArmorStand) {
                ArmorStand aS = (ArmorStand) e;
                if (!aS.isVisible() && aS.getCustomName().startsWith(ChatColor.GOLD + "Spawning in")) e.remove();
            } else if (e instanceof Item) e.remove();
        });

        InvisEvent.getNames().clear();
    }

    private void teleportPlayers() {
        for (GameTeam team : PotatoOlympics.getInstance().getTeamManager().getTeams()) {
            for (TeamPlayer teamPlayer : team.getTeamMembers()) {
                teamPlayer.getPlayer().ifPresent(player -> {
                    player.sendMessage(ChatColor.WHITE + "You are now playing " + ChatColor.GOLD + "BEDWARS" + ChatColor.WHITE + "!");
                    player.teleport(mapConfig.getSpawn(team.getTeamColor()));
                    player.getInventory().clear();
                    player.setGameMode(GameMode.SURVIVAL);
                });
            }
        }
    }

    private void startBaseOperations() {
        for (GameTeam team : PotatoOlympics.getInstance().getTeamManager().getTeams()) {
            BedwarsTeam bedwarsTeam = new BedwarsTeam(team);
            bedwarsTeams.add(bedwarsTeam);

            BaseGen gen = new BaseGen(mapConfig.getBaseGenerator(team.getTeamColor()));
            baseGens.put(bedwarsTeam, gen);

            startGenTimer(gen);
        }
    }

    private void startDiamondGenerators() {
        for (Location location : mapConfig.getDiamondGenerators()) {
            DiamondGen gen = new DiamondGen(location);
            diamondGens.add(gen);

            startGenTimer(gen);
            startGenUpgradeTimer(gen);
        }
    }

    private void startEmeraldGenerators() {
        for (Location location : mapConfig.getEmeraldGenerators()) {
            EmeraldGen gen = new EmeraldGen(location);
            emeraldGens.add(gen);

            startGenTimer(gen);
            startGenUpgradeTimer(gen);
        }
    }

    public BedwarsTeam getTeam(Player p) {
        return bedwarsTeams.stream().filter(t -> t.getGameTeam().getMember(p).isPresent()).findFirst().get();
    }

    public BaseGen getBaseGenerator(BedwarsTeam team) {
        return baseGens.get(team);
    }

    private void startGenTimer(IGen gen) {
        if (gen instanceof BaseGen) {
            ((BaseGen) gen).setDropTask(Bukkit.getScheduler().runTaskLater(POBedwars.getInstance(), () -> {
                gen.drop();
                startGenTimer(gen);
            }, gen.dropWaitTicks()));
        } else {
            ArmorStand hologram = (ArmorStand) mapConfig.getWorld().spawnEntity(gen.getDropLocation().clone().add(0, 2, 0), EntityType.ARMOR_STAND);
            hologram.setVisible(false);
            hologram.setSmall(true);
            hologram.setGravity(false);
            hologram.setCustomNameVisible(true);
            hologram.setMarker(true);
            hologram.setCustomName(ChatColor.GOLD + "Spawning in " + ChatColor.AQUA + gen.dropWaitTicks()/20 + " seconds");

            AtomicInteger dropTime = new AtomicInteger(gen.dropWaitTicks());
            gameTasks.add(Bukkit.getScheduler().runTaskTimer(POBedwars.getInstance(), () -> {
                dropTime.set(dropTime.get()-20);
                hologram.setCustomName(ChatColor.GOLD + "Spawning in " + ChatColor.AQUA + (int) Math.ceil(dropTime.get()/20) + " seconds");
                if (dropTime.get() <= 0) {
                    gen.drop();
                    dropTime.set(gen.dropWaitTicks());
                }
            }, 0L, 20L));
        }
    }

    private void spawnNPCS() {
        for (Location loc : mapConfig.getShopNPCS()) {
            Villager villager = (Villager) mapConfig.getWorld().spawnEntity(loc, EntityType.VILLAGER);
            villager.setCustomName(ChatColor.YELLOW + "Shop NPC");
            villager.setCustomNameVisible(true);

            villager.setProfession(Villager.Profession.LIBRARIAN);
            villager.setCanPickupItems(false);

            villager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 255, false, false));
            gameEntities.add(villager);
        }
    }

    private void startGenUpgradeTimer(IGen gen) {
        gameTasks.add(Bukkit.getScheduler().runTaskLater(POBedwars.getInstance(), () -> {
            String display = gen instanceof EmeraldGen ? "Emerald" : "Diamond";
            if ((gen instanceof DiamondGen && !announcedDiamondFirst.get()) || (gen instanceof EmeraldGen && !announcedEmeraldFirst.get())) {
                if (gen instanceof DiamondGen) announcedDiamondFirst.set(true);
                if (gen instanceof EmeraldGen) announcedEmeraldFirst.set(true);
                Bukkit.broadcastMessage(" ");
                Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "GENERATORS > " + ChatColor.RESET + ChatColor.WHITE + display + " Generator has been upgraded to Level II");
                Bukkit.broadcastMessage(" ");
            }
            gen.upgrade();
        }, gen.firstUpgradeTicks()));
        gameTasks.add(Bukkit.getScheduler().runTaskLater(POBedwars.getInstance(), () -> {
            String display = gen instanceof EmeraldGen ? "Emerald" : "Diamond";
            if ((gen instanceof DiamondGen && !announcedDiamondSecond.get()) || (gen instanceof EmeraldGen && !announcedEmeraldSecond.get())) {
                if (gen instanceof DiamondGen) announcedDiamondSecond.set(true);
                if (gen instanceof EmeraldGen) announcedEmeraldSecond.set(true);
                Bukkit.broadcastMessage(" ");
                Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "GENERATORS > " + ChatColor.RESET + ChatColor.WHITE + display + " Generator has been upgraded to Level III");
                Bukkit.broadcastMessage(" ");
            }
            gen.upgrade();
        }, gen.firstUpgradeTicks() + gen.secondUpgradeTicks()));
    }

    private void createScoreboard() {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective obj = sb.registerNewObjective(ChatColor.YELLOW + "" + ChatColor.BOLD + "BEDWARS", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Team white = sb.registerNewTeam("game_whiteTeam");
        white.addEntry(ChatColor.BLACK + "" + ChatColor.WHITE);
        white.setPrefix(ChatColor.WHITE + "" + ChatColor.BOLD + "W" + ChatColor.WHITE + " White: ");
        obj.getScore(ChatColor.BLACK + "" + ChatColor.WHITE).setScore(13);

        Team red = sb.registerNewTeam("game_redTeam");
        red.addEntry(ChatColor.BLACK + "" + ChatColor.RED);
        red.setPrefix(ChatColor.RED + "" + ChatColor.BOLD + "R" + ChatColor.WHITE + " Red: ");
        obj.getScore(ChatColor.BLACK + "" + ChatColor.RED).setScore(12);

        Team green = sb.registerNewTeam("game_greenTeam");
        green.addEntry(ChatColor.BLACK + "" + ChatColor.GREEN);
        green.setPrefix(ChatColor.GREEN + "" + ChatColor.BOLD + "G" + ChatColor.WHITE + " Green: ");
        obj.getScore(ChatColor.BLACK + "" + ChatColor.GREEN).setScore(11);

        Team blue = sb.registerNewTeam("game_blueTeam");
        blue.addEntry(ChatColor.BLACK + "" + ChatColor.BLUE);
        blue.setPrefix(ChatColor.BLUE + "" + ChatColor.BOLD + "B" + ChatColor.WHITE + " Blue: ");
        obj.getScore(ChatColor.BLACK + "" + ChatColor.BLUE).setScore(10);

        Team aqua = sb.registerNewTeam("game_aquaTeam");
        aqua.addEntry(ChatColor.BLACK + "" + ChatColor.AQUA);
        aqua.setPrefix(ChatColor.AQUA + "" + ChatColor.BOLD + "A" + ChatColor.WHITE + " Aqua: ");
        obj.getScore(ChatColor.BLACK + "" + ChatColor.AQUA).setScore(9);

        Team pink = sb.registerNewTeam("game_pinkTeam");
        pink.addEntry(ChatColor.BLACK + "" + ChatColor.LIGHT_PURPLE);
        pink.setPrefix(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "P" + ChatColor.WHITE + " Pink: ");
        obj.getScore(ChatColor.BLACK + "" + ChatColor.LIGHT_PURPLE).setScore(8);

        Team gray = sb.registerNewTeam("game_grayTeam");
        gray.addEntry(ChatColor.BLACK + "" + ChatColor.DARK_GRAY);
        gray.setPrefix(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "G" + ChatColor.WHITE + " Gray: ");
        obj.getScore(ChatColor.BLACK + "" + ChatColor.DARK_GRAY).setScore(7);

        Team yellow = sb.registerNewTeam("game_yellowTeam");
        yellow.addEntry(ChatColor.BLACK + "" + ChatColor.YELLOW);
        yellow.setPrefix(ChatColor.YELLOW + "" + ChatColor.BOLD + "Y" + ChatColor.WHITE + " Yellow: ");
        obj.getScore(ChatColor.BLACK + "" + ChatColor.YELLOW).setScore(6);

        gameTasks.add(Bukkit.getScheduler().runTaskTimer(POBedwars.getInstance(), () -> {
            white.setSuffix(getScoreboardTeamValue("white"));
            red.setSuffix(getScoreboardTeamValue("red"));
            green.setSuffix(getScoreboardTeamValue("green"));
            blue.setSuffix(getScoreboardTeamValue("blue"));
            aqua.setSuffix(getScoreboardTeamValue("aqua"));
            pink.setSuffix(getScoreboardTeamValue("pink"));
            gray.setSuffix(getScoreboardTeamValue("gray"));
            yellow.setSuffix(getScoreboardTeamValue("yellow"));
        }, 0L, 10L));
    }

    private String getScoreboardTeamValue(String color) {
        Optional<BedwarsTeam> optionalBedwarsTeam = bedwarsTeams.stream().filter(team -> TeamUtil.getDisplayColor(team.getGameTeam()).equalsIgnoreCase(color)).findFirst();
        if (optionalBedwarsTeam.isPresent()) {
            BedwarsTeam team = optionalBedwarsTeam.get();
            if (team.hasBed()) return ChatColor.GREEN + "" + ChatColor.BOLD + SCOREBOARD_CHECKMARK;
            else {
                AtomicInteger numAlive = new AtomicInteger(0);
                team.getGameTeam().getTeamMembers().forEach(member -> {
                    if (member.getPlayer().isPresent() && member.getPlayer().get().getGameMode() != GameMode.SPECTATOR) numAlive.getAndIncrement();
                });

                if (numAlive.get() == 0) return ChatColor.RED + SCOREBOARD_XMARK;
                else return ChatColor.RED + String.valueOf(numAlive.get());
            }
        } else return ChatColor.RED + SCOREBOARD_XMARK;
    }

    public Set<Location> getPlayerPlacedBlocks() {
        return playerPlacedBlocks;
    }

    public BedwarsWorldConfig getMapConfig() {
        return mapConfig;
    }

    public Set<BedwarsTeam> getBedwarsTeams() {
        return Collections.unmodifiableSet(bedwarsTeams);
    }

    public Set<BukkitTask> getGameTasks() {
        return gameTasks;
    }

    public Map<UUID, AttackInfo> getLastAttacks() {
        return lastAttacks;
    }
}
