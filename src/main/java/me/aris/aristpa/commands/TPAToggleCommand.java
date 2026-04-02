package me.aris.aristpa.commands;

import me.aris.aristpa.ArisTPA;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TPAToggleCommand implements CommandExecutor {
    private ArisTPA plugin;
    
    public TPAToggleCommand(ArisTPA plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(null, "player-only");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("aristpa.tpatoggle")) {
            plugin.getMessageManager().sendMessage(player, "no-permission");
            return true;
        }
        
        boolean current = plugin.getTPAManager().isTPAEnabled(player);
        plugin.getTPAManager().setTPAEnabled(player, !current);
        
        if (!current) {
            plugin.getMessageManager().sendMessage(player, "tpa-toggle-on");
            plugin.getSoundManager().playTPAutoEnable(player);
        } else {
            plugin.getMessageManager().sendMessage(player, "tpa-toggle-off");
            plugin.getSoundManager().playTPAutoDisable(player);
        }
        
        return true;
    }
}
