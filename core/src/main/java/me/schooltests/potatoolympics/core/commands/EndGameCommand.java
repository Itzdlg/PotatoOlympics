package me.schooltests.potatoolympics.core.commands;

import me.schooltests.potatoolympics.core.PotatoOlympics;
import me.schooltests.potatoolympics.core.data.IGame;
import me.schooltests.potatoolympics.core.util.ChatScanner;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.Optional;

public class EndGameCommand implements CommandExecutor {
    private final String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&6&lPotato&b&llympics&8] &f");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (args.length >= 1) {
                if (PotatoOlympics.getInstance().getCurrentGame().isPresent()) {
                    sender.sendMessage(c(prefix + "Ended " + PotatoOlympics.getInstance().getCurrentGame().get().getName()));
                    PotatoOlympics.getInstance().getCurrentGame().get().end();
                } else sender.sendMessage(c(prefix + "&cThere is no active game to end!"));
            }
        } else sender.sendMessage(c(prefix + "&cYou do not have permission to do this command!"));

        return true;
    }

    private String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}