package me.aris.aristpa.listeners;

import me.aris.aristpa.ArisTPA;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {
    private ArisTPA plugin;
    
    public TeleportListener(ArisTPA plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.getTeleportExecutor().isTeleporting(player)) {
            int allowedRange = plugin.getConfigManager().getAllowedWalkRange();
            if (allowedRange == 0) {
                if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                    event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                    plugin.getTeleportExecutor().cancelTeleport(player);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (plugin.getTeleportExecutor().isTeleporting(player)) {
            plugin.getTeleportExecutor().cancelTeleport(player);
        }
        plugin.getTPAManager().removeAllRequestsFrom(player);
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (plugin.getTeleportExecutor().isTeleporting(event.getPlayer())) {
            plugin.getTeleportExecutor().cancelTeleport(event.getPlayer());
        }
    }
  }
