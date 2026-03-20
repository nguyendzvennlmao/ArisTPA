package me.arismc;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class ArisTPA extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupFolders();
        getLogger().info("ArisTPA v1.0.7 by VennLMAO enabled!");
    }

    private void setupFolders() {
        File guiDir = new File(getDataFolder(), "gui");
        if (!guiDir.exists()) guiDir.mkdirs();

        String[] resources = {"messages.yml", "sounds.yml", "gui/tpa-gui.yml", "gui/tpahere-gui.yml"};
        for (String res : resources) {
            File file = new File(getDataFolder(), res);
            if (!file.exists()) saveResource(res, false);
        }
    }
}
