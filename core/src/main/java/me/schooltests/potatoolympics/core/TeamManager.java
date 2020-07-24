package me.schooltests.potatoolympics.core;

import me.schooltests.potatoolympics.core.data.GameTeam;
import me.schooltests.potatoolympics.core.data.TeamPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class TeamManager {
    private PotatoOlympics plugin = PotatoOlympics.getInstance();
    private List<GameTeam> teams = new ArrayList<>();

    public void arrangeSolo() {
        Map<Player, Integer> points = new HashMap<>();
        for (GameTeam team : getTeams()) {
            for (TeamPlayer teamPlayer : team.getTeamMembers()) {
                teamPlayer.getPlayer().ifPresent(p -> points.put(p, teamPlayer.getPoints()));
            }
        }

        teams.clear();
        GameTeam.usableColors = new ArrayList<>(GameTeam.acceptableColors);
        Bukkit.getOnlinePlayers().forEach(p -> {
            GameTeam team = new GameTeam(p);
            team.getMember(p).ifPresent(teamPlayer -> teamPlayer.addPoints(teamPlayer.getPlayer().isPresent() ? points.get(teamPlayer.getPlayer().get()) : 0));
            teams.add(team);
        });

        arrangeTab();
    }

    public void arrangeTeams() {
        Map<Player, Integer> points = new HashMap<>();
        for (GameTeam team : getTeams()) {
            for (TeamPlayer teamPlayer : team.getTeamMembers()) {
                teamPlayer.getPlayer().ifPresent(p -> points.put(p, teamPlayer.getPoints()));
            }
        }

        teams.clear();
        GameTeam.usableColors = new ArrayList<>(GameTeam.acceptableColors);
        final int numPlayers = Bukkit.getOnlinePlayers().size();
        if (numPlayers >= 2) {
            int i = 0;
            GameTeam previous = null;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (previous == null) previous = new GameTeam(p);
                else {
                    previous.addMember(p);
                    previous.getMember(p).ifPresent(teamPlayer -> teamPlayer.addPoints(teamPlayer.getPlayer().isPresent() ? points.get(teamPlayer.getPlayer().get()) : 0));
                    teams.add(previous);
                    previous = null;
                }

                if (i >= numPlayers) teams.add(previous);
                i++;
            }
        } else arrangeSolo();

        arrangeTab();
    }

    public void arrangeTab() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
            sb.getTeams().forEach(team -> {
                if (!team.getName().startsWith("game_")) team.unregister();
            });

            for (Player p2 : Bukkit.getOnlinePlayers()) {
                GameTeam playerTeam = getTeam(p2);
                Team sbTeam = sb.getTeam(getAlphabeticalChar(teams.indexOf(playerTeam)) + "-TEAM");
                if (sbTeam == null) {
                    Team newTeam = sb.registerNewTeam(getAlphabeticalChar(teams.indexOf(playerTeam)) + "-TEAM");
                    newTeam.setPrefix(playerTeam.getTeamColor() + "" + ChatColor.BOLD + "TEAM " + (teams.indexOf(playerTeam) + 1) + " " + ChatColor.RESET + ChatColor.GRAY);
                    sbTeam = newTeam;
                }

                sbTeam.addEntry(p2.getName());
            }

            p.setScoreboard(sb);
        }
    }

    private char getAlphabeticalChar(int index) {
        final String s = "ABCDEFHIJKLMNOPQRSTUVWXYZ";
        return s.charAt(index);
    }

    public GameTeam getTeam(Player p) {
        Optional<GameTeam> team = teams.stream().filter(t -> t.getMember(p).isPresent()).findFirst();
        return team.orElse(null);
    }

    public TeamPlayer getTeamPlayer(Player p) {
        GameTeam t = getTeam(p);
        return t.getMember(p).orElse(null);
    }

    public void addPlayerToOpenTeam(Player p) {
        if (getTeam(p) == null) {
            if (teams.isEmpty()) teams.add(new GameTeam(p));
            else {
                GameTeam last = teams.get(teams.size() - 1);
                if (last.getTeamMembers().size() <= 1)
                    last.addMember(p);
                else teams.add(new GameTeam(p));
            }
        }

        arrangeTab();
    }

    public boolean moveTeamPlayer(Player p, int index) {
        if (index <= teams.size()) {
            GameTeam team = getTeam(p);
            final int points = team.getMember(p).get().getPoints();
            team.removeMember(p);

            GameTeam newTeam = teams.get(index - 1);
            newTeam.addMember(p);
            newTeam.getMember(p).ifPresent(t -> t.addPoints(points));

            arrangeTab();
            return true;
        } else return false;
    }

    public List<GameTeam> getTeams() {
        return Collections.unmodifiableList(teams);
    }
}