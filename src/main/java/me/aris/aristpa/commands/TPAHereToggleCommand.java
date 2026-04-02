package me.aris.aristpa.commands;

import me.aris.aristpa.ArisTPA;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TPAHereToggleCommand implements CommandExecutor {
    private ArisTPA plugin;
    
    public TPAHereToggleCommand(ArisTPA plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(null, "player-only");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("aristpa.tpaheretoggle")) {
            plugin.getMessageManager().sendMessage(player, "no-permission");
            return true;
        }
        
        boolean current = plugin.getTPAManager().isTPAHereEnabled(player);
        plugin.getTPAManager().setTPAHereEnabled(player, !current);
        
        if (!current) {
            plugin.getMessageManager().sendMessage(player, "tpa-here-toggle-on");
            plugin.getSoundManager().playTPAHereToggleOn(player);
        } else {
            plugin.getMessageManager().sendMessage(player, "tpa-here-toggle-off");
            plugin.getSoundManager().playTPAHereToggleOff(player);
        }
        
        return true;
    }
}
