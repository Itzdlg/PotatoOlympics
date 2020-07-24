package me.schooltests.potatoolympics.bedwars.events;

import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.Validator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

public class HologramEvent implements Listener {
    @EventHandler
    public void onInteract(PlayerArmorStandManipulateEvent e) {
        if (Validator.isActiveGame() && !e.getRightClicked().isVisible()) e.setCancelled(true);
    }
}
