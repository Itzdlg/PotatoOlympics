package me.schooltests.potatoolympics.bedwars;

import me.schooltests.potatoolympics.bedwars.game.BedwarsGame;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public final class Validator {
    private static final POBedwars plugin = POBedwars.getInstance();
    private static final BedwarsGame game = POBedwars.getInstance().getBedwarsGame();

    public Validator() { throw new UnsupportedOperationException(); }

    public static boolean isActiveGame() {
        return plugin.activeGame;
    }

    public static boolean isValidPlayer(Player player) {
        return isActiveGame() && player.getWorld().getUID().equals(game.getMapConfig().getWorld().getUID()) && game.getTeam(player) != null && player.getGameMode() == GameMode.SURVIVAL;
    }
}