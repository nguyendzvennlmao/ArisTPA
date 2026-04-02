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

public class TPAHereCommand implements CommandExecutor {
    private ArisTPA plugin;
    
    public TPAHereCommand(ArisTPA plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageManager().sendMessage(null, "player-only");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("aristpa.tpahere")) {
            plugin.getMessageManager().sendMessage(player, "no-permission");
            return true;
        }
        
        if (args.length < 1) {
            plugin.getMessageManager().sendMessage(player, "tpahere-usage");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", args[0]);
            plugin.getMessageManager().sendMessage(player, "player-not-found", placeholders);
            return true;
        }
        
        if (player.equals(target)) {
            plugin.getMessageManager().sendMessage(player, "self-teleport");
            return true;
        }
        
        if (plugin.getConfigManager().isBlacklistedWorldsEnabled()) {
            if (plugin.getConfigManager().getBlacklistedWorlds().contains(player.getWorld().getName())) {
                plugin.getMessageManager().sendMessage(player, "blacklisted-world-sender");
                return true;
            }
            if (plugin.getConfigManager().getBlacklistedWorlds().contains(target.getWorld().getName())) {
                plugin.getMessageManager().sendMessage(player, "blacklisted-world-target");
                return true;
            }
        }
        
        if (!plugin.getTPAManager().canSendRequest(player, target)) {
            return true;
        }
        
        if (!plugin.getTPAManager().isTPAHereEnabled(target)) {
            plugin.getMessageManager().sendMessage(player, "block-tphere-request");
            return true;
        }
        
        if (plugin.getConfigManager().isGUIEnabled() && plugin.getTPAManager().isGUIHereEnabled(player)) {
            plugin.getTPAHereGUI().openRequestGUI(player, target);
        } else {
            TeleportRequest request = new TeleportRequest(player, target, true);
            plugin.getTPAManager().addRequest(request);
            
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("player", target.getName());
            plugin.getMessageManager().sendMessage(player, "sent-here-request", placeholders);
            
            plugin.getMessageManager().sendRequestMessage(target, player.getName(), true);
            plugin.getSoundManager().playRequestReceived(target);
            plugin.getSoundManager().playRequestSent(player);
        }
        
        return true;
    }
            }
