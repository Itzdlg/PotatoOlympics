package me.schooltests.potatoolympics.bedwars.generators;

import me.schooltests.potatoolympics.bedwars.util.GenUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BaseGen implements IGen {
    private Location dropLocation;
    private BukkitTask dropTask;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final Map<Material, Integer> drops = new HashMap<Material, Integer>() {{
        put(Material.IRON_INGOT, 90);
        put(Material.GOLD_INGOT, 20);
    }};

    private int currentLevel = 1;

    public BaseGen(Location dropLocation) {
        this.dropLocation = dropLocation;
    }

    @Override
    public void drop() {
        dropLocation.getWorld().dropItem(dropLocation, new ItemStack(GenUtil.getMaterialFromChance(drops)));
    }

    @Override
    public int dropWaitTicks() {
        return currentLevel == 1 ? 30 : 25;
    }

    @Override
    public void upgrade() {
        currentLevel++;
        if (currentLevel == 2)
            drops.put(Material.GOLD_INGOT, 30);
        if (currentLevel == 3)
            drops.put(Material.DIAMOND, 20);
        if (currentLevel == 4)
            drops.put(Material.DIAMOND, 30);
            drops.put(Material.EMERALD, 20);
    }

    @Override
    public int firstUpgradeTicks() {
        return -1;
    }

    @Override
    public int secondUpgradeTicks() {
        return -1;
    }

    public BukkitTask getDropTask() {
        return dropTask;
    }

    public void setDropTask(BukkitTask dropTask) {
        this.dropTask = dropTask;
    }

    @Override
    public Location getDropLocation() {
        return dropLocation;
    }
}