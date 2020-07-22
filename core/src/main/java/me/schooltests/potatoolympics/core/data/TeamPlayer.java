package me.schooltests.potatoolympics.core.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class TeamPlayer {
    private UUID uuid;
    private int points = 0;

    public TeamPlayer(Player p) {
        uuid = p.getUniqueId();
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(final int points) {
        this.points += points;
    }

    public void removePoints(final int points) {
        this.points -= points;
        if (this.points < 0) this.points = 0;
    }

    public Optional<Player> getPlayer() {
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        if (p.isOnline()) return Optional.of((Player) p);
        else return Optional.empty();
    }
}