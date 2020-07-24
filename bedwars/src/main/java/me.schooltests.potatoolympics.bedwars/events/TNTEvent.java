package me.schooltests.potatoolympics.bedwars.events;

import me.schooltests.potatoolympics.bedwars.POBedwars;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class TNTEvent implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onTNTPlace(BlockPlaceEvent e) {
        if (e.getBlockPlaced().getType() == Material.TNT) {
            e.getBlockPlaced().setType(Material.AIR);
            e.getBlockPlaced().getWorld().spawnEntity(e.getBlockPlaced().getLocation().clone().add(.5, 0, .5), EntityType.PRIMED_TNT);
        }
    }

    @EventHandler
    public void onTNTExplode(EntityExplodeEvent e) {
        if (POBedwars.getInstance().activeGame && e.getEntity() instanceof TNTPrimed) {
            for (Block b : new ArrayList<>(e.blockList())) {
                if (b.getType() == Material.GLASS || !POBedwars.getInstance().getBedwarsGame().getPlayerPlacedBlocks().contains(b.getLocation()))
                    e.blockList().remove(b);
            }
        }
    }

    @EventHandler
    public void onTNTDamage(EntityDamageByEntityEvent e) {
        if (POBedwars.getInstance().activeGame && e.getDamager() instanceof TNTPrimed) {
            Vector v = e.getEntity().getLocation().toVector().subtract(e.getDamager().getLocation().toVector()).normalize();
            e.getEntity().setVelocity(v.add(new Vector(0, 1, 0)));
            e.setDamage(e.getDamage()/4);
        }
    }
}