package me.schooltests.potatoolympics.bedwars.events;

import me.schooltests.potatoolympics.bedwars.AttackInfo;
import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.game.BedwarsGame;
import me.schooltests.potatoolympics.core.PotatoOlympics;
import me.schooltests.potatoolympics.core.data.TeamPlayer;
import me.schooltests.potatoolympics.core.util.ItemBuilder;
import me.schooltests.potatoolympics.core.util.PacketUtil;
import me.schooltests.potatoolympics.core.util.TeamUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class DeathEvent implements Listener {
    private final Set<Material> toDrop = new HashSet<>(Arrays.asList(Material.IRON_INGOT, Material.GOLD_INGOT, Material.DIAMOND, Material.EMERALD));
    private final Set<Material> toKeep = new HashSet<>(Arrays.asList(Material.WOOD_PICKAXE, Material.IRON_PICKAXE, Material.GOLD_PICKAXE, Material.DIAMOND_PICKAXE, Material.WOOD_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.DIAMOND_AXE));

    @EventHandler
    public void onDeath(EntityDamageEvent e) {
        if (POBedwars.getInstance().activeGame) {
            if (e.getEntity() instanceof Player && e.getEntity().getLocation().getY() <= 5) {
                e.setCancelled(true);

                Player p = (Player) e.getEntity();
                p.setHealth(20);

                BedwarsGame game = POBedwars.getInstance().getBedwarsGame();
                AttackInfo attackInfo = game.getLastAttacks().get(e.getEntity().getUniqueId());
                List<ItemStack> drops = new ArrayList<>();

                for (ItemStack i : p.getInventory().getContents()) {
                    if (i != null) {
                        if (attackInfo != null && toDrop.contains(i.getType()))
                            attackInfo.getAttacker().getInventory().addItem(i);

                        if (toKeep.contains(i.getType())) drops.add(i);
                    }
                }

                p.getInventory().clear();
                p.teleport(new Location(game.getMapConfig().getWorld(), 0, 150, 0));
                p.getInventory().clear();
                p.setGameMode(GameMode.SPECTATOR);

                if (!game.getTeam(p).hasBed()) {
                    if (attackInfo != null) {
                        Bukkit.broadcastMessage(game.getTeam(p).getGameTeam().getTeamColor() + p.getName() + " " + ChatColor.GRAY + "has been killed by " + game.getTeam(attackInfo.getAttacker()).getGameTeam().getTeamColor() + attackInfo.getAttacker().getName() + ChatColor.AQUA + "" + ChatColor.BOLD + " FINAL KILL!");
                        game.getTeam(attackInfo.getAttacker()).getGameTeam().getMember(attackInfo.getAttacker()).ifPresent(member -> {
                            member.addPoints(10);
                            member.getPlayer().ifPresent(plr -> plr.sendMessage(ChatColor.GOLD + "+ 10 points (Final Kill)"));
                        });
                    } else
                        Bukkit.broadcastMessage(game.getTeam(p).getGameTeam().getTeamColor() + p.getName() + " " + ChatColor.GRAY + "has died" + ChatColor.AQUA + "" + ChatColor.BOLD + " FINAL KILL!");

                    final boolean anyAlive = game.getTeam(p).getGameTeam().getTeamMembers().stream().anyMatch(member -> member.getPlayer().isPresent() && member.getPlayer().get().getGameMode() != GameMode.SPECTATOR);
                    if (!anyAlive) {
                        game.getTeam(p).setDead(true);
                        game.getBaseGenerator(game.getTeam(p)).getDropTask().cancel();
                        Bukkit.broadcastMessage(" ");
                        Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "ELIMINATION > " + ChatColor.RESET + ChatColor.WHITE + TeamUtil.getFormatted(TeamUtil.getDisplayColor(game.getTeam(p).getGameTeam())) + " team has been eliminated!");
                        Bukkit.broadcastMessage(" ");

                        PacketUtil.sendTitle(p, new PacketUtil.FormattedText("Elimination", ChatColor.RED, true), new PacketUtil.FormattedText("You have been eliminated!"), 10, 40, 10);

                        AtomicInteger teamsAlive = new AtomicInteger();
                        game.getBedwarsTeams().forEach(team -> {
                            boolean isAlive = false;
                            for (TeamPlayer teamPlayer : team.getGameTeam().getTeamMembers()) {
                                if (!isAlive && teamPlayer.getPlayer().isPresent() && teamPlayer.getPlayer().get().getGameMode() != GameMode.SPECTATOR)
                                    isAlive = true;
                            }

                            if (isAlive) teamsAlive.getAndIncrement();
                        });

                        if (teamsAlive.get() <= 1) {
                            game.getBedwarsTeams().forEach(team -> {
                                team.getGameTeam().getTeamMembers().forEach(member -> {
                                    member.getPlayer().ifPresent(player -> {
                                        PacketUtil.sendTitle(player, new PacketUtil.FormattedText("VICTORY!", ChatColor.GOLD, true), null, 10, 40, 10);
                                        player.sendMessage(ChatColor.GOLD + "+ 30 points (Victory)");
                                    });

                                    member.addPoints(30);
                                });
                            });

                            game.getGameTasks().add(Bukkit.getScheduler().runTaskLater(POBedwars.getInstance(), () -> POBedwars.getInstance().getBedwarsGame().end(), 60));
                        }
                    }
                } else {
                    PacketUtil.sendTitle(p, new PacketUtil.FormattedText("Respawning in 5 seconds"), null, 20, 70, 20);
                    POBedwars.getInstance().getBedwarsGame().getGameTasks().add(Bukkit.getScheduler().runTaskLater(POBedwars.getInstance(), () -> {
                        p.setGameMode(GameMode.SURVIVAL);
                        p.teleport(POBedwars.getInstance().getBedwarsGame().getMapConfig().getSpawn(PotatoOlympics.getInstance().getTeamManager().getTeam(p).getTeamColor()));
                        for (ItemStack i : drops)
                            p.getInventory().addItem(new ItemBuilder(i).type(getTierDown(i.getType())).get());

                        game.getTeam(p).resetArmor();
                    }, 5 * 20));

                    if (attackInfo != null) {
                        Bukkit.broadcastMessage(game.getTeam(p).getGameTeam().getTeamColor() + p.getName() + " " + ChatColor.GRAY + "has been killed by " + game.getTeam(attackInfo.getAttacker()).getGameTeam().getTeamColor() + attackInfo.getAttacker().getName());
                        game.getTeam(attackInfo.getAttacker()).getGameTeam().getMember(attackInfo.getAttacker()).ifPresent(member -> {
                            member.addPoints(5);
                            member.getPlayer().ifPresent(plr -> plr.sendMessage(ChatColor.GOLD + "+ 5 points (Kill)"));
                        });
                    } else
                        Bukkit.broadcastMessage(game.getTeam(p).getGameTeam().getTeamColor() + p.getName() + " " + ChatColor.GRAY + "has died");
                }
            }
        }
    }

    private Material getTierDown(Material m) {
        switch (m) {
            case IRON_PICKAXE:
                return Material.WOOD_PICKAXE;
            case GOLD_PICKAXE:
                return Material.IRON_PICKAXE;
            case DIAMOND_PICKAXE:
                return Material.GOLD_PICKAXE;
            case STONE_AXE:
                return Material.WOOD_AXE;
            case IRON_AXE:
                return Material.STONE_AXE;
            case DIAMOND_AXE:
                return Material.IRON_AXE;
            default:
                return m;
        }
    }
}