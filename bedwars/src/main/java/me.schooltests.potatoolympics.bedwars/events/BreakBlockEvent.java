package me.schooltests.potatoolympics.bedwars.events;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.game.BedwarsGame;
import me.schooltests.potatoolympics.bedwars.game.BedwarsTeam;
import me.schooltests.potatoolympics.core.data.TeamPlayer;
import me.schooltests.potatoolympics.core.util.TeamUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Optional;
import java.util.Set;

public class BreakBlockEvent implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (POBedwars.getInstance().activeGame) {
            BedwarsGame game = POBedwars.getInstance().getBedwarsGame();
            if (e.getBlock().getType() == Material.BED || e.getBlock().getType() == Material.BED_BLOCK) {
                Set<ProtectedRegion> regions = WorldGuardPlugin.inst().getRegionManager(game.getMapConfig().getWorld()).getApplicableRegions(e.getBlock().getLocation()).getRegions();
                Optional<BedwarsTeam> optionalBedwarsTeam = game.getBedwarsTeams().stream().filter(bedwarsTeam -> regions.stream().anyMatch(r -> r.getId().endsWith(TeamUtil.getDisplayColor(bedwarsTeam.getGameTeam().getTeamColor())))).findFirst();
                if (optionalBedwarsTeam.isPresent()) {
                    BedwarsTeam team = optionalBedwarsTeam.get();
                    if (regions.stream().noneMatch(r -> r.getId().endsWith(TeamUtil.getDisplayColor(team.getGameTeam())))) {
                        team.setHasBed(false);

                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (!team.getGameTeam().getMember(p).isPresent()) {
                                Bukkit.broadcastMessage(" ");
                                Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "BED DESTRUCTION > " + ChatColor.WHITE + TeamUtil.getDisplayColor(team.getGameTeam()).toUpperCase() + " team's bed has been destroyed by " + ChatColor.RED + e.getPlayer().getName() + ChatColor.WHITE + "!");
                                Bukkit.broadcastMessage(" ");
                            }
                        }

                        for (Player p : Bukkit.getOnlinePlayers())
                            game.getMapConfig().getWorld().playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 1);
                        for (TeamPlayer teamPlayer : team.getGameTeam().getTeamMembers())
                            teamPlayer.getPlayer().ifPresent(p -> {
                                p.sendMessage(" ");
                                p.sendMessage(ChatColor.RED + " " + ChatColor.BOLD + "BED DESTRUCTION > " + ChatColor.RESET + ChatColor.WHITE + "Your bed has been destroyed by " + ChatColor.RED + e.getPlayer().getName() + ChatColor.WHITE + "!");
                                p.sendMessage(" ");
                            });
                    } else e.setCancelled(true);
                }
            } else if (!game.getPlayerPlacedBlocks().contains(e.getBlock().getLocation())) e.setCancelled(true);
            else game.getPlayerPlacedBlocks().remove(e.getBlock().getLocation());
        }
    }
}