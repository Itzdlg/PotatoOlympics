package me.schooltests.potatoolympics.bedwars.events;

import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.Validator;
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

public class BedBreakEvent implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (Validator.isValidPlayer(e.getPlayer())) {
            BedwarsGame game = POBedwars.getInstance().getBedwarsGame();
            if (e.getBlock().getType() == Material.BED || e.getBlock().getType() == Material.BED_BLOCK) {
                Set<ProtectedRegion> regions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(e.getBlock().getLocation()).getRegions();
                Optional<BedwarsTeam> optionalBedwarsTeam = game.getBedwarsTeams().stream().filter(bedwarsTeam -> regions.stream().anyMatch(r -> r.getId().endsWith(TeamUtil.getDisplayColor(bedwarsTeam.getGameTeam().getTeamColor())))).findFirst();
                if (optionalBedwarsTeam.isPresent()) {
                    BedwarsTeam team = optionalBedwarsTeam.get();
                    team.setHasBed(false);

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!team.getGameTeam().getMember(p).isPresent()) {
                            Bukkit.broadcastMessage(" ");
                            Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "BED DESTRUCTION > " + ChatColor.WHITE + TeamUtil.getFormatted(TeamUtil.getDisplayColor(team.getGameTeam())) + " team's bed has been destroyed by " + game.getTeam(e.getPlayer()).getGameTeam().getTeamColor() + e.getPlayer().getName() + ChatColor.WHITE + "!");
                            Bukkit.broadcastMessage(" ");
                        }
                    }

                    for (Player p : Bukkit.getOnlinePlayers())
                        game.getMapConfig().getWorld().playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 1);
                    for (TeamPlayer teamPlayer : team.getGameTeam().getTeamMembers())
                        teamPlayer.getPlayer().ifPresent(p -> {
                            p.sendMessage(" ");
                            p.sendMessage(ChatColor.RED + " " + ChatColor.BOLD + "BED DESTRUCTION > " + ChatColor.RESET + ChatColor.WHITE + "Your bed has been destroyed by " + game.getTeam(e.getPlayer()).getGameTeam().getTeamColor() + e.getPlayer().getName() + ChatColor.WHITE + "!");
                            p.sendMessage(" ");
                        });

                    game.getTeam(e.getPlayer()).getGameTeam().getMember(e.getPlayer()).ifPresent(member -> {
                        member.addPoints(20);
                        member.getPlayer().ifPresent(plr -> plr.sendMessage(ChatColor.GOLD + "+ 20 points (Bed Break)"));
                    });
                }
            } else if (!game.getPlayerPlacedBlocks().contains(e.getBlock().getLocation())) e.setCancelled(true);
            else game.getPlayerPlacedBlocks().remove(e.getBlock().getLocation());
        }
    }
}