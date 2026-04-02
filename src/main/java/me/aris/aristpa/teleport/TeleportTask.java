package me.aris.aristpa.teleport;

import me.aris.aristpa.ArisTPA;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportTask {
    private ArisTPA plugin;
    private Player player;
    private Location startLocation;
    private Location targetLocation;
    private Runnable onComplete;
    private Runnable onCancel;
    private int countdown;
    private int taskId;
    private boolean cancelled;
    private boolean isFlying;
    
    public TeleportTask(ArisTPA plugin, Player player, Location targetLocation, Runnable onComplete, Runnable onCancel) {
        this.plugin = plugin;
        this.player = player;
        this.startLocation = player.getLocation().clone();
        this.targetLocation = targetLocation;
        this.onComplete = onComplete;
        this.onCancel = onCancel;
        this.countdown = plugin.getConfigManager().getCountdown();
        this.cancelled = false;
        this.isFlying = player.isFlying() || player.isGliding();
    }
    
    public void start() {
        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                if (cancelled || !player.isOnline()) {
                    cancel();
                    return;
                }
                
                if (checkCancellationConditions()) {
                    cancelTeleport();
                    return;
                }
                
                if (countdown <= 0) {
                    executeTeleport();
                    cancel();
                    return;
                }
                
                plugin.getSoundManager().playCountdown(player);
                plugin.getMessageManager().sendTeleportCountdown(player, countdown);
                countdown--;
            }
        }.runTaskTimer(plugin, 0L, 20L).getTaskId();
    }
    
    private boolean checkCancellationConditions() {
        int allowedRange = plugin.getConfigManager().getAllowedWalkRange();
        Location currentLocation = player.getLocation();
        
        if (allowedRange == 0) {
            if (currentLocation.getBlockX() != startLocation.getBlockX() || 
                currentLocation.getBlockZ() != startLocation.getBlockZ()) {
                plugin.getMessageManager().sendTeleportCancelled(player, "movement");
                return true;
            }
        } else if (allowedRange > 0) {
            double distance = startLocation.distance(currentLocation);
            if (distance > allowedRange) {
                plugin.getMessageManager().sendTeleportCancelled(player, "movement");
                return true;
            }
        }
        
        boolean isNowFlying = player.isFlying() || player.isGliding();
        if (!isFlying && isNowFlying) {
            plugin.getMessageManager().sendTeleportCancelled(player, "flying");
            return true;
        }
        
        return false;
    }
    
    private void executeTeleport() {
        if (cancelled || !player.isOnline()) return;
        
        plugin.getTeleportExecutor().teleportPlayer(player, targetLocation);
        if (onComplete != null) {
            onComplete.run();
        }
    }
    
    private void cancelTeleport() {
        cancelled = true;
        if (onCancel != null) {
            onCancel.run();
        }
        cancel();
    }
    
    public void cancel() {
        cancelled = true;
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (Bukkit.getScheduler().isCurrentlyRunning(taskId)) {
                Bukkit.getScheduler().cancelTask(taskId);
            }
        });
    }
    
    public boolean isCancelled() { return cancelled; }
            }
