package me.schooltests.potatoolympics.core.data;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class GameTeam {
    public static final List<ChatColor> acceptableColors = Collections.unmodifiableList(Arrays.asList(ChatColor.WHITE, ChatColor.RED, ChatColor.GREEN, ChatColor.BLUE, ChatColor.AQUA, ChatColor.LIGHT_PURPLE, ChatColor.DARK_GRAY, ChatColor.YELLOW));

    public static List<ChatColor> usableColors = new ArrayList<>(acceptableColors);

    private Set<TeamPlayer> players = new HashSet<>();
    private final ChatColor teamColor = usableColors.get(ThreadLocalRandom.current().nextInt(0, usableColors.size()));

    public GameTeam(Player... players) {
        for (Player p : players)
            this.players.add(new TeamPlayer(p));
        if (usableColors.size() > 2) usableColors.remove(teamColor);

    }

    public void addMember(Player p) {
        players.add(new TeamPlayer(p));
    }

    public void removeMember(Player p) {
        if (getMember(p).isPresent())
            players.remove(getMember(p).get());
    }

    public Optional<TeamPlayer> getMember(Player p) {
        return players.stream().filter(tP -> {
            if (tP.getPlayer().isPresent())
                return tP.getPlayer().get().getUniqueId().equals(p.getUniqueId());
            else return false;
        }).findFirst();
    }

    public int getTeamPoints() {
        return players.stream().mapToInt(TeamPlayer::getPoints).sum();
    }

    public ChatColor getTeamColor() {
        return teamColor;
    }

    public Set<TeamPlayer> getTeamMembers() {
        return Collections.unmodifiableSet(players);
    }
}
