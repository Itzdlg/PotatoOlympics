package me.schooltests.potatoolympics.bedwars.events;

import me.schooltests.potatoolympics.bedwars.POBedwars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ScoreboardEvent implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void join(PlayerJoinEvent e) {
        if (POBedwars.getInstance().activeGame) {
            POBedwars.getInstance().getBedwarsGame().createScoreboard(e.getPlayer());
        }
    }
}
