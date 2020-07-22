package me.schooltests.potatoolympics.core.commands;

import me.schooltests.potatoolympics.core.util.ChatScanner;
import me.schooltests.potatoolympics.core.PotatoOlympics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.Arrays;

public class SetModeCommand implements CommandExecutor {
    private final String prefix = "&8[&b&lPotato&6&lOlympics&8] &f";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (args.length >= 1) {
                switch (args[0].toLowerCase()) {
                    case "solo":
                        PotatoOlympics.getInstance().getTeamManager().arrangeSolo();
                        sender.sendMessage(c(prefix + "Set the mode to SOLO"));
                        break;
                    case "teams":
                        PotatoOlympics.getInstance().getTeamManager().arrangeTeams();
                        sender.sendMessage(c(prefix + "Set the mode to TEAMS"));
                        break;
                    default:
                        sender.sendMessage(c(prefix + "/setmode <solo|teams>"));
                }
            } else {
                new ChatScanner(PotatoOlympics.getInstance(), (HumanEntity) sender, "What mode would you like (SOLO, TEAMS)?")
                        .match(Arrays.asList("solo", "teams"))
                        .handle((raw, match) ->
                                Bukkit.dispatchCommand(sender, "setmode " + match))
                        .ifError((err) -> sender.sendMessage(err.msg()))
                        .await(5);
            }
        } else sender.sendMessage(c(prefix + "&cYou do not have permission to do this command!"));

        return true;
    }

    private String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}