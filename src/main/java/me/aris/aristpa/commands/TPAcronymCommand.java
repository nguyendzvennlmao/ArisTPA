package me.aris.aristpa.commands;

import me.aris.aristpa.ArisTPA;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TPAcancelCommand implements CommandExecutor {
    private ArisTPA plugin;
    
    public TPAcancelCommand(ArisTPA plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(null, "player-only");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("aristpa.tpacancel")) {
            plugin.getMessageManager().sendMessage(player, "no-permission");
            return true;
        }
        
        java.util.List<me.aris.aristpa.models.TeleportRequest> toRemove = new java.util.ArrayList<>();
        for (me.aris.aristpa.models.TeleportRequest request : plugin.getTPAManager().getRequestsForTarget(player)) {
            if (request.getSender().equals(player)) {
                toRemove.add(request);
            }
        }
        
        if (toRemove.isEmpty()) {
            plugin.getMessageManager().sendMessage(player, "cancel-requests-failed");
            return true;
        }
        
        for (me.aris.aristpa.models.TeleportRequest request : toRemove) {
            plugin.getTPAManager().removeRequest(request);
        }
        
        plugin.getMessageManager().sendMessage(player, "cancel-requests");
        
        return true;
    }
              }
