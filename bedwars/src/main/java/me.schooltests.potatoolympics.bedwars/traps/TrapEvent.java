package me.schooltests.potatoolympics.bedwars.traps;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.Validator;
import me.schooltests.potatoolympics.bedwars.game.BedwarsGame;
import me.schooltests.potatoolympics.bedwars.game.BedwarsTeam;
import me.schooltests.potatoolympics.core.util.TeamUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class TrapEvent implements Listener {
    private static final BedwarsGame game = POBedwars.getInstance().getBedwarsGame();
    private static final Map<UUID, BukkitTask> trapImmune = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        if (!Validator.isValidPlayer(e.getPlayer()) || trapImmune.containsKey(e.getPlayer().getUniqueId())) return;
        if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) return;

        Location from = e.getFrom();
        Location to = e.getTo();
        Set<ProtectedRegion> fromRegions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(from).getRegions();
        Set<ProtectedRegion> toRegions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(to).getRegions();

        if (!fromRegions.containsAll(toRegions)) {
            Optional<BedwarsTeam> optionalBedwarsTeam = game.getBedwarsTeams().stream().filter(bedwarsTeam -> toRegions.stream().anyMatch(r -> r.getId().endsWith(TeamUtil.getDisplayColor(bedwarsTeam.getGameTeam().getTeamColor())))).findFirst();
            optionalBedwarsTeam.ifPresent(bedwarsTeam -> bedwarsTeam.trapTriggered(e.getPlayer()));
        }
    }

    @EventHandler
    public void onMagicMilkConsume(PlayerItemConsumeEvent e) {
        if (Validator.isValidPlayer(e.getPlayer()) && e.getItem() != null && e.getItem().getType() != null && e.getItem().getType() == Material.MILK_BUCKET)
            addTrapImmune(e.getPlayer());
    }

    private static void addTrapImmune(Player player) {
        if (trapImmune.containsKey(player.getUniqueId())) {
            trapImmune.get(player.getUniqueId()).cancel();
            trapImmune.remove(player.getUniqueId());
        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(POBedwars.getInstance(), () -> trapImmune.remove(player.getUniqueId()), 30 * 20);
        trapImmune.put(player.getUniqueId(), task);
    }

    public static void clearTrapImmune() {
        trapImmune.values().forEach(BukkitTask::cancel);
        trapImmune.clear();
    }
}