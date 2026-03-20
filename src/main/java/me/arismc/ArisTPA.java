package me.arismc;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArisTPA extends JavaPlugin {
    private static ArisTPA instance;
    private FileConfiguration messages, sounds, tpaGui, tpahereGui;
    private final Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");

    @Override
    public void onEnable() {
        instance = this;
        
        // Tạo cấu trúc folder và file giống Donut-TPA
        saveDefaultConfig();
        setupFolders();

        getLogger().info("ArisTPA v1.0.7 by VennLMAO enabled!");
    }

    private void setupFolders() {
        File guiDir = new File(getDataFolder(), "gui");
        if (!guiDir.exists()) guiDir.mkdirs();

        String[] resources = {
            "messages.yml", 
            "sounds.yml", 
            "gui/tpa-gui.yml", 
            "gui/tpahere-gui.yml"
        };

        for (String res : resources) {
            File file = new File(getDataFolder(), res);
            if (!file.exists()) {
                saveResource(res, false);
            }
        }
        
        // Load configs
        messages = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "messages.yml"));
        tpaGui = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "gui/tpa-gui.yml"));
        tpahereGui = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "gui/tpahere-gui.yml"));
    }

    public String format(String msg) {
        if (msg == null) return "";
        Matcher matcher = hexPattern.matcher(msg.replace("|", "\n"));
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, ChatColor.of("#" + matcher.group(1)).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(sb).toString());
    }

    public static ArisTPA getInstance() { return instance; }
              }
