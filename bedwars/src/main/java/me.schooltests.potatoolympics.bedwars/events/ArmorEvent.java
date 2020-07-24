package me.schooltests.potatoolympics.bedwars.events;

import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.armor.ArmorEquipEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArmorEvent implements Listener {
    @EventHandler
    public void equip(ArmorEquipEvent event) {
        if (POBedwars.getInstance().activeGame) event.setCancelled(true);
    }
}