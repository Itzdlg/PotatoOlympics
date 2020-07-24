package me.schooltests.potatoolympics.core.commands;

import me.schooltests.potatoolympics.core.util.ChatScanner;
import me.schooltests.potatoolympics.core.PotatoOlympics;
import me.schooltests.potatoolympics.core.data.IGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.Optional;

public class StartGameCommand implements CommandExecutor {
    private final String prefix = ChatColor.translateAlternateColorCodes('&', "&8[&6&lPotato&b&llympics&8] &f");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (args.length >= 1) {
                String gameToStart = args[0].toLowerCase();
                String map = (args.length >= 2) ? args[1].toLowerCase() : gameToStart;
                Optional<IGame> registeredGame = PotatoOlympics.getInstance().getRegisteredGame(gameToStart);
                if (registeredGame.isPresent()) {
                    registeredGame.get().start(map);
                    sender.sendMessage(c(prefix + "Started a " + gameToStart + " game!"));
                } else sender.sendMessage(c(prefix + "&cThat is not a valid game!"));
            } else {
                new ChatScanner(PotatoOlympics.getInstance(), (HumanEntity) sender, prefix + "What game would you like to start?")
                        .match(PotatoOlympics.getInstance().getRegisteredGames().values(), t -> ((IGame) t).getName())
                        .handle((raw, match) ->
                                Bukkit.getScheduler().runTask(PotatoOlympics.getInstance(), () -> Bukkit.dispatchCommand(sender, "startgame " + match)))
                        .ifError((err) -> sender.sendMessage(prefix + ChatColor.RED + err.msg()))
                        .await(5);
            }
        } else sender.sendMessage(c(prefix + "&cYou do not have permission to do this command!"));

        return true;
    }

    private String c(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
