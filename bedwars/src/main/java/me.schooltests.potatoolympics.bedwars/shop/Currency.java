package me.schooltests.potatoolympics.bedwars.shop;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Currency {
    IRON, GOLD, DIAMOND, EMERALD;

    Material getMaterial() {
        switch (this) {
            case GOLD:
                return Material.GOLD_INGOT;
            case DIAMOND:
                return Material.DIAMOND;
            case EMERALD:
                return Material.EMERALD;
            default:
                return Material.IRON_INGOT;
        }
    }

    ChatColor getColor() {
        switch (this) {
            case GOLD:
                return ChatColor.GOLD;
            case DIAMOND:
                return ChatColor.AQUA;
            case EMERALD:
                return ChatColor.DARK_GREEN;
            default:
                return ChatColor.WHITE;
        }
    }

    String getDisplay() {
        switch (this) {
            case GOLD:
                return "Gold";
            case DIAMOND:
                return "Diamond";
            case EMERALD:
                return "Emerald";
            default:
                return "Iron";
        }
    }
}
