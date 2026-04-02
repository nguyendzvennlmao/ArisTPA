package me.aris.aristpa;

import me.aris.aristpa.commands.*;
import me.aris.aristpa.gui.TPAGUI;
import me.aris.aristpa.gui.TPAHereGUI;
import me.aris.aristpa.managers.ConfigManager;
import me.aris.aristpa.managers.MessageManager;
import me.aris.aristpa.managers.SoundManager;
import me.aris.aristpa.managers.TPAManager;
import me.aris.aristpa.listeners.TeleportListener;
import org.bukkit.plugin.java.JavaPlugin;

public class ArisTPA extends JavaPlugin {
    private static ArisTPA instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private SoundManager soundManager;
    private TPAManager tpaManager;
    private TPAGUI tpaGUI;
    private TPAHereGUI tpaHereGUI;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        saveResource("message.yml", false);
        saveResource("sound.yml", false);
        saveResource("gui/tpa-gui.yml", false);
        saveResource("gui/tpahere-gui.yml", false);
        
        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        soundManager = new SoundManager(this);
        tpaManager = new TPAManager(this);
        tpaGUI = new TPAGUI(this);
        tpaHereGUI = new TPAHereGUI(this);
        
        registerCommands();
        registerListeners();
        
        getLogger().info("ArisTPA has been enabled!");
    }

    @Override
    public void onDisable() {
        if (tpaManager != null) {
            tpaManager.shutdown();
        }
        getLogger().info("ArisTPA has been disabled!");
    }

    private void registerCommands() {
        getCommand("tpa").setExecutor(new TPACommand(this));
        getCommand("tpahere").setExecutor(new TPAHereCommand(this));
        getCommand("tpaccept").setExecutor(new TPAcceptCommand(this));
        getCommand("tpdeny").setExecutor(new TPDenyCommand(this));
        getCommand("tpacancel").setExecutor(new TPAcancelCommand(this));
        getCommand("tpatoggle").setExecutor(new TPAToggleCommand(this));
        getCommand("tpaheretoggle").setExecutor(new TPAHereToggleCommand(this));
        getCommand("tpauto").setExecutor(new TPAutoCommand(this));
        getCommand("dtpa").setExecutor(new DTPACommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new TeleportListener(this), this);
        getServer().getPluginManager().registerEvents(tpaGUI, this);
        getServer().getPluginManager().registerEvents(tpaHereGUI, this);
    }

    public static ArisTPA getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() { return configManager; }
    public MessageManager getMessageManager() { return messageManager; }
    public SoundManager getSoundManager() { return soundManager; }
    public TPAManager getTPAManager() { return tpaManager; }
    public TPAGUI getTPAGUI() { return tpaGUI; }
    public TPAHereGUI getTPAHereGUI() { return tpaHereGUI; }
  }
