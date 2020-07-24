package me.schooltests.potatoolympics.core.util;

import me.schooltests.potatoolympics.core.data.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.Color;

public class TeamUtil {
    public static String getDisplayColor(ChatColor color) {
        switch (color) {
            case LIGHT_PURPLE:
                return "pink";
            case DARK_GRAY:
                return "gray";
            default:
                return color.name().toLowerCase();
        }
    }

    public static String getDisplayColor(GameTeam team) {
        return getDisplayColor(team.getTeamColor());
    }

    public static int getWoolColor(GameTeam team) {
        switch (team.getTeamColor()) {
            case RED:
                return 14;
            case GREEN:
                return 5;
            case BLUE:
                return 11;
            case AQUA:
                return 3;
            case LIGHT_PURPLE:
                return 6;
            case DARK_GRAY:
                return 7;
            case YELLOW:
                return 4;
            default:
                return 0;
        }
    }

    public static Color getBukkitColor(ChatColor color) {
        switch (color) {
            case RED:
                return Color.RED;
            case GREEN:
                return Color.LIME;
            case BLUE:
                return Color.BLUE;
            case AQUA:
                return Color.AQUA;
            case LIGHT_PURPLE:
                return Color.FUCHSIA;
            case DARK_GRAY:
                return Color.GRAY;
            case YELLOW:
                return Color.YELLOW;
            default:
                return Color.WHITE;
        }
    }

    public static String getFormatted(String lowercase) {
        return lowercase.substring(0, 1).toUpperCase() + lowercase.toLowerCase().substring(1);
    }

    public static Color getBukkitColor(GameTeam team) {
        return getBukkitColor(team.getTeamColor());
    }
}
