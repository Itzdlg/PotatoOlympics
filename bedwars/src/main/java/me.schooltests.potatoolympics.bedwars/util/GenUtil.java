package me.schooltests.potatoolympics.bedwars.util;

import org.bukkit.Material;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class GenUtil {
    public static Material getMaterialFromChance(Map<Material, Integer> chances) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        int total = chances.values().stream().mapToInt(Integer::intValue).sum();
        int count = random.nextInt(total) + 1;

        while (count > 0) {
            for (Material m : chances.keySet()) {
                count -= chances.get(m);
                if (count <= 0) return m;
            }
        }

        return null;
    }
}
