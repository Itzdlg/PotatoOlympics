package me.schooltests.potatoolympics.bedwars.generators;

import org.bukkit.Location;

public interface IGen {
    void drop();
    int dropWaitTicks();
    void upgrade();
    int firstUpgradeTicks();
    int secondUpgradeTicks();
    Location getDropLocation();
}