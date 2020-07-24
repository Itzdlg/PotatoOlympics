package me.schooltests.potatoolympics.bedwars.events;

import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.core.util.InventoryUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InvisEvent implements Listener {
    private static final List<UUID> foot = new ArrayList<>();

    @EventHandler
    public void onInvis(PlayerItemConsumeEvent e) {
        if (POBedwars.getInstance().activeGame) {
            if (e.getItem().getType() == Material.POTION && e.getItem().hasItemMeta()) {
                PotionMeta meta = (PotionMeta) e.getItem().getItemMeta();
                if (meta.hasCustomEffect(PotionEffectType.INVISIBILITY)) {
                    Player p = e.getPlayer();
                    InventoryUtil.remove(p.getInventory(), new ItemStack(Material.GLASS_BOTTLE));
                    fakeRemoveArmor(p.getUniqueId());

                    BukkitTask stepTask = Bukkit.getScheduler().runTaskTimer(POBedwars.getInstance(), () -> {
                        Location l = p.getLocation(); //Get the player's location
                        l.setY(Math.floor(l.getY())); //Make sure the location's y is an integer

                        if (!l.clone().subtract(0, 1, 0).getBlock().isEmpty()) {
                            double x = Math.cos(Math.toRadians(p.getLocation().getYaw())) * 0.25d; //If you don't understand trigonometry, just think of it as rotating the footprints to the direction the player is looking.
                            double y = Math.sin(Math.toRadians(p.getLocation().getYaw())) * 0.25d;

                            if (foot.contains(p.getUniqueId())) //This code just modifies the location with the rotation and the current foot
                                l.add(x, 0.025D, y);
                            else
                                l.subtract(x, -0.025D, y);

                            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.FOOTSTEP, false, (float) l.getX(),(float)  l.getY(), (float) l.getZ(), 0F, 0F, 0F, 1, 0);
                            for (Player loopPlayer : Bukkit.getOnlinePlayers())
                                for (int i = 0; i < 2; i++) ((CraftPlayer) loopPlayer).getHandle().playerConnection.sendPacket(packet);

                            if (foot.contains(p.getUniqueId())) foot.remove(p.getUniqueId());
                            else foot.add(p.getUniqueId());
                        }
                    }, 5, 10);

                    POBedwars.getInstance().getBedwarsGame().getGameTasks().add(stepTask);
                    POBedwars.getInstance().getBedwarsGame().getGameTasks().add(Bukkit.getScheduler().runTaskLater(POBedwars.getInstance(), () -> {
                        foot.remove(p.getUniqueId());
                        stepTask.cancel();
                        sendActualArmor(p.getUniqueId());
                    }, 30 * 20));
                }
            }
        }
    }

    public static void sendActualArmor(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) return;
        for (int slot = 1; slot <= 4; slot++) {
            PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(player.getEntityId(), slot, CraftItemStack.asNMSCopy(getItemInSlot(player, slot)));
            Bukkit.getOnlinePlayers().forEach(loopPlayer -> ((CraftPlayer) loopPlayer).getHandle().playerConnection.sendPacket(packet));
        }
    }

    public static void fakeRemoveArmor(UUID uuid)  {
        Player player = Bukkit.getPlayer(uuid);
        if(player == null) return;
        for (int slot = 1; slot <= 4; slot++) {
            PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(player.getEntityId(), slot, CraftItemStack.asNMSCopy(new ItemStack(Material.AIR)));
            Bukkit.getOnlinePlayers().forEach(loopPlayer -> ((CraftPlayer) loopPlayer).getHandle().playerConnection.sendPacket(packet));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (POBedwars.getInstance().activeGame) {
            if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
                if (((Player) e.getEntity()).hasPotionEffect(PotionEffectType.INVISIBILITY)) sendActualArmor(e.getEntity().getUniqueId());
            }
        }
    }

    private static ItemStack getItemInSlot(Player player, int slot)
    {
        PlayerInventory inv = player.getInventory();
        ItemStack item = null;
        switch (slot) {
            case 1:
                item = inv.getBoots();
                break;
            case 2:
                item = inv.getLeggings();
                break;
            case 3:
                item = inv.getChestplate();
                break;
            case 4:
                item = inv.getHelmet();
                break;
        }


        return item == null || item.getType() == Material.AIR ? null : item;
    }
}