package me.schooltests.potatoolympics.bedwars.generators;

import me.schooltests.potatoolympics.bedwars.util.GenUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class DiamondGen implements IGen {
    private Location dropLocation;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final Map<Material, Integer> drops = new HashMap<Material, Integer>() {{
        put(Material.DIAMOND, 100);
    }};

    private int currentLevel = 1;

    public DiamondGen(Location dropLocation) {
        this.dropLocation = dropLocation;
    }

    @Override
    public void drop() {
        dropLocation.getWorld().dropItem(dropLocation, new ItemStack(GenUtil.getMaterialFromChance(drops)));
    }

    @Override
    public int dropWaitTicks() {
        switch (currentLevel) {
            case 1:
                return (30 * 20);
            case 2:
                return (23 * 20);
            case 3:
                return (17 * 20);
        }

        return (30 * 20);
    }

    @Override
    public void upgrade() {
        currentLevel++;
    }

    @Override
    public int firstUpgradeTicks() {
        return (5 * 60 * 20);
    }

    @Override
    public int secondUpgradeTicks() {
        return (15 * 60 * 20);
    }

    @Override
    public Location getDropLocation() {
        return dropLocation;
    }
}
