package net.ravinvt.nameless.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.ravinvt.nameless.Nameless.plugin;
import static net.ravinvt.nameless.code.CommonAPI.getPrefix;

public class NamelessCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getPrefix().append(Component.text("Only a player can run this command!")));
            return false;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                player.sendMessage(getPrefix().append(Component.text("Reloading configuration...")));
                plugin.getConfig();
            }
        }
        return true;
    }
}
