package me.schooltests.potatoolympics.bedwars.events;

import me.schooltests.potatoolympics.bedwars.Validator;
import me.schooltests.potatoolympics.core.armor.ArmorEquipEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArmorEvent implements Listener {
    @EventHandler
    public void equip(ArmorEquipEvent event) {
        if (Validator.isValidPlayer(event.getPlayer())) event.setCancelled(true);
    }
}