package me.schooltests.potatoolympics.bedwars;

import me.schooltests.potatoolympics.bedwars.armor.ArmorListener;
import me.schooltests.potatoolympics.bedwars.events.ArmorEvent;
import me.schooltests.potatoolympics.bedwars.events.BedBreakEvent;
import me.schooltests.potatoolympics.bedwars.events.DamageEvent;
import me.schooltests.potatoolympics.bedwars.events.DeathEvent;
import me.schooltests.potatoolympics.bedwars.events.HologramEvent;
import me.schooltests.potatoolympics.bedwars.events.InvisEvent;
import me.schooltests.potatoolympics.bedwars.events.PlaceBlockEvent;
import me.schooltests.potatoolympics.bedwars.events.ScoreboardEvent;
import me.schooltests.potatoolympics.bedwars.events.TNTEvent;
import me.schooltests.potatoolympics.bedwars.game.BedwarsGame;
import me.schooltests.potatoolympics.bedwars.shop.ShopEvents;
import me.schooltests.potatoolympics.bedwars.traps.TrapEvent;
import me.schooltests.potatoolympics.core.PotatoOlympics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class POBedwars extends JavaPlugin {
    private static POBedwars instance;
    private BedwarsGame bedwarsGame = new BedwarsGame();
    public boolean activeGame = false;

    @Override
    public void onEnable() {
        instance = this;

        PotatoOlympics.getInstance().registerGame(bedwarsGame);

        Bukkit.getPluginManager().registerEvents(new ArmorListener(new ArrayList<>()), this);

        Bukkit.getPluginManager().registerEvents(new BedBreakEvent(), this);
        Bukkit.getPluginManager().registerEvents(new DeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlaceBlockEvent(), this);
        Bukkit.getPluginManager().registerEvents(new TNTEvent(), this);
        Bukkit.getPluginManager().registerEvents(new HologramEvent(), this);
        Bukkit.getPluginManager().registerEvents(new DamageEvent(), this);
        Bukkit.getPluginManager().registerEvents(new InvisEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ScoreboardEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ArmorEvent(), this);
        Bukkit.getPluginManager().registerEvents(new TrapEvent(), this);

        Bukkit.getPluginManager().registerEvents(new ShopEvents(), this);
    }

    public static POBedwars getInstance() {
        return instance;
    }

    public BedwarsGame getBedwarsGame() {
        return bedwarsGame;
    }
}