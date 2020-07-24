package me.schooltests.potatoolympics.bedwars.events;

import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.bedwars.Validator;
import me.schooltests.potatoolympics.bedwars.game.BedwarsGame;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlaceBlockEvent implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (Validator.isValidPlayer(e.getPlayer()) && e.getBlockPlaced().getType() != Material.TNT) {
            BedwarsGame game = POBedwars.getInstance().getBedwarsGame();
            game.getPlayerPlacedBlocks().add(e.getBlockPlaced().getLocation());
        }
    }
}
