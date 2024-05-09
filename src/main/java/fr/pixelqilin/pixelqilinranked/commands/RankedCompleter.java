package fr.pixelqilin.pixelqilinranked.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class RankedCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length <= 1) {
            if (sender.hasPermission("pixelqilinranked.admin"))
                return List.of("me", "join", "leave", "see", "top", "setzone", "add", "remove", "set", "help");
            else
                return List.of("me", "join", "leave", "top", "help");
        }

        return null;
    }
}
