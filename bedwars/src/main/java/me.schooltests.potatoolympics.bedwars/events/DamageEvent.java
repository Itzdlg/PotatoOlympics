package me.schooltests.potatoolympics.bedwars.events;

import me.schooltests.potatoolympics.bedwars.AttackInfo;
import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.Validator;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageEvent implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (Validator.isActiveGame()) {
            if (e.getEntity() instanceof Player) {
                if (e.getDamager() instanceof Player) {
                    Player p = (Player) e.getDamager();
                    if (!Validator.isValidPlayer(p)) return;
                    if (POBedwars.getInstance().getBedwarsGame().getTeam(p).getGameTeam().getTeamColor() == POBedwars.getInstance().getBedwarsGame().getTeam((Player) e.getEntity()).getGameTeam().getTeamColor())
                        e.setCancelled(true);
                    else POBedwars.getInstance().getBedwarsGame().getLastAttacks().put(e.getEntity().getUniqueId(), new AttackInfo((Player) e.getEntity(), (Player) e.getDamager(), ((Player) e.getDamager()).getItemInHand().getType().toString()));
                } else if (e.getDamager() instanceof Projectile) {
                    Player p = (Player) ((Projectile) e.getDamager()).getShooter();
                    if (p != null) {
                        if (POBedwars.getInstance().getBedwarsGame().getTeam(p).getGameTeam().getTeamColor() == POBedwars.getInstance().getBedwarsGame().getTeam((Player) e.getEntity()).getGameTeam().getTeamColor())
                            e.setCancelled(true);
                        else POBedwars.getInstance().getBedwarsGame().getLastAttacks().put(e.getEntity().getUniqueId(), new AttackInfo((Player) e.getEntity(), (Player) ((Projectile) e.getDamager()).getShooter(), e.getDamager().getName()));
                    }
                }
            }
        }
    }
}