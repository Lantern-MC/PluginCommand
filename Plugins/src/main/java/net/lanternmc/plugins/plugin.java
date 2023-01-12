package net.lanternmc.plugins;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.PluginsCommand;
import org.bukkit.entity.Player;

import java.util.Objects;

public class plugin implements CommandExecutor{

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
             Player p = (Player) sender;
             p.kickPlayer(Plugins.getMe().getConfig().getString("pluginsmessage").replace('&','§'));
             return true;
        }
        return true;
    }
}
