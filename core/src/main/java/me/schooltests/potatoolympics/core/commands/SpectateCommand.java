package me.schooltests.potatoolympics.core.commands;

import me.schooltests.potatoolympics.core.PotatoOlympics;
import me.schooltests.potatoolympics.core.data.GameTeam;
import me.schooltests.potatoolympics.core.util.ChatScanner;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

public class SpectateCommand implements CommandExecutor {
    private final String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&6&lPotato&b&llympics&8] &f");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            PotatoOlympics plugin = PotatoOlympics.getInstance();
            Player player = (Player) sender;
            if (!plugin.getCurrentGame().isPresent()) {
                if (plugin.getTeamManager().getTeam(player) != null) {
                    new ChatScanner(PotatoOlympics.getInstance(), player, "Are you sure you want to become a spectator? &cThis will wipe your points! (yes, or cancel)")
                            .match(Collections.singletonList("yes"))
                            .handle((raw, match) -> {
                                if (match.equalsIgnoreCase("yes")) {
                                    GameTeam team = plugin.getTeamManager().getTeam(player);
                                    int checkIndex = plugin.getTeamManager().getTeams().indexOf(plugin.getTeamManager().getTeam(player));
                                    plugin.getTeamManager().getTeam(player).removeMember(player);
                                } else sender.sendMessage(c(prefix + "&cCancelled"));
                            })
                            .ifError((err) -> sender.sendMessage(c(prefix + "&c" + err.msg())))
                            .await(5);
                } else sender.sendMessage(c(prefix + "&cYou are already a spectator!"));
            } else sender.sendMessage(c(prefix + "&cThis command is only available before games have started!"));
        } else sender.sendMessage(c(prefix + "&cOnly players can use this command!"));

        return true;
    }

    private String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
