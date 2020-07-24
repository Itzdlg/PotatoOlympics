package me.schooltests.potatoolympics.bedwars.events;

import me.schooltests.potatoolympics.bedwars.AttackInfo;
import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.Validator;
import me.schooltests.potatoolympics.bedwars.game.BedwarsGame;
import me.schooltests.potatoolympics.core.PotatoOlympics;
import me.schooltests.potatoolympics.core.util.ItemBuilder;
import me.schooltests.potatoolympics.core.util.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class JoinLeaveEvent implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void join(PlayerJoinEvent e) {
        if (Validator.isValidPlayer(e.getPlayer())) {
            Player p = e.getPlayer();
            POBedwars.getInstance().getBedwarsGame().createScoreboard(p);

            BedwarsGame game = POBedwars.getInstance().getBedwarsGame();
            AttackInfo attackInfo = game.getLastAttacks().get(p.getUniqueId());
            List<ItemStack> drops = new ArrayList<>();

            for (ItemStack i : p.getInventory().getContents()) {
                if (i != null) {
                    if (attackInfo != null && DeathEvent.toDrop.contains(i.getType()))
                        if (Validator.isValidPlayer(attackInfo.getAttacker())) attackInfo.getAttacker().getInventory().addItem(i);

                    if (DeathEvent.toKeep.contains(i.getType())) drops.add(i);
                }
            }

            p.getInventory().clear();
            p.teleport(new Location(game.getMapConfig().getWorld(), 0, 150, 0));
            p.setGameMode(GameMode.SPECTATOR);
            PacketUtil.sendTitle(p, new PacketUtil.FormattedText("Respawning in 5 seconds"), null, 20, 70, 20);
            POBedwars.getInstance().getBedwarsGame().getGameTasks().add(Bukkit.getScheduler().runTaskLater(POBedwars.getInstance(), () -> {
                p.setGameMode(GameMode.SURVIVAL);
                p.teleport(POBedwars.getInstance().getBedwarsGame().getMapConfig().getSpawn(PotatoOlympics.getInstance().getTeamManager().getTeam(p).getTeamColor()));
                POBedwars.getInstance().getBedwarsGame().getTeam(p).resetArmor();

                for (ItemStack i : drops)
                    p.getInventory().addItem(new ItemBuilder(i).type(DeathEvent.getTierDown(i.getType())).get());
            }, 5 * 20));
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        if (Validator.isValidPlayer(e.getPlayer())) POBedwars.getInstance().getBedwarsGame().checkEndGame();
    }
}
