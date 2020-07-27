package me.schooltests.potatoolympics.core;

import me.schooltests.potatoolympics.core.armor.ArmorListener;
import me.schooltests.potatoolympics.core.commands.EndGameCommand;
import me.schooltests.potatoolympics.core.commands.SetModeCommand;
import me.schooltests.potatoolympics.core.commands.SetTeamCommand;
import me.schooltests.potatoolympics.core.commands.StartGameCommand;
import me.schooltests.potatoolympics.core.data.IGame;
import me.schooltests.potatoolympics.core.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class PotatoOlympics extends JavaPlugin {
    private static PotatoOlympics instance;
    private TeamManager teamManager;
    private Map<String, IGame> registeredGames = new HashMap<>();
    private Optional<IGame> currentGame = Optional.empty();

    @Override
    public void onEnable() {
        instance = this;
        teamManager = new TeamManager();

        getCommand("startgame").setExecutor(new StartGameCommand());
        getCommand("endgame").setExecutor(new EndGameCommand());
        getCommand("setmode").setExecutor(new SetModeCommand());
        getCommand("setteam").setExecutor(new SetTeamCommand());

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void join(PlayerJoinEvent e) {
                if (!currentGame.isPresent()) e.getPlayer().getInventory().clear();
            }

            @EventHandler(priority = EventPriority.HIGHEST)
            public void chat(AsyncPlayerChatEvent e) {
                if (teamManager.getTeam(e.getPlayer()) != null)
                    e.setFormat(ChatColor.GRAY + "[" + teamManager.getTeam(e.getPlayer()).getTeamColor() + "" + ChatColor.BOLD + "TEAM " + (teamManager.getTeams().indexOf(teamManager.getTeam(e.getPlayer())) + 1) + ChatColor.RESET + ChatColor.GRAY + "] " + ChatColor.WHITE + e.getPlayer().getDisplayName() + ChatColor.RESET + ": " + e.getMessage());
                else
                    e.setFormat(ChatColor.GRAY + "[] " + ChatColor.WHITE + e.getPlayer().getDisplayName() + ChatColor.RESET + ": " + e.getMessage());
            }
        }, this);

        Bukkit.getPluginManager().registerEvents(new ArmorListener(Collections.emptyList()), this);
    }

    @Override
    public void onDisable() {
        currentGame.ifPresent(IGame::end);
        Bukkit.getScoreboardManager().getMainScoreboard().getObjectives().forEach(Objective::unregister);
        Bukkit.getScoreboardManager().getMainScoreboard().getTeams().forEach(Team::unregister);
    }

    public static PotatoOlympics getInstance() {
        return instance;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public void registerGame(IGame game) {
        if (!registeredGames.containsKey(game.getName().toLowerCase())) registeredGames.put(game.getName().toLowerCase(), game);
    }

    public Optional<IGame> getRegisteredGame(String name) {
        if (registeredGames.containsKey(name.toLowerCase())) return Optional.of(registeredGames.get(name.toLowerCase()));
        else return Optional.empty();
    }

    public Map<String, IGame> getRegisteredGames() {
        return registeredGames;
    }

    public Optional<IGame> getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(IGame currentGame) {
        this.currentGame = Optional.ofNullable(currentGame);
    }
}