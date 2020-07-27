package me.schooltests.potatoolympics.bedwars.events;

import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.Validator;
import me.schooltests.potatoolympics.bedwars.shop.PlayerPurchaseItemEvent;
import me.schooltests.potatoolympics.core.util.InventoryUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TNTEvent implements Listener {
    final Map<UUID, BukkitTask> tntPacketTasks = new HashMap<>();
    @EventHandler(priority = EventPriority.NORMAL)
    public void onTNTPlace(BlockPlaceEvent e) {
        if (Validator.isValidPlayer(e.getPlayer())) {
            if (e.getBlockPlaced().getType() == Material.TNT) {
                e.getBlockPlaced().setType(Material.AIR);
                e.getBlockPlaced().getWorld().spawnEntity(e.getBlockPlaced().getLocation().clone().add(.5, 0, .5), EntityType.PRIMED_TNT);
                if (!InventoryUtil.contains(e.getPlayer().getInventory(), new ItemStack(Material.TNT, 1))) removeTask(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onTNTExplode(EntityExplodeEvent e) {
        if (Validator.isActiveGame() && e.getEntity() instanceof TNTPrimed) {
            e.blockList().removeIf(b -> b.getType() == Material.GLASS || !POBedwars.getInstance().getBedwarsGame().getPlayerPlacedBlocks().contains(b.getLocation()));
        }
    }

    @EventHandler
    public void onTNTDamage(EntityDamageByEntityEvent e) {
        if (Validator.isActiveGame() && e.getDamager() instanceof TNTPrimed && e.getEntity() instanceof Player) {
            if (!Validator.isValidPlayer((Player) e.getEntity())) return;
            Vector v = e.getEntity().getLocation().toVector().subtract(e.getDamager().getLocation().toVector()).normalize();
            e.getEntity().setVelocity(v.add(new Vector(0, 1, 0)));
            e.setDamage(e.getDamage()/4);
        }
    }

    @EventHandler
    public void purchaseTNT(PlayerPurchaseItemEvent e) {
        if (Validator.isValidPlayer(e.getPlayer()) && e.getItem().getType() == Material.TNT) {
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(POBedwars.getInstance(), () -> {
                if (InventoryUtil.contains(e.getPlayer().getInventory(), new ItemStack(Material.TNT, 1))) {
                    Location l = e.getPlayer().getLocation().clone().add(0, 1, 0);
                    PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, false, (float) l.getX(),(float)  l.getY(), (float) l.getZ(), 0F, 0F, 0F, 1, 50);
                    for (Entity entity : l.getWorld().getNearbyEntities(l, 20, 256, 20)) {
                        if (entity instanceof Player) ((CraftPlayer) entity).getHandle().playerConnection.sendPacket(packet);
                    }
                }
            }, 0, 5);
            POBedwars.getInstance().getBedwarsGame().getGameTasks().add(task);
            tntPacketTasks.put(e.getPlayer().getUniqueId(), task);
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        removeTask(e.getPlayer());
    }

    private void removeTask(Player p) {
        if (tntPacketTasks.containsKey(p.getUniqueId())) tntPacketTasks.get(p.getUniqueId()).cancel();
        tntPacketTasks.remove(p.getUniqueId());
    }
}