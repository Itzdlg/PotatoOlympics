package me.schooltests.potatoolympics.core.commands;

import me.schooltests.potatoolympics.core.PotatoOlympics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetTeamCommand implements CommandExecutor {
    private final String prefix = "&8[&6&lPotato&b&llympics&8] &f";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (PotatoOlympics.getInstance().getCurrentGame().isPresent()) {
                sender.sendMessage(c(prefix + "&cYou can not change team settings during a game!"));
                return true;
            }

            if (args.length >= 2) {
                List<Player> matched = Bukkit.matchPlayer(args[0]);
                if (!matched.isEmpty()) {
                    Player player = matched.get(0);
                    sender.sendMessage(c(prefix + "Moving player to team"));
                    try {
                        int i = Integer.parseInt(args[1]);
                        boolean success = PotatoOlympics.getInstance().getTeamManager().moveTeamPlayer(player, i);
                        if (!success) sender.sendMessage(c(prefix + "&cYou must use an actual team number!"));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(c(prefix + "&cYou must use an actual team number!"));
                    }
                } else sender.sendMessage(c(prefix + "/setteam <player> <team>"));
            } else sender.sendMessage(c(prefix + "/setteam <player> <team>"));
        } else sender.sendMessage(c(prefix + "&cYou do not have permission to do this command!"));

        return true;
    }

    private String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
