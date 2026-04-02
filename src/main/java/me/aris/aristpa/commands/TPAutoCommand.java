package me.aris.aristpa.commands;

import me.aris.aristpa.ArisTPA;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TPAutoCommand implements CommandExecutor {
    private ArisTPA plugin;
    
    public TPAutoCommand(ArisTPA plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(null, "player-only");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("aristpa.tpauto")) {
            plugin.getMessageManager().sendMessage(player, "no-permission");
            return true;
        }
        
        boolean current = plugin.getTPAManager().isTPAutoEnabled(player);
        plugin.getTPAManager().setTPAutoEnabled(player, !current);
        
        if (!current) {
            plugin.getMessageManager().sendMessage(player, "tp-auto-on");
            plugin.getMessageManager().sendMessage(player, "tp-auto-on-persistent");
            plugin.getSoundManager().playTPAutoEnable(player);
        } else {
            plugin.getMessageManager().sendMessage(player, "tp-auto-off");
            plugin.getSoundManager().playTPAutoDisable(player);
        }
        
        return true;
    }
                                                   }
