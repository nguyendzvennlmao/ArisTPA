package me.aris.aristpa.managers;

import me.aris.aristpa.ArisTPA;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;

public class SoundManager {
    private ArisTPA plugin;
    private FileConfiguration sounds;
    
    public SoundManager(ArisTPA plugin) {
        this.plugin = plugin;
        loadSounds();
    }
    
    public void loadSounds() {
        File soundFile = new File(plugin.getDataFolder(), "sound.yml");
        if (soundFile.exists()) {
            sounds = YamlConfiguration.loadConfiguration(soundFile);
        } else {
            sounds = new YamlConfiguration();
        }
    }
    
    private Sound getSound(String path) {
        String soundName = sounds.getString("sounds." + path, "ENTITY_EXPERIENCE_ORB_PICKUP");
        try {
            return Sound.valueOf(soundName);
        } catch (IllegalArgumentException e) {
            return Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        }
    }
    
    public void playRequestSent(Player player) {
        if (player != null) player.playSound(player.getLocation(), getSound("request-sent"), 1.0f, 1.0f);
    }
    
    public void playRequestReceived(Player player) {
        if (player != null) player.playSound(player.getLocation(), getSound("request-received"), 1.0f, 1.0f);
    }
    
    public void playCountdown(Player player) {
        if (player != null) player.playSound(player.getLocation(), getSound("countdown"), 1.0f, 1.0f);
    }
    
    public void playTeleport(Player player) {
        if (player != null) player.playSound(player.getLocation(), getSound("teleport"), 1.0f, 1.0f);
    }
    
    public void playError(Player player) {
        if (player != null) player.playSound(player.getLocation(), getSound("error"), 1.0f, 1.0f);
    }
    
    public void playAccept(Player player) {
        if (player != null) player.playSound(player.getLocation(), getSound("accept"), 1.0f, 1.0f);
    }
    
    public void playCancel(Player player) {
        if (player != null) player.playSound(player.getLocation(), getSound("cancel"), 1.0f, 1.0f);
    }
    
    public void playTPAutoEnable(Player player) {
        if (player != null) player.playSound(player.getLocation(), getSound("tpauto-enable"), 1.0f, 1.0f);
    }
    
    public void playTPAutoDisable(Player player) {
        if (player != null) player.playSound(player.getLocation(), getSound("tpauto-disable"), 1.0f, 1.0f);
    }
    }
