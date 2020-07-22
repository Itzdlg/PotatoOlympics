package me.schooltests.potatoolympics.bedwars.events;

import me.schooltests.potatoolympics.bedwars.AttackInfo;
import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.game.BedwarsGame;
import me.schooltests.potatoolympics.core.PotatoOlympics;
import me.schooltests.potatoolympics.core.data.TeamPlayer;
import me.schooltests.potatoolympics.core.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class DeathEvent implements Listener {
    private final Set<Material> toDrop = new HashSet<Material>(Arrays.asList(Material.IRON_INGOT, Material.GOLD_INGOT, Material.DIAMOND, Material.EMERALD));
    private final Set<Material> toKeep = new HashSet<Material>(Arrays.asList(Material.WOOD_PICKAXE, Material.IRON_PICKAXE, Material.GOLD_PICKAXE, Material.DIAMOND_PICKAXE, Material.WOOD_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.DIAMOND_AXE));
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (POBedwars.getInstance().activeGame) {
            BedwarsGame game = POBedwars.getInstance().getBedwarsGame();
            AttackInfo attackInfo = game.getLastAttacks().get(e.getEntity().getUniqueId());
            List<ItemStack> drops = new ArrayList<>(e.getDrops());
            for (ItemStack i : e.getDrops()) {
                if (attackInfo != null && toDrop.contains(i.getType()))
                    attackInfo.getAttacker().getInventory().addItem(i);


                if (!toKeep.contains(i.getType())) drops.remove(i);
            }
            e.getDrops().clear();

            e.getEntity().spigot().respawn();

            e.getEntity().teleport(new Location(game.getMapConfig().getWorld(), 0, 150, 0));
            e.getEntity().getInventory().clear();
            e.getEntity().setGameMode(GameMode.SPECTATOR);

            if (!game.getTeam(e.getEntity()).hasBed()) {
                final boolean anyAlive = game.getTeam(e.getEntity()).getGameTeam().getTeamMembers().stream().anyMatch(p -> p.getPlayer().isPresent() && p.getPlayer().get().getGameMode() != GameMode.SPECTATOR);
                if (!anyAlive) {
                    game.getTeam(e.getEntity()).setDead(true);
                    game.getBaseGenerator(game.getTeam(e.getEntity())).getDropTask().cancel();
                    Bukkit.broadcastMessage(" ");
                    Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "ELIMINATION > " + ChatColor.RESET + ChatColor.WHITE + game.getTeam(e.getEntity()).getGameTeam().getTeamColor().name().replaceAll("(?i)dark_", "").toUpperCase() + " team has been eliminated!");
                    Bukkit.broadcastMessage(" ");

                    PacketUtil.sendTitlePacket(e.getEntity(), ChatColor.RED + "You have been eliminated", "", 10, 40, 10);

                    AtomicInteger teamsAlive = new AtomicInteger();
                    game.getBedwarsTeams().forEach(team -> {
                        boolean isAlive = false;
                        for (TeamPlayer teamPlayer : team.getGameTeam().getTeamMembers()) {
                            if (!isAlive && teamPlayer.getPlayer().isPresent() && teamPlayer.getPlayer().get().getGameMode() != GameMode.SPECTATOR) isAlive = true;
                        }

                        if (isAlive) teamsAlive.getAndIncrement();
                    });

                    if (teamsAlive.get() <= 1) {
                        game.getBedwarsTeams().forEach(team -> {
                            team.getGameTeam().getTeamMembers().forEach(member -> {
                                member.getPlayer().ifPresent(player -> {
                                    PacketUtil.sendTitlePacket(player, ChatColor.GOLD + "" + ChatColor.BOLD + "VICTORY!", "", 10, 40, 10);
                                });

                                member.addPoints(20);
                            });
                        });

                        game.getGameTasks().add(Bukkit.getScheduler().runTaskLater(POBedwars.getInstance(), () -> POBedwars.getInstance().getBedwarsGame().end(), 60));
                    }
                } else {
                    if (attackInfo != null)
                        Bukkit.broadcastMessage(game.getTeam(e.getEntity()).getGameTeam().getTeamColor() + e.getEntity().getName() + " " + ChatColor.GRAY + " has been killed by " + game.getTeam(attackInfo.getAttacker()).getGameTeam().getTeamColor() + " " + attackInfo.getAttacker().getName() + ChatColor.AQUA + "" + ChatColor.BOLD + " FINAL KILL!");
                    else
                        Bukkit.broadcastMessage(game.getTeam(e.getEntity()).getGameTeam().getTeamColor() + e.getEntity().getName() + " " + ChatColor.GRAY + " has died." + ChatColor.AQUA + "" + ChatColor.BOLD + " FINAL KILL!");
                }
            } else {
                PacketUtil.sendTitlePacket(e.getEntity(), ChatColor.RED + "Respawning in 5 seconds", "", 15, 60, 15);
                POBedwars.getInstance().getBedwarsGame().getGameTasks().add(Bukkit.getScheduler().runTaskLater(POBedwars.getInstance(), () -> {
                    e.getEntity().setGameMode(GameMode.SURVIVAL);
                    e.getEntity().teleport(POBedwars.getInstance().getBedwarsGame().getMapConfig().getSpawn(PotatoOlympics.getInstance().getTeamManager().getTeam(e.getEntity()).getTeamColor()));
                    for (ItemStack i : drops) e.getEntity().getInventory().addItem(i);
                    game.getTeam(e.getEntity()).resetArmor();
                }, 7 * 20));

                if (attackInfo != null)
                    Bukkit.broadcastMessage(game.getTeam(e.getEntity()).getGameTeam().getTeamColor() + e.getEntity().getName() + " " + ChatColor.GRAY + " has been killed by " + game.getTeam(attackInfo.getAttacker()).getGameTeam().getTeamColor() + " " + attackInfo.getAttacker().getName());
                else
                    Bukkit.broadcastMessage(game.getTeam(e.getEntity()).getGameTeam().getTeamColor() + e.getEntity().getName() + " " + ChatColor.GRAY + " has died.");
            }
        }
    }
}