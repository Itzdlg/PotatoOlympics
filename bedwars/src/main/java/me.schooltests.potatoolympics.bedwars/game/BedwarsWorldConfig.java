package me.schooltests.potatoolympics.bedwars.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.schooltests.potatoolympics.bedwars.POBedwars;
import me.schooltests.potatoolympics.core.util.TeamUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BedwarsWorldConfig {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File file;
    private JsonObject config;

    public BedwarsWorldConfig(String map) {
        file = new File(POBedwars.getInstance().getDataFolder().toString() + File.separator + "maps", map + ".json");
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            config = new JsonParser().parse(new FileReader(file)).getAsJsonObject();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public World getWorld() {
        return Bukkit.getWorld(config.get("world").getAsString());
    }

    public Location getSpawn(ChatColor color) {
        return parseTextLocation(config.get("spawns").getAsJsonObject().get(TeamUtil.getDisplayColor(color)).toString());
    }

    public Location getBaseGenerator(ChatColor color) {
        return parseTextLocation(config.get("generators").getAsJsonObject().get("base").getAsJsonObject().get(TeamUtil.getDisplayColor(color)).getAsString());
    }

    public Set<Location> getDiamondGenerators() {
        Set<Location> set = new HashSet<>();
        for (JsonElement e : config.get("generators").getAsJsonObject().get("diamond").getAsJsonArray())
            set.add(parseTextLocation(e.getAsString()));

        return set;
    }

    public Set<Location> getEmeraldGenerators() {
        Set<Location> set = new HashSet<>();
        for (JsonElement e : config.get("generators").getAsJsonObject().get("emerald").getAsJsonArray())
            set.add(parseTextLocation(e.getAsString()));

        return set;
    }

    public Set<Location> getShopNPCS() {
        Set<Location> set = new HashSet<>();
        for (JsonElement e : config.get("npcs").getAsJsonObject().get("shop").getAsJsonArray()) {
            set.add(parseTextLocation(e.toString()));
        }

        return set;
    }

    private Location parseTextLocation(String text) {
        String[] parts = text.replace("\"", "").split(",");
        return new Location(getWorld(), Double.valueOf(parts[0]), Double.valueOf(parts[1]), Double.valueOf(parts[2]), Float.valueOf(parts[3]), Float.valueOf(parts[4]));
    }
}
