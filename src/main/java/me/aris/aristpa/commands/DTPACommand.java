package me.aris.aristpa.commands;

import me.aris.aristpa.ArisTPA;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.HashMap;
import java.util.Map;

public class DTPACommand implements CommandExecutor {
    private ArisTPA plugin;
    
    public DTPACommand(ArisTPA plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("aristpa.admin")) {
            plugin.getMessageManager().sendMessage(null, "no-permission");
            return true;
        }
        
        if (args.length < 1) {
            plugin.getMessageManager().sendMessage(null, "dtpa-usage");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("reload")) {
            long startTime = System.currentTimeMillis();
            
            plugin.reloadConfig();
            plugin.getConfigManager().reloadConfigs();
            plugin.getMessageManager().loadMessages();
            plugin.getSoundManager().loadSounds();
            
            long endTime = System.currentTimeMillis();
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("ms", String.valueOf(endTime - startTime));
            plugin.getMessageManager().sendMessage(null, "reload-success", placeholders);
            
            return true;
        }
        
        plugin.getMessageManager().sendMessage(null, "dtpa-usage");
        return true;
    }
    }
