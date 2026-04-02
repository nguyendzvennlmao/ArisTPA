package me.aris.aristpa.commands;

import me.aris.aristpa.ArisTPA;
import me.aris.aristpa.models.TeleportRequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class TPAcceptCommand implements CommandExecutor {
    private ArisTPA plugin;
    
    public TPAcceptCommand(ArisTPA plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(null, "player-only");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("aristpa.tpaccept")) {
            plugin.getMessageManager().sendMessage(player, "no-permission");
            return true;
        }
        
        if (plugin.getConfigManager().isBlacklistedWorldsEnabled()) {
            if (plugin.getConfigManager().getBlacklistedWorlds().contains(player.getWorld().getName())) {
                plugin.getMessageManager().sendMessage(player, "blacklisted-world-accept");
                return true;
            }
        }
        
        TeleportRequest request = null;
        
        if (args.length > 0) {
            Player senderPlayer = Bukkit.getPlayer(args[0]);
            if (senderPlayer != null) {
                request = plugin.getTPAManager().getRequest(player, senderPlayer);
            }
        } else {
            List<TeleportRequest> requests = plugin.getTPAManager().getRequestsForTarget(player);
            if (!requests.isEmpty()) {
                request = requests.get(0);
            }
        }
        
        if (request == null) {
            plugin.getMessageManager().sendMessage(player, "no-requests-found");
            return true;
        }
        
        if (plugin.getConfigManager().isBlacklistedWorldsEnabled()) {
            if (plugin.getConfigManager().getBlacklistedWorlds().contains(request.getSender().getWorld().getName())) {
                plugin.getMessageManager().sendMessage(player, "blacklisted-world-requester");
                return true;
            }
        }
        
        plugin.getTPAManager().removeRequest(request);
        
        plugin.getMessageManager().sendMessage(player, "accepted-teleport");
        
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player.getName());
        plugin.getMessageManager().sendMessage(request.getSender(), "request-accepted", placeholders);
        
        plugin.getSoundManager().playAccept(player);
        plugin.getSoundManager().playAccept(request.getSender());
        
        if (request.isHere()) {
            plugin.getMessageManager().sendMessage(request.getSender(), "teleport-to-you", placeholders);
            plugin.getTeleportExecutor().startTeleport(request.getSender(), player.getLocation(), null);
        } else {
            plugin.getTeleportExecutor().startTeleport(player, request.getSender().getLocation(), null);
        }
        
        return true;
    }
          }
