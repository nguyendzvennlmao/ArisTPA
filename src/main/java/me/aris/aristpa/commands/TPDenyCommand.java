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

public class TPDenyCommand implements CommandExecutor {
    private ArisTPA plugin;
    
    public TPDenyCommand(ArisTPA plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(null, "player-only");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("aristpa.tpdeny")) {
            plugin.getMessageManager().sendMessage(player, "no-permission");
            return true;
        }
        
        TeleportRequest request = null;
        
        if (args.length > 0) {
            Player senderPlayer = Bukkit.getPlayer(args[0]);
            if (senderPlayer != null) {
                request = plugin.getTPAManager().getRequest(player, senderPlayer);
            }
        } else {
            java.util.List<TeleportRequest> requests = plugin.getTPAManager().getRequestsForTarget(player);
            if (!requests.isEmpty()) {
                request = requests.get(0);
            }
        }
        
        if (request == null) {
            plugin.getMessageManager().sendMessage(player, "non-valid-request");
            return true;
        }
        
        plugin.getTPAManager().removeRequest(request);
        
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", request.getSender().getName());
        plugin.getMessageManager().sendMessage(player, "cancelled-request", placeholders);
        
        placeholders.put("player", player.getName());
        plugin.getMessageManager().sendMessage(request.getSender(), "cancelled-request-sender", placeholders);
        
        plugin.getSoundManager().playCancel(player);
        plugin.getSoundManager().playCancel(request.getSender());
        
        return true;
    }
          }
