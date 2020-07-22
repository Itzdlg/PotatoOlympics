package me.schooltests.potatoolympics.bedwars.events;

import me.schooltests.potatoolympics.bedwars.AttackInfo;
import me.schooltests.potatoolympics.bedwars.POBedwars;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageEvent implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (POBedwars.getInstance().activeGame) {
            if (e.getEntity() instanceof Player) {
                if (e.getDamager() instanceof Player)
                    POBedwars.getInstance().getBedwarsGame().getLastAttacks().put(e.getEntity().getUniqueId(), new AttackInfo((Player) e.getEntity(), (Player) e.getDamager(), ((Player) e.getDamager()).getItemInHand().getType().toString()));
                else if (e.getDamager() instanceof Projectile) {
                    POBedwars.getInstance().getBedwarsGame().getLastAttacks().put(e.getEntity().getUniqueId(), new AttackInfo((Player) e.getEntity(), (Player) ((Projectile) e.getDamager()).getShooter(),  e.getDamager().getName()));
                }
            }
        }
    }
}