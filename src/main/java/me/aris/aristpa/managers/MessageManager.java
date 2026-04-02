package me.aris.aristpa.managers;

import me.aris.aristpa.ArisTPA;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {
    private ArisTPA plugin;
    private FileConfiguration messages;
    private Map<String, String> messageCache;
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    
    public MessageManager(ArisTPA plugin) {
        this.plugin = plugin;
        this.messageCache = new HashMap<>();
        loadMessages();
    }
    
    public void loadMessages() {
        File messageFile = new File(plugin.getDataFolder(), "message.yml");
        if (messageFile.exists()) {
            messages = YamlConfiguration.loadConfiguration(messageFile);
        } else {
            messages = new YamlConfiguration();
        }
        messageCache.clear();
    }
    
    public String getMessage(String path) {
        if (messageCache.containsKey(path)) {
            return messageCache.get(path);
        }
        String message = messages.getString("messages." + path, "&cMessage not found: " + path);
        message = ChatColor.translateAlternateColorCodes('&', translateHexColors(message));
        messageCache.put(path, message);
        return message;
    }
    
    private String translateHexColors(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String hexCode = matcher.group(1);
            StringBuilder replacement = new StringBuilder(net.md_5.bungee.api.ChatColor.of("#" + hexCode).toString());
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
    
    public void sendMessage(Player player, String path) {
        sendMessage(player, path, new HashMap<>());
    }
    
    public void sendMessage(Player player, String path, Map<String, String> placeholders) {
        if (plugin.getConfigManager().isUseActionBarOnly()) {
            sendActionBar(player, path, placeholders);
            return;
        }
        
        if (plugin.getConfigManager().isChatMessages()) {
            String message = getMessage(path);
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("%" + entry.getKey() + "%", entry.getValue());
            }
            player.sendMessage(message);
        }
        
        if (plugin.getConfigManager().isActionBarMessages()) {
            sendActionBar(player, path, placeholders);
        }
    }
    
    public void sendActionBar(Player player, String path, Map<String, String> placeholders) {
        String message = getMessage(path + "-actionbar");
        if (message.equals("&cMessage not found: " + path + "-actionbar")) {
            message = getMessage(path);
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        player.sendActionBar(message);
    }
    
    public void sendRequestMessage(Player target, String senderName, boolean isHere) {
        String path = isHere ? "receive-here-request" : "receive-request";
        String message = getMessage(path);
        message = message.replace("%player%", senderName);
        
        String[] lines = message.split("\\|");
        for (String line : lines) {
            TextComponent component = new TextComponent(ChatColor.translateAlternateColorCodes('&', line.trim()));
            
            if (line.contains("<clickable>")) {
                String clickText = line.replaceAll(".*<clickable>(.*?)</clickable>.*", "$1");
                String cleanText = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', clickText));
                
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + senderName));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                    new ComponentBuilder("Click to accept").create()));
            }
            
            target.spigot().sendMessage(component);
        }
    }
    
    public void sendTeleportCountdown(Player player, int time) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("time", String.valueOf(time));
        sendMessage(player, "teleport-countdown", placeholders);
    }
    
    public void sendTeleportCancelled(Player player, String reason) {
        if (reason.equals("movement")) {
            sendMessage(player, "teleport-cancelled-movement");
        } else if (reason.equals("flying")) {
            sendMessage(player, "teleport-cancelled-flying");
        } else {
            sendMessage(player, "teleport-cancelled");
        }
    }
    
    public void sendTeleportSuccess(Player player) {
        sendMessage(player, "teleport-success");
    }
    
    public void sendTeleportFailed(Player player) {
        sendMessage(player, "teleport-failed");
    }
    
    public void sendNoPermission(Player player) {
        sendMessage(player, "no-permission");
    }
    
    public void sendPlayerNotFound(Player player, String name) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", name);
        sendMessage(player, "player-not-found", placeholders);
    }
          }
