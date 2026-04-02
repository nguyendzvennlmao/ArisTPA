package me.aris.aristpa.managers;

import me.aris.aristpa.ArisTPA;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.List;

public class ConfigManager {
    private ArisTPA plugin;
    private FileConfiguration config;
    private FileConfiguration guiConfig;
    private FileConfiguration guiHereConfig;
    
    public ConfigManager(ArisTPA plugin) {
        this.plugin = plugin;
        reloadConfigs();
    }
    
    public void reloadConfigs() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        File guiFile = new File(plugin.getDataFolder(), "gui/tpa-gui.yml");
        if (guiFile.exists()) {
            guiConfig = YamlConfiguration.loadConfiguration(guiFile);
        }
        
        File guiHereFile = new File(plugin.getDataFolder(), "gui/tpahere-gui.yml");
        if (guiHereFile.exists()) {
            guiHereConfig = YamlConfiguration.loadConfiguration(guiHereFile);
        }
    }
    
    public int getCountdown() { return config.getInt("teleport.countdown", 5); }
    public int getAllowedWalkRange() { return config.getInt("teleport.allowed-walk-range", 0); }
    public boolean isSafeTeleport() { return config.getBoolean("teleport.safe-teleport", false); }
    public int getExpirationTime() { return config.getInt("request.expiration-time", 120); }
    public int getCooldown() { return config.getInt("request.cooldown", 30); }
    public int getDailyLimit() { return config.getInt("request.daily-limit", -1); }
    public boolean isBlacklistedWorldsEnabled() { return config.getBoolean("blacklisted-worlds.enabled", true); }
    public List<String> getBlacklistedWorlds() { return config.getStringList("blacklisted-worlds.worlds"); }
    public boolean isGUIEnabled() { return config.getBoolean("gui.enabled", true); }
    public boolean isCommandEnabled() { return config.getBoolean("gui.command-enabled", false); }
    public boolean isActionBarMessages() { return config.getBoolean("messages.action-bar-messages", true); }
    public boolean isChatMessages() { return config.getBoolean("messages.chat-messages", true); }
    public boolean isUseActionBarOnly() { return config.getBoolean("messages.use-action-bar-only", false); }
    public int getThreadPoolSize() { return config.getInt("storage.thread-pool-size", 2); }
    public boolean isAllowWildernessTeleport() { return config.getBoolean("huskclaims.allow-wilderness-teleport", true); }
    public boolean isOverrideEssentials() { return config.getBoolean("override-essentials", true); }
    
    public FileConfiguration getGUIConfig() { return guiConfig; }
    public FileConfiguration getGUIHereConfig() { return guiHereConfig; }
    }
