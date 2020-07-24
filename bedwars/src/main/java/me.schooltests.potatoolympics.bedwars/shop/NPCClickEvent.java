package me.schooltests.potatoolympics.bedwars.shop;

import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.Validator;
import me.schooltests.potatoolympics.bedwars.game.BedwarsGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class NPCClickEvent implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onShopNPCRightClick(PlayerInteractEntityEvent e) {
        if (Validator.isActiveGame()) {
            if (!Validator.isValidPlayer(e.getPlayer())) return;
            BedwarsGame game = POBedwars.getInstance().getBedwarsGame();
            if (e.getRightClicked().getType() == EntityType.VILLAGER && e.getRightClicked().getCustomName().equals(ChatColor.YELLOW + "Shop NPC")) {
                e.setCancelled(true);
                new ShopGUI(e.getPlayer()).open(ShopGUI.ShopPage.QUICK_BUY);
            } else if (e.getRightClicked().getType() == EntityType.VILLAGER && e.getRightClicked().getCustomName().equals(ChatColor.YELLOW + "Upgrades NPC")) {
                e.setCancelled(true);
                new UpgradesGUI(e.getPlayer()).open(UpgradesGUI.UpgradesPage.NORMAL);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShopNPCLeftClick(EntityDamageByEntityEvent e) {
        if (Validator.isActiveGame() && e.getDamager() instanceof Player) {
            if (!Validator.isValidPlayer((Player) e.getDamager())) return;
            BedwarsGame game = POBedwars.getInstance().getBedwarsGame();
            if (e.getEntity().getType() == EntityType.VILLAGER && e.getEntity().getCustomName().equals(ChatColor.YELLOW + "Shop NPC")) {
                e.setCancelled(true);
                new ShopGUI((Player) e.getDamager()).open(ShopGUI.ShopPage.QUICK_BUY);
            } else if (e.getEntity().getType() == EntityType.VILLAGER && e.getEntity().getCustomName().equals(ChatColor.YELLOW + "Upgrades NPC")) {
                e.setCancelled(true);
                new UpgradesGUI((Player) e.getDamager()).open(UpgradesGUI.UpgradesPage.NORMAL);
            }
        }
    }
}